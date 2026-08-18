//go:build windows

package main

import (
	"os"
	"path/filepath"
)

// cleanupLegacyWebviewData removes the cache directory created by Wails when
// WebviewUserDataPath was not configured. WebView2 recreates this cache under
// the unified JStickyGo directory on the current launch.
func cleanupLegacyWebviewData() {
	configRoot, err := os.UserConfigDir()
	if err != nil {
		return
	}
	legacyPath := filepath.Join(configRoot, "JStickyGo.exe")
	if filepath.Base(legacyPath) != "JStickyGo.exe" {
		return
	}
	_ = os.RemoveAll(legacyPath)
}
