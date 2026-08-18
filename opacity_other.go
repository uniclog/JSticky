//go:build !windows

package main

import "github.com/wailsapp/wails/v3/pkg/application"

// Other platforms keep the CSS fallback until Wails exposes a portable API.
func setNativeWindowOpacity(window application.Window, opacity float64) bool {
	return false
}
