package io.github.uniclog.jsticky.controllers;

import io.github.uniclog.jsticky.App;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import static io.github.uniclog.jsticky.App.jStickyData;

public class SettingsController implements ControllersInterface {
    public ToggleButton exit;
    public ChoiceBox<String> choiceTextFamily;
    public CheckBox checkWrap;
    public TextArea samples;
    public Spinner<Integer> textSize;
    private Stage stage;

    public void initialize() {
        var settings = jStickyData.getSettings();

        choiceTextFamily.setValue(settings.getTextFontFamily());
        checkWrap.setSelected(settings.getTextWrap());
        textSize.getValueFactory().setValue(settings.getTextSize());

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
    }

    public void settingsReload() {
        var settings = jStickyData.getSettings();
        samples.setFont(new Font(
                settings.getTextFontFamily(),
                settings.getTextSize()
        ));
        samples.setWrapText(settings.getTextWrap());

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
