package io.github.uniclog.jsticky.controllers;

import io.github.uniclog.jsticky.App;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static io.github.uniclog.jsticky.App.jStickyData;

public class SettingsController implements ControllersInterface {
    public ToggleButton exit;
    public ChoiceBox<String> choiceTextFamily;
    public CheckBox checkWrap;
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
    public Slider sliderOpacity;
    private Stage stage;

    public void initialize() {
        var settings = jStickyData.getSettings();
        var windowSettings = jStickyData.getWindowSettings();

        choiceTextFamily.setValue(settings.getTextFontFamily());
        checkWrap.setSelected(settings.getTextWrap());
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
            jStickyData.getWindowSettings().setMouseHoverOpacity(mouseHoverOpacity.isSelected());
            App.settingsReload();
        });
    }

    public void settingsReload() {
        var settings = jStickyData.getSettings();
        var windowSettings = jStickyData.getWindowSettings();

        samples.setFont(new Font(
                settings.getTextFontFamily(),
                settings.getTextSize()
        ));
        samples.setWrapText(settings.getTextWrap());

        samples.setStyle(String.format("-fx-text-fill: %s ; -fx-background-color: %s ;",
                settings.getTextFontColorText(),
                settings.getAppThemeColorText2()));
        mainPane.setStyle(String.format("-fx-background-color: %s ;", settings.getAppThemeColorText()));
        mainPane2.setStyle(String.format("-fx-background-color: %s ;", settings.getAppThemeColorText()));

        labelCheckWrap.setStyle(String.format("-fx-text-fill: %s ; ", settings.getTextFontColorText()));
        labelFont.setStyle(String.format("-fx-text-fill: %s ; ", settings.getTextFontColorText()));
        labelFontSize.setStyle(String.format("-fx-text-fill: %s ; ", settings.getTextFontColorText()));
        labelThemeStyle.setStyle(String.format("-fx-text-fill: %s ; ", settings.getTextFontColorText()));
        labelFontStyle.setStyle(String.format("-fx-text-fill: %s ; ", settings.getTextFontColorText()));
        labelOpacity.setStyle(String.format("-fx-text-fill: %s ; ", settings.getTextFontColorText()));
        title.setStyle(String.format("-fx-text-fill: %s ; ", settings.getTextFontColorText()));
        labelVersion.setStyle(String.format("-fx-text-fill: %s ; ", settings.getTextFontColorText()));
        labelMouseHoverOpacity.setStyle(String.format("-fx-text-fill: %s ; ", settings.getTextFontColorText()));

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
