package io.github.uniclog.jsticky.controllers;

import io.github.uniclog.jsticky.App;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static io.github.uniclog.jsticky.App.J_STICKY_DATA;
import static java.lang.String.format;

public class SettingsController implements ControllersInterface {
    public ToggleButton exit;
    public ChoiceBox<String> choiceTextFamily;
    public CheckBox checkWrap;
    public CheckBox spellCheck;
    public CheckBox mouseHoverOpacity;
    public TextArea samples;
    public Spinner<Integer> textSize;
    public ColorPicker appThemeColor;
    public ColorPicker textFontColor;
    public AnchorPane mainPane;
    public AnchorPane mainPane2;
    public Label labelFont;
    public Label labelFontSize;
    public Label labelThemeStyle;
    public Label labelFontStyle;
    public Label labelOpacity;
    public Label title;
    public Label labelVersion;
    public Label labelMouseHoverOpacity;
    public Label labelCheckWrap;
    public Label labelSpellCheck;
    public Slider sliderOpacity;
    private Stage stage;

    public void initialize() {
        var settings = J_STICKY_DATA.getSettings();
        var windowSettings = J_STICKY_DATA.getWindowSettings();

        choiceTextFamily.setValue(settings.getTextFontFamily());
        checkWrap.setSelected(settings.getTextWrap());
        spellCheck.setSelected(settings.getSpellCheck());
        mouseHoverOpacity.setSelected(windowSettings.isMouseHoverOpacity());
        textSize.getValueFactory().setValue(settings.getTextSize());

        appThemeColor.setValue(settings.getAppThemeColor());
        textFontColor.setValue(settings.getTextFontColor());

        choiceTextFamily.getSelectionModel().selectedIndexProperty()
                .addListener((observable, oldValue, newValue) -> {
                    settings.setTextFontFamily(choiceTextFamily.getItems().get((Integer) newValue));
                    App.settingsReload();
                });

        checkWrap.setOnAction(event -> {
            settings.setTextWrap(checkWrap.isSelected());
            App.settingsReload();
        });

        spellCheck.setOnAction(event -> {
            settings.setSpellCheck(spellCheck.isSelected());
            if (spellCheck.isSelected()) {
                App.loadDictionaries();
            } else {
                App.uploadDictionaries();
            }
            App.settingsReload();
        });

        textSize.focusedProperty().addListener((obs, oldValue, newValue) -> {
            settings.setTextSize(textSize.getValue());
            App.settingsReload();
        });

        appThemeColor.setOnAction(event -> {
            settings.setAppThemeColor(appThemeColor.getValue().toString());
            App.settingsReload();
        });

        textFontColor.setOnAction(event -> {
            settings.setTextFontColor(textFontColor.getValue().toString());
            App.settingsReload();
        });

        sliderOpacity.setOnMouseReleased(event -> {
            windowSettings.setOpacity(sliderOpacity.getValue());
            App.settingsReload();
        });

        mouseHoverOpacity.setOnAction(event -> {
            J_STICKY_DATA.getWindowSettings().setMouseHoverOpacity(mouseHoverOpacity.isSelected());
            App.settingsReload();
        });
    }

    public void settingsReload() {
        var settings = J_STICKY_DATA.getSettings();
        var windowSettings = J_STICKY_DATA.getWindowSettings();

        samples.setFont(new Font(
                settings.getTextFontFamily(),
                settings.getTextSize()
        ));
        samples.setWrapText(settings.getTextWrap());

        samples.setStyle(format("-fx-text-fill: %s ; -fx-background-color: %s ;",
                settings.getTextFontColorText(),
                settings.getAppThemeColorText2()));
        mainPane.setStyle(format("-fx-background-color: %s ;", settings.getAppThemeColorText()));
        mainPane2.setStyle(format("-fx-background-color: %s ;", settings.getAppThemeColorText()));

        String labelColor = format("-fx-text-fill: %s ; ", settings.getTextFontColorText());
        labelCheckWrap.setStyle(labelColor);
        labelSpellCheck.setStyle(labelColor);
        labelFont.setStyle(labelColor);
        labelFontSize.setStyle(labelColor);
        labelThemeStyle.setStyle(labelColor);
        labelFontStyle.setStyle(labelColor);
        labelOpacity.setStyle(labelColor);
        title.setStyle(labelColor);
        labelVersion.setStyle(labelColor);
        labelMouseHoverOpacity.setStyle(labelColor);

        var opacity = windowSettings.getOpacity();
        sliderOpacity.setValue(opacity);

        // stage.setOpacity(opacity);
    }

    @Override
    public void setStageAndSetupListeners(Parent root, Scene scene, Stage stage) {
        this.stage = stage;
        setupRootListeners(root, scene, stage);
        exit.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        this.settingsReload();
    }

    public void onExit() {
        stage.close();
    }
}
