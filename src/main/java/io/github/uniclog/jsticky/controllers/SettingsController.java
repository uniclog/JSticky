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
    public TextArea samples;
    public Spinner<Integer> textSize;
    public ColorPicker appThemeColor;
    public ColorPicker textFontColor;
    public AnchorPane mainPane;
    public AnchorPane mainPane2;
    private Stage stage;

    public void initialize() {
        var settings = jStickyData.getSettings();

        choiceTextFamily.setValue(settings.getTextFontFamily());
        checkWrap.setSelected(settings.getTextWrap());
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

        settingsReload();
    }

    public void settingsReload() {
        var settings = jStickyData.getSettings();
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

    }

    @Override
    public void setStageAndSetupListeners(Parent root, Scene scene, Stage stage) {
        this.stage = stage;
        setupRootListeners(root, scene, stage);
        exit.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
    }

    public void onExit() {
        stage.close();
    }
}
