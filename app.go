package main

import (
	"bytes"
	"encoding/base64"
	"encoding/json"
	"errors"
	"image"
	"image/draw"
	"image/png"
	"os"
	"path/filepath"
	"strings"
	"sync"
	"time"

	"github.com/kbinani/screenshot"
	"github.com/wailsapp/wails/v3/pkg/application"
)

type Settings struct {
	SpellCheck     bool   `json:"spellCheck"`
	TextWrap       bool   `json:"textWrap"`
	TextSize       int    `json:"textSize"`
	TextFontFamily string `json:"textFontFamily"`
	AppThemeColor  string `json:"appThemeColor"`
	TextFontColor  string `json:"textFontColor"`
}

type WindowSettings struct {
	Width             int     `json:"width"`
	Height            int     `json:"height"`
	PosX              *int    `json:"posX,omitempty"`
	PosY              *int    `json:"posY,omitempty"`
	Opacity           float64 `json:"opacity"`
	MouseHoverOpacity bool    `json:"mouseHoverOpacity"`
}

type State struct {
	Settings       Settings       `json:"settings"`
	WindowSettings WindowSettings `json:"windowSettings"`
	Content        string         `json:"content"`
}

type Capture struct {
	DataURL string `json:"dataURL"`
	X       int    `json:"x"`
	Y       int    `json:"y"`
	Width   int    `json:"width"`
	Height  int    `json:"height"`
}

// App is the Wails service shared by the note, settings and capture windows.
type App struct {
	mu          sync.Mutex
	state       State
	configPath  string
	application *application.App
	mainWindow  application.Window
	capture     Capture
}

func defaultState() State {
	return State{
		Settings:       Settings{TextSize: 12, TextFontFamily: "System", AppThemeColor: "#FFEB82", TextFontColor: "#000000"},
		WindowSettings: WindowSettings{Width: 310, Height: 280, Opacity: 1},
	}
}

func NewApp() *App {
	a := &App{state: defaultState()}
	configDir, err := os.UserConfigDir()
	if err != nil {
		configDir = "."
	}
	a.configPath = filepath.Join(configDir, "JStickyGo", "jSticky.json")
	a.load()
	return a
}

func (a *App) attach(app *application.App, mainWindow application.Window) {
	a.application = app
	a.mainWindow = mainWindow
}

func (a *App) load() {
	paths := []string{a.configPath, "jSticky.ini", filepath.Join("..", "JSticky", "jSticky.ini")}
	for _, path := range paths {
		data, err := os.ReadFile(path)
		if err != nil {
			continue
		}
		candidate := defaultState()
		if json.Unmarshal(data, &candidate) == nil {
			normalise(&candidate)
			a.state = candidate
			if path != a.configPath {
				_ = a.persist()
			}
			return
		}
	}
}

func normalise(s *State) {
	if s.Settings.TextSize < 8 || s.Settings.TextSize > 72 {
		s.Settings.TextSize = 12
	}
	if s.Settings.TextFontFamily == "" {
		s.Settings.TextFontFamily = "System"
	}
	s.Settings.AppThemeColor = cssColor(s.Settings.AppThemeColor, "#FFEB82")
	s.Settings.TextFontColor = cssColor(s.Settings.TextFontColor, "#000000")
	if s.WindowSettings.Width < 152 {
		s.WindowSettings.Width = 310
	}
	if s.WindowSettings.Height < 74 {
		s.WindowSettings.Height = 280
	}
	// JavaFX used 0.004 as the minimum: the note becomes practically
	// invisible but still keeps a hit-testable window for hover restore.
	if s.WindowSettings.Opacity < 0.004 || s.WindowSettings.Opacity > 1 {
		s.WindowSettings.Opacity = 1
	}
}

func cssColor(value, fallback string) string {
	v := strings.TrimSpace(value)
	if strings.HasPrefix(v, "0x") && len(v) >= 8 {
		v = "#" + v[2:8]
	}
	if len(v) == 9 && strings.HasPrefix(v, "#") {
		v = v[:7]
	}
	if len(v) != 7 || v[0] != '#' {
		return fallback
	}
	return v
}

func (a *App) GetState() State {
	a.mu.Lock()
	defer a.mu.Unlock()
	return a.state
}

func (a *App) SaveState(state State) error {
	normalise(&state)
	a.mu.Lock()
	a.state.Settings = state.Settings
	a.state.Content = state.Content
	a.state.WindowSettings.Opacity = state.WindowSettings.Opacity
	a.state.WindowSettings.MouseHoverOpacity = state.WindowSettings.MouseHoverOpacity
	a.captureMainGeometryLocked()
	err := a.persist()
	a.mu.Unlock()
	if err == nil && a.application != nil {
		a.application.Event.Emit("state-changed", state)
	}
	return err
}

func (a *App) SaveMainGeometry() {
	a.mu.Lock()
	defer a.mu.Unlock()
	a.captureMainGeometryLocked()
	_ = a.persist()
}

func (a *App) captureMainGeometryLocked() {
	if a.mainWindow == nil || a.mainWindow.IsMinimised() {
		return
	}
	w, h := a.mainWindow.Size()
	x, y := a.mainWindow.Position()
	if a.application != nil && !windowPositionVisible(x, y, w, h, a.application.Screen.GetAll()) {
		return
	}
	if w > 0 {
		a.state.WindowSettings.Width = w
	}
	if h > 0 {
		a.state.WindowSettings.Height = h
	}
	a.state.WindowSettings.PosX, a.state.WindowSettings.PosY = &x, &y
}

func (a *App) persist() error {
	if err := os.MkdirAll(filepath.Dir(a.configPath), 0o755); err != nil {
		return err
	}
	data, err := json.MarshalIndent(a.state, "", "  ")
	if err != nil {
		return err
	}
	tmp := a.configPath + ".tmp"
	if err = os.WriteFile(tmp, data, 0o600); err != nil {
		return err
	}
	return os.Rename(tmp, a.configPath)
}

func (a *App) SetAlwaysOnTop(value bool) {
	if a.mainWindow != nil {
		a.mainWindow.SetAlwaysOnTop(value)
	}
}

// SetWindowOpacity applies native window alpha when supported. It returns
// false when the frontend should use its CSS fallback.
func (a *App) SetWindowOpacity(name string, opacity float64) bool {
	if a.application == nil {
		return false
	}
	window, ok := a.application.Window.GetByName(name)
	if !ok {
		return false
	}
	return setNativeWindowOpacity(window, opacity)
}

func (a *App) Minimise() {
	if a.mainWindow != nil {
		a.mainWindow.Minimise()
	}
}

func (a *App) Quit() {
	a.SaveMainGeometry()
	if a.application != nil {
		a.application.Quit()
	}
}

func (a *App) OpenSettings() {
	if a.application == nil {
		return
	}
	if window, ok := a.application.Window.GetByName("settings"); ok {
		window.Show().Focus()
		return
	}
	a.application.Window.NewWithOptions(application.WebviewWindowOptions{
		Name: "settings", Title: "JSticky Settings", URL: "/?view=settings",
		Width: 273, Height: 432, MinWidth: 273, MinHeight: 360,
		Frameless: true, BackgroundType: application.BackgroundTypeTransparent,
		BackgroundColour: application.NewRGBA(0, 0, 0, 0),
	}).Show()
}

func (a *App) OpenCapture() error {
	if a.application == nil {
		return errors.New("application is not ready")
	}
	if window, ok := a.application.Window.GetByName("capture"); ok {
		window.Show().Focus()
		return nil
	}
	if a.mainWindow != nil {
		a.mainWindow.Hide()
		time.Sleep(80 * time.Millisecond)
	}
	capture, err := captureDesktop()
	if a.mainWindow != nil {
		a.mainWindow.Show()
	}
	if err != nil {
		return err
	}
	a.mu.Lock()
	a.capture = capture
	a.mu.Unlock()
	a.application.Window.NewWithOptions(application.WebviewWindowOptions{
		Name: "capture", Title: "JSticky Screen Capture", URL: "/?view=capture",
		Width: 556, Height: 290, MinWidth: 260, MinHeight: 120,
		Frameless: true, BackgroundType: application.BackgroundTypeTransparent,
		BackgroundColour: application.NewRGBA(0, 0, 0, 0),
	}).Show().Focus()
	return nil
}

func (a *App) GetCapture() Capture {
	a.mu.Lock()
	defer a.mu.Unlock()
	return a.capture
}

func captureDesktop() (Capture, error) {
	count := screenshot.NumActiveDisplays()
	if count == 0 {
		return Capture{}, errors.New("active displays not found")
	}
	bounds := screenshot.GetDisplayBounds(0)
	for i := 1; i < count; i++ {
		bounds = bounds.Union(screenshot.GetDisplayBounds(i))
	}
	canvas := image.NewRGBA(image.Rect(0, 0, bounds.Dx(), bounds.Dy()))
	for i := range count {
		b := screenshot.GetDisplayBounds(i)
		img, err := screenshot.CaptureRect(b)
		if err != nil {
			return Capture{}, err
		}
		target := image.Rect(b.Min.X-bounds.Min.X, b.Min.Y-bounds.Min.Y, b.Max.X-bounds.Min.X, b.Max.Y-bounds.Min.Y)
		draw.Draw(canvas, target, img, image.Point{}, draw.Src)
	}
	var buf bytes.Buffer
	if err := png.Encode(&buf, canvas); err != nil {
		return Capture{}, err
	}
	return Capture{DataURL: "data:image/png;base64," + base64.StdEncoding.EncodeToString(buf.Bytes()), X: bounds.Min.X, Y: bounds.Min.Y, Width: bounds.Dx(), Height: bounds.Dy()}, nil
}
