package io.github.uniclog.jsticky;

import io.github.uniclog.jsticky.controllers.AppController;
import io.github.uniclog.jsticky.controllers.ListController;
import io.github.uniclog.jsticky.controllers.SettingsController;
import io.github.uniclog.jsticky.controllers.services.SceneControlService;
import io.github.uniclog.jsticky.model.JStickyData;
import io.github.uniclog.jsticky.utils.FileServiceWrapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static io.github.uniclog.jsticky.utils.FileServiceWrapper.saveObjectAsJson;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javafx.stage.StageStyle.UNDECORATED;

public class App extends Application {

    public static final String J_STICKY_DATA_PATH = "jSticky.ini";
    private static final Timer dataSaveTimer = new Timer("DataSaveTimer");
    public static JStickyData jStickyData = new JStickyData();
    private static Stage listStage;
    private static Stage appStage;
    private static AppController appController;
    private static Stage settingsStage;
    private static SettingsController settingsController;
    private static Stage screenCapStage;
    //private static ScreenCapController screenCapController;

    public static void main(String[] args) {
        launch();
    }

    public static void loadSettingsScene() throws IOException {
        if (isNull(settingsStage)) {
            var loader = new FXMLLoader(App.class.getResource("settings.fxml"));
            var root = (Parent) loader.load();
            var scene = new Scene(root);
            settingsStage = new Stage();

            settingsController = loader.getController();
            settingsController.setStageAndSetupListeners(root, scene, settingsStage);

            settingsStage.setScene(scene);
            settingsStage.initModality(Modality.NONE);
            settingsStage.initStyle(StageStyle.UNDECORATED);
            settingsStage.setTitle("jSticky Settings");
            settingsStage.show();
        } else {
            if (settingsStage.isShowing()) {
                settingsStage.close();
            } else {
                settingsStage.show();
            }
        }
    }

    /*public static void loadScreenCapScene() throws IOException {
        if (isNull(screenCapStage)) {
            var loader = new FXMLLoader(App.class.getResource("screenCap.fxml"));
            var root = (Parent) loader.load();
            var scene = new Scene(root);
            screenCapStage = new Stage();
            screenCapStage.setScene(scene);
            screenCapStage.initModality(Modality.NONE);
            screenCapStage.initStyle(StageStyle.UNDECORATED);
            screenCapStage.setTitle("jSticky Screen Cap");

            screenCapController = loader.getController();
            screenCapController.setStageAndSetupListeners(root, scene, screenCapStage);
        } else {
            if (screenCapStage.isShowing()) {
                screenCapStage.close();
            } else {
                screenCapController.show();
            }
        }
    }*/

    public static void loadListScene() throws IOException {
        if (isNull(listStage)) {
            var loader = new FXMLLoader(App.class.getResource("list.fxml"));
            var root = (Parent) loader.load();
            var scene = new Scene(root);
            listStage = new Stage();

            var controller = (ListController) loader.getController();
            controller.setStageAndSetupListeners(root, scene, listStage);

            listStage.setScene(scene);
            listStage.initModality(Modality.NONE);
            listStage.initStyle(StageStyle.UNDECORATED);
            listStage.setTitle("jSticky sticky list");
            listStage.show();
        } else {
            if (listStage.isShowing())
                listStage.close();
            else
                listStage.show();
        }
    }

    public static void close() {
        // stages check
        saveObjectAsJson(J_STICKY_DATA_PATH, jStickyData);
        SceneControlService.onExit();
    }

    public static void settingsReload() {
        appController.settingsReload();
        settingsController.settingsReload();
    }

    @Override
    public void start(Stage stage) throws IOException {
        initialize();
        loadAppScene();
    }

    private void initialize() {
        jStickyData = FileServiceWrapper.loadObjectFromJson(J_STICKY_DATA_PATH, JStickyData.class);
        if (isNull(jStickyData)) {
            jStickyData = new JStickyData();
        }
        TimerTask dataSaveTimerTask = new TimerTask() {
            @Override
            public void run() {
                var actual = jStickyData.getContent();
                if (nonNull(actual) && !actual.equals(jStickyData.getContentMirror())) {
                    saveObjectAsJson(J_STICKY_DATA_PATH, jStickyData);
                    jStickyData.setContentMirror(actual);
                }
            }
        };
        dataSaveTimer.scheduleAtFixedRate(dataSaveTimerTask, 1000, 8000);
    }

    private void loadAppScene() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("view.fxml"));
        var root = (Parent) loader.load();
        var scene = new Scene(root);
        appStage = new Stage();

        appController = loader.getController();
        appController.setStageAndSetupListeners(root, scene, appStage);

        appStage.initStyle(UNDECORATED);
        appStage.setTitle("jSticky");
        appStage.toFront();
        appStage.setScene(scene);
        appStage.show();
    }
}