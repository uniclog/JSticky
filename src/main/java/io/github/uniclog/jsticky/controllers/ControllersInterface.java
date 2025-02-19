package io.github.uniclog.jsticky.controllers;

import io.github.uniclog.jsticky.controllers.services.SceneControlService;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static io.github.uniclog.jsticky.App.jStickyData;

public interface ControllersInterface {
    SceneControlService controlService = new SceneControlService();
    void setStageAndSetupListeners(Parent root, Scene scene, Stage stage);

    default void setupRootListeners(Parent root, Scene scene, Stage stage) {
        root.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, scene));
        root.setOnMousePressed(mouseEvent -> controlService.setOnMousePressed(mouseEvent, root, stage));
        root.setOnMouseDragged(mouseEvent -> controlService.setOnMouseDragged(mouseEvent, scene, stage));
    }

    default void addMouseHoverOpacityListeners(Scene scene, Stage stage) {
        scene.setOnMouseEntered(ev -> {
            if (jStickyData.getWindowSettings().isMouseHoverOpacity()) {
                stage.setOpacity(1.0D);
            }
        });
        scene.setOnMouseExited(ev -> {
            if (jStickyData.getWindowSettings().isMouseHoverOpacity()) {
                stage.setOpacity(jStickyData.getWindowSettings().getOpacity());
            }
        });
    }
}
