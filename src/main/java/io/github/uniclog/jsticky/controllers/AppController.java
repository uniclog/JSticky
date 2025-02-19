package io.github.uniclog.jsticky.controllers;

import io.github.uniclog.jsticky.App;
import io.github.uniclog.jsticky.controllers.services.SceneControlService;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

import static io.github.uniclog.jsticky.App.*;
import static java.util.Objects.nonNull;

public class AppController implements ControllersInterface {
    private static boolean alwaysOnTop = false;
    private static boolean onWrap = false;
    public TextArea mainTextArea;
    public ToggleButton exit;
    public ToggleButton fix;
    public ToggleButton wrap;
    public ToggleButton settings;
    public AnchorPane mainPane;
    private Stage stage;

    @Override
    public void setStageAndSetupListeners(Parent root, Scene scene, Stage stage) {
        this.stage = stage;
        setupRootListeners(root, scene, stage);
        exit.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        mainTextArea.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        fix.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        settingsReload();
        addMouseHoverOpacityListeners(scene, stage);
        stage.setOnCloseRequest(event -> App.close());
    }

    /**
     * Controller post construct
     */
    public void initialize() {
        if (nonNull(jStickyData) && nonNull(jStickyData.getContent())) {
            mainTextArea.setText(jStickyData.getContent());
        }
        mainTextArea.setOnKeyReleased(event -> jStickyData.setContent(mainTextArea.getText()));
    }

    public void settingsReload() {
        var settings = jStickyData.getSettings();
        var windowSettings = jStickyData.getWindowSettings();

        mainTextArea.setFont(new Font(
                settings.getTextFontFamily(),
                settings.getTextSize()
        ));
        mainTextArea.setWrapText(settings.getTextWrap());
        mainTextArea.setStyle(String.format(
                "-fx-background-color: %s ; -fx-text-fill: %s ;",
                settings.getAppThemeColorText2(),
                settings.getTextFontColorText()));
        mainPane.setStyle(String.format("-fx-background-color: %s ;", settings.getAppThemeColorText()));
        stage.setOpacity(windowSettings.getOpacity());
    }

    /**
     * Button: Exit
     */
    public void onExit() {
        var actual = mainTextArea.getText();
        if (!actual.equals(jStickyData.getContent())) {
            jStickyData.setContent(actual);
        }
        App.close();
    }

    /**
     * Button: Minimize window
     */
    public void onMin() {
        SceneControlService.onMin(stage);
    }

    /**
     * Button: Window pin
     */
    public void onFix() {
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


    /**
     * Button: App settings
     */
    public void onSettings() {
        Platform.runLater(() -> {
            try {
                loadSettingsScene();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void openList() {
        /*Platform.runLater(() -> {
            try {
                loadListScene();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });*/
    }

    public void onScreenCapture() {
        Platform.runLater(() -> {
            try {
                loadScreenCapScene();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}