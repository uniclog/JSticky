package io.github.uniclog.jsticky.utils;


import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ClipboardUtils {
    public static final Clipboard clipboard = Clipboard.getSystemClipboard();

    public static void copyToClipboard(Image image) {
        ClipboardContent content = new ClipboardContent();
        content.putImage(image);
        clipboard.setContent(content);
    }
}
