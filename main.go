package main

import (
	"embed"
	"log"
	"path/filepath"

	"github.com/wailsapp/wails/v3/pkg/application"
	"github.com/wailsapp/wails/v3/pkg/events"
)

//go:embed all:frontend/dist
var assets embed.FS

func init() {
	application.RegisterEvent[State]("state-changed")
}

func main() {
	service := NewApp()
	cleanupLegacyWebviewData()
	app := application.New(application.Options{
		Name: "JStickyGo", Description: "JStickyGo Application",
		Services: []application.Service{application.NewService(service)},
		Assets:   application.AssetOptions{Handler: application.AssetFileServerFS(assets)},
		Mac:      application.MacOptions{ApplicationShouldTerminateAfterLastWindowClosed: true},
		Windows:  application.WindowsOptions{WebviewUserDataPath: filepath.Dir(service.configPath)},
	})

	ws := service.state.WindowSettings
	windowOptions := application.WebviewWindowOptions{
		Name: "main", Title: "JStickyGo", URL: "/", Width: ws.Width, Height: ws.Height,
		MinWidth: 152, MinHeight: 74, Frameless: true,
		BackgroundType:   application.BackgroundTypeTransparent,
		BackgroundColour: application.NewRGBA(0, 0, 0, 0),
	}
	if ws.PosX != nil && ws.PosY != nil && storedPositionPlausible(*ws.PosX, *ws.PosY) {
		windowOptions.InitialPosition = application.WindowXY
		windowOptions.X, windowOptions.Y = *ws.PosX, *ws.PosY
	}
	mainWindow := app.Window.NewWithOptions(windowOptions)
	service.attach(app, mainWindow)
	app.Event.OnApplicationEvent(events.Common.ApplicationStarted, func(event *application.ApplicationEvent) {
		if ws.PosX != nil && ws.PosY != nil && !windowPositionVisible(*ws.PosX, *ws.PosY, ws.Width, ws.Height, app.Screen.GetAll()) {
			mainWindow.Center()
			service.SaveMainGeometry()
		}
	})
	mainWindow.RegisterHook(events.Common.WindowClosing, func(event *application.WindowEvent) {
		service.SaveMainGeometry()
	})

	if err := app.Run(); err != nil {
		log.Fatal(err)
	}
}

func storedPositionPlausible(x, y int) bool {
	// Windows uses coordinates around -32000 for minimised windows.
	return x > -30000 && y > -30000
}

func windowPositionVisible(x, y, width, height int, screens []*application.Screen) bool {
	if !storedPositionPlausible(x, y) || width <= 0 || height <= 0 {
		return false
	}
	if len(screens) == 0 {
		return true
	}

	const minimumVisible = 32
	for _, screen := range screens {
		area := screen.WorkArea
		visibleWidth := min(x+width, area.X+area.Width) - max(x, area.X)
		visibleHeight := min(y+height, area.Y+area.Height) - max(y, area.Y)
		if visibleWidth >= minimumVisible && visibleHeight >= minimumVisible {
			return true
		}
	}
	return false
}
