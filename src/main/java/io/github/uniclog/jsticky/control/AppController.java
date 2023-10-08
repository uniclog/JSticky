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

    private final Text holder = new Text();
    private double oldHeight = 0;
    private static boolean alwaysOnTop = false;

    /**
     * Controller post construct
     */
    public void initialize() {
        holder.textProperty().bind(mainTextArea.textProperty());
        holder.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            if (oldHeight != newValue.getHeight()) {
                oldHeight = newValue.getHeight();
                var newHeight = holder.getLayoutBounds().getHeight() + 20;
                mainTextArea.setPrefHeight(newHeight);
                Stage stage = (Stage) mainTextArea.getScene().getWindow();
            }
        });

        exit.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
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
}