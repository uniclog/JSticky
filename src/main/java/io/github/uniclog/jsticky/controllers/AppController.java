package io.github.uniclog.jsticky.controllers;

import io.github.uniclog.jsticky.App;
import io.github.uniclog.jsticky.controllers.services.SceneControlService;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.IOException;

import static io.github.uniclog.jsticky.App.*;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

public class AppController implements ControllersInterface {
    private static boolean alwaysOnTop = false;
    private static boolean onWrap = false;

    public AnchorPane mainTextAreaContainer;
    private InlineCssTextArea mainTextArea;
    private VirtualizedScrollPane<InlineCssTextArea> mainTextAreaScrollPane;
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
        mainTextArea.setOnKeyReleased(keyEvent -> {
            if (DICTIONARY == null) {
                return;
            }
            if (keyEvent.getCode() == KeyCode.SPACE || keyEvent.getCode() == KeyCode.ENTER
                    || keyEvent.getCode() == KeyCode.PERIOD || keyEvent.getCode() == KeyCode.DECIMAL) {
                mainTextAreaStyleReload();
            }
        });
        mainTextArea.setOnMouseClicked(event -> {
            if (DICTIONARY == null) {
                return;
            }
            var actual = mainTextArea.getText();
            if (!actual.equals(J_STICKY_DATA.getContent())) {
                J_STICKY_DATA.setContent(mainTextArea.getText());
            }
            mainTextAreaStyleReload();
        });
        fix.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        settingsReload();
        addMouseHoverOpacityListeners(scene, stage);
        stage.setOnCloseRequest(event -> App.close());
    }

    /**
     * Controller post construct
     */
    public void initialize() {
        mainTextArea = new InlineCssTextArea();
        mainTextAreaScrollPane = new VirtualizedScrollPane<>(mainTextArea);
        mainTextAreaScrollPane.getStyleClass().add("text-area-style");
        AnchorPane.setTopAnchor(mainTextAreaScrollPane, 0.0);
        AnchorPane.setBottomAnchor(mainTextAreaScrollPane, 0.0);
        AnchorPane.setLeftAnchor(mainTextAreaScrollPane, 0.0);
        AnchorPane.setRightAnchor(mainTextAreaScrollPane, 0.0);
        mainTextAreaContainer.getChildren().add(mainTextAreaScrollPane);

        if (nonNull(J_STICKY_DATA) && nonNull(J_STICKY_DATA.getContent())) {
            mainTextArea.appendText(J_STICKY_DATA.getContent());
        }
    }

    public void settingsReload() {
        var settings = J_STICKY_DATA.getSettings();
        var windowSettings = J_STICKY_DATA.getWindowSettings();

        mainTextAreaStyleReload();
        mainPane.setStyle(format("-fx-background-color: %s ;", settings.getAppThemeColorText()));
        mainTextAreaScrollPane.setStyle(format("-fx-background-color: %s ;", settings.getAppThemeColorText2()));
        stage.setOpacity(windowSettings.getOpacity());
    }

    private void mainTextAreaStyleReload() {
        var settings = J_STICKY_DATA.getSettings();
        mainTextArea.setWrapText(settings.getTextWrap());
        mainTextArea.setStyle(0, mainTextArea.getLength(), format(
                " -fx-fill: '%s'; -fx-font-family: '%s'; -fx-font-size: %dpx;",
                settings.getTextFontColorText(),
                settings.getTextFontFamily(),
                settings.getTextSize()));
        mainTextArea.setStyle(format("-fx-background-color: '%s'; ", settings.getAppThemeColorText2()));

        if (settings.getSpellCheck()) {
            highlightErrors(mainTextArea);
        }
    }

    private void highlightErrors(InlineCssTextArea textArea) {
        var settings = J_STICKY_DATA.getSettings();
        String text = textArea.getText();
        String[] words = text.split("[^a-zA-Zа-яА-ЯёЁ]+");
        int lastIndex = 0;
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Zа-яА-ЯёЁ]", "");
            if (!word.isEmpty()) {
                int startIndex = text.indexOf(word, lastIndex);
                lastIndex = startIndex + word.length();
                if (DICTIONARY != null && !DICTIONARY.contains(word.toLowerCase())) {
                    int endIndex = startIndex + word.length();
                    textArea.setStyle(startIndex, endIndex, format(
                            "-fx-underline: true; -fx-fill: '%s'; -fx-font-family: '%s'; -fx-font-size: %dpx;",
                            settings.getTextFontColorAsString2(),
                            settings.getTextFontFamily(),
                            settings.getTextSize()));
                }
            }
        }
    }

    /**
     * Button: Exit
     */
    public void onExit() {
        var actual = mainTextArea.getText();
        if (!actual.equals(J_STICKY_DATA.getContent())) {
            J_STICKY_DATA.setContent(actual);
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