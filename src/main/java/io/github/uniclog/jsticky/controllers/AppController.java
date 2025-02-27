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
import org.fxmisc.richtext.InlineCssTextArea;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static io.github.uniclog.jsticky.App.*;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class AppController implements ControllersInterface {
    private static boolean alwaysOnTop = false;
    private static boolean onWrap = false;

    public InlineCssTextArea mainTextArea;
    public ToggleButton exit;
    public ToggleButton fix;
    public ToggleButton wrap;
    public ToggleButton settings;
    public AnchorPane mainPane;
    private Stage stage;

    private final Set<String> dictionary = new HashSet<>();

    @Override
    public void setStageAndSetupListeners(Parent root, Scene scene, Stage stage) {
        this.stage = stage;
        setupRootListeners(root, scene, stage);
        exit.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        mainTextArea.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        mainTextArea.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.SPACE || keyEvent.getCode() == KeyCode.ENTER
                    || keyEvent.getCode() == KeyCode.PERIOD || keyEvent.getCode() == KeyCode.DECIMAL) {
                mainTextAreaStyleReload();
            }
        });
        mainTextArea.setOnMouseClicked(event -> {
            var actual = mainTextArea.getText();
            if (!actual.equals(jStickyData.getContent())) {
                jStickyData.setContent(mainTextArea.getText());
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
    public void initialize() throws IOException, URISyntaxException {
        if (nonNull(jStickyData) && nonNull(jStickyData.getContent())) {
            mainTextArea.appendText(jStickyData.getContent());
        }
        // add dictionaries
        dictionary.addAll(Files.readAllLines(Path.of(requireNonNull(App.class.getResource("dic/ru_RU.dic")).toURI())));
        dictionary.addAll(Files.readAllLines(Path.of(requireNonNull(App.class.getResource("dic/en_US.dic")).toURI())));
        dictionary.addAll(Files.readAllLines(Path.of(requireNonNull(App.class.getResource("dic/ru_spec.dic")).toURI())));
    }

    public void settingsReload() {
        var settings = jStickyData.getSettings();
        var windowSettings = jStickyData.getWindowSettings();

        mainTextAreaStyleReload();

        mainPane.setStyle(format("-fx-background-color: %s ;", settings.getAppThemeColorText()));
        stage.setOpacity(windowSettings.getOpacity());
    }

    private void mainTextAreaStyleReload() {
        var settings = jStickyData.getSettings();
        mainTextArea.setWrapText(settings.getTextWrap());
        mainTextArea.setStyle(0, mainTextArea.getLength(), format(
                " -fx-fill: '%s'; -fx-font-family: '%s'; -fx-font-size: %dpx;",
                settings.getTextFontColorText(),
                settings.getTextFontFamily(),
                settings.getTextSize()));
        mainTextArea.setStyle(format("-fx-background-color: '%s'; ", settings.getAppThemeColorText2()));
        highlightErrors(mainTextArea);
    }

    public void highlightErrors(InlineCssTextArea textArea) {
        var settings = jStickyData.getSettings();
        String text = textArea.getText();
        String[] words = text.split("[^a-zA-Zа-яА-Я]+");
        int lastIndex = 0;
        for (String word : words) {
            word = word.replaceAll("[^a-zA-Zа-яА-Я]", "");
            if (!word.isEmpty()) {
                int startIndex = text.indexOf(word, lastIndex);
                lastIndex = startIndex + word.length();
                if (!dictionary.contains(word.toLowerCase())) {
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