package io.github.uniclog.jsticky;

import io.github.uniclog.jsticky.control.services.SceneControlService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.stage.StageStyle.UNDECORATED;

public class MainApp extends Application {

    static public final SceneControlService controlService = new SceneControlService();
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        var loader = new FXMLLoader(getClass().getResource("view.fxml"));
        var root = (Parent) loader.load();
        var scene = new Scene(root);

        root.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, scene));
        root.setOnMousePressed(mouseEvent -> controlService.setOnMousePressed(mouseEvent, root, stage));
        root.setOnMouseDragged(mouseEvent -> controlService.setOnMouseDragged(mouseEvent, scene, stage));

        // stage.setOpacity(0.90);
        stage.initStyle(UNDECORATED);
        stage.setTitle("jSticky");
        // stage.setAlwaysOnTop(true)
        stage.toFront();

        // stage.setMinHeight(24);
        // stage.setMinWidth(96);

        stage.setScene(scene);
        stage.show();
    }
}