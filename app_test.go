package main

import (
	"path/filepath"
	"testing"

	"github.com/wailsapp/wails/v3/pkg/application"
)

func TestCSSColorConvertsJavaFXValue(t *testing.T) {
	if got := cssColor("0xffccffff", "#000000"); got != "#ffccff" {
		t.Fatalf("cssColor() = %q, want #ffccff", got)
	}
}

func TestNormaliseRepairsInvalidValues(t *testing.T) {
	s := State{Settings: Settings{TextSize: 2}, WindowSettings: WindowSettings{Width: 1, Height: 1, Opacity: 0}}
	normalise(&s)
	if s.Settings.TextSize != 12 || s.WindowSettings.Width != 310 || s.WindowSettings.Height != 280 || s.WindowSettings.Opacity != 1 {
		t.Fatalf("normalise() did not restore defaults: %#v", s)
	}
}

func TestNormaliseKeepsJavaFXMinimumOpacity(t *testing.T) {
	s := defaultState()
	s.WindowSettings.Opacity = 0.004
	normalise(&s)
	if s.WindowSettings.Opacity != 0.004 {
		t.Fatalf("normalise() opacity = %v, want 0.004", s.WindowSettings.Opacity)
	}
}

func TestPersistWithoutWindow(t *testing.T) {
	a := &App{state: defaultState(), configPath: filepath.Join(t.TempDir(), "jSticky.json")}
	a.SaveMainGeometry()
}

func TestWindowPositionVisible(t *testing.T) {
	screens := []*application.Screen{
		{WorkArea: application.Rect{X: 0, Y: 0, Width: 1920, Height: 1040}},
		{WorkArea: application.Rect{X: -1280, Y: 0, Width: 1280, Height: 1024}},
	}
	if !windowPositionVisible(-500, 100, 310, 280, screens) {
		t.Fatal("a window on a monitor left of the primary should be visible")
	}
	if windowPositionVisible(2500, 100, 310, 280, screens) {
		t.Fatal("a window outside every active screen should not be visible")
	}
	if windowPositionVisible(-32000, -32000, 310, 280, screens) {
		t.Fatal("minimised Windows coordinates should not be restored")
	}
}
