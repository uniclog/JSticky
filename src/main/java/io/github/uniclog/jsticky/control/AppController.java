package io.github.uniclog.jsticky.control;

import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static io.github.uniclog.jsticky.MainApp.controlService;

public class AppController {
    public TextArea mainTextArea;
    public ToggleButton exit;
    public ToggleButton fix;
    public ToggleButton wrap;

    private static boolean alwaysOnTop = false;
    private static boolean onWrap = false;

    /**
     * Controller post construct
     */
    public void initialize() {
        exit.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        mainTextArea.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        fix.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
    }

    /**
     * Button: Exit
     */
    public void onExit() {
        SceneControlService.onExit();
    }

    /**
     * Button: Minimize window
     */
    public void onMin() {

        SceneControlService.onMin((Stage) exit.getScene().getWindow());
    }

    /**
     * Button: Window pin
     */
    public void onFix() {
        var stage = (Stage) exit.getScene().getWindow();
        if (alwaysOnTop) {
            stage.setAlwaysOnTop(false);
            fix.getStyleClass().removeAll("gui-fix-button-on");
            fix.getStyleClass().add("gui-fix-button");
        } else {
            stage.setAlwaysOnTop(true);
            fix.getStyleClass().removeAll("gui-fix-button");
            fix.getStyleClass().add("gui-fix-button-on");
        }
        alwaysOnTop = !alwaysOnTop;
    }

    /**
     * Button: Window pin
     */
    public void onWrap() {
        if (onWrap) {
            wrap.getStyleClass().removeAll("gui-wrap-button-on");
            wrap.getStyleClass().add("gui-wrap-button");
        } else {
            wrap.getStyleClass().removeAll("gui-wrap-button");
            wrap.getStyleClass().add("gui-wrap-button-on");
        }
        onWrap = !onWrap;
        mainTextArea.setWrapText(onWrap);
    }
}