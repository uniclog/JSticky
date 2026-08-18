//go:build windows

package main

import (
	"math"

	"github.com/wailsapp/wails/v3/pkg/application"
	"github.com/wailsapp/wails/v3/pkg/w32"
)

// setNativeWindowOpacity mirrors JavaFX Stage.setOpacity. Applying alpha to
// the HWND avoids WebView2 compositing its transparent surface against grey.
func setNativeWindowOpacity(window application.Window, opacity float64) bool {
	if window == nil || window.NativeWindow() == nil {
		return false
	}
	if opacity < 0.004 {
		opacity = 0.004
	}
	if opacity > 1 {
		opacity = 1
	}

	hwnd := w32.HWND(uintptr(window.NativeWindow()))
	exStyle := uint32(w32.GetWindowLong(hwnd, w32.GWL_EXSTYLE))
	if exStyle&w32.WS_EX_LAYERED == 0 {
		w32.SetWindowLong(hwnd, w32.GWL_EXSTYLE, exStyle|w32.WS_EX_LAYERED)
	}
	alpha := byte(math.Round(opacity * 255))
	if alpha == 0 {
		alpha = 1
	}
	return w32.SetLayeredWindowAttributes(hwnd, 0, alpha, w32.LWA_ALPHA)
}
