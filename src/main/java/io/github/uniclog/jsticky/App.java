package io.github.uniclog.jsticky;

import io.github.uniclog.jsticky.controllers.AppController;
import io.github.uniclog.jsticky.controllers.ScreenCapController;
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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static io.github.uniclog.jsticky.utils.FileServiceWrapper.saveObjectAsJson;
import static java.util.Objects.*;
import static javafx.stage.StageStyle.UNDECORATED;

public class App extends Application {
    private volatile static boolean APP_STOP = false;

    public static final String J_STICKY_DATA_PATH = "jSticky.ini";
    public static JStickyData J_STICKY_DATA = new JStickyData();
    public static Set<String> DICTIONARY;
    private static final Timer dataSaveTimer = new Timer("DataSaveTimer");
    private static Stage appStage;
    private static AppController appController;
    private static Stage settingsStage;
    private static SettingsController settingsController;
    private static Stage screenCapStage;
    private static ScreenCapController screenCapController;

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
            if (!settingsStage.isShowing()) {
                settingsStage.show();
            }
        }
    }

    public static void loadScreenCapScene() throws IOException {
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
            if (!screenCapStage.isShowing()) {
                screenCapController.show();
            }
        }
    }

    public static void close() {
        App.APP_STOP = true;

        var windowSettings = J_STICKY_DATA.getWindowSettings();
        windowSettings.modifySettings(appStage.getWidth(), appStage.getHeight(), appStage.getX(), appStage.getY());
        saveObjectAsJson(J_STICKY_DATA_PATH, J_STICKY_DATA);

        SceneControlService.onExit();
    }

    public static void closeScreenCapStage() {
        screenCapStage = null;
        screenCapController = null;
    }

    public static void settingsReload() {
        if (nonNull(appController))
            appController.settingsReload();
        if (nonNull(settingsController))
            settingsController.settingsReload();
        if (nonNull(screenCapController))
            screenCapController.settingsReload();
    }

    @Override
    public void start(Stage stage) throws IOException {
        appStage = new Stage();
        initialize();
        loadAppScene();
    }

    private void initialize() {
        J_STICKY_DATA = FileServiceWrapper.loadObjectFromJson(J_STICKY_DATA_PATH, JStickyData.class);
        if (isNull(J_STICKY_DATA)) {
            J_STICKY_DATA = new JStickyData();
        }
        if (J_STICKY_DATA.getSettings().getSpellCheck()) {
            loadDictionaries();
        }
        TimerTask dataSaveTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (App.APP_STOP) return;
                var actual = J_STICKY_DATA.getContent();
                var windowSettings = J_STICKY_DATA.getWindowSettings();
                if (nonNull(actual) && !actual.equals(J_STICKY_DATA.getContentMirror())
                        || windowSettings.isModify(appStage.getWidth(), appStage.getHeight(), appStage.getX(), appStage.getY())) {
                    windowSettings.modifySettings(appStage.getWidth(), appStage.getHeight(), appStage.getX(), appStage.getY());
                    saveObjectAsJson(J_STICKY_DATA_PATH, J_STICKY_DATA);
                    J_STICKY_DATA.setContentMirror(actual);
                }
            }
        };
        dataSaveTimer.scheduleAtFixedRate(dataSaveTimerTask, 10000, 10000);
    }

    private void loadAppScene() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("view.fxml"));
        var root = (Parent) loader.load();
        var scene = new Scene(root);

        appController = loader.getController();
        appController.setStageAndSetupListeners(root, scene, appStage);

        appStage.initStyle(UNDECORATED);
        appStage.setTitle("jSticky");
        appStage.toFront();
        appStage.setScene(scene);

        var windowSettings = J_STICKY_DATA.getWindowSettings();
        var width = windowSettings.getWidth();
        if (width != null) appStage.setWidth(width);
        var height = windowSettings.getHeight();
        if (height != null) appStage.setHeight(height);
        var posX = windowSettings.getPosX();
        if (posX != null) appStage.setX(posX);
        var posY = windowSettings.getPosY();
        if (posY != null) appStage.setY(posY);

        appStage.show();
    }

    public static void loadDictionaries() {
        try {
            DICTIONARY = new HashSet<>();
            DICTIONARY.addAll(Files.readAllLines(Path.of(requireNonNull(App.class.getResource("dic/ru_RU.dic")).toURI())));
            DICTIONARY.addAll(Files.readAllLines(Path.of(requireNonNull(App.class.getResource("dic/ru_spec.dic")).toURI())));
            DICTIONARY.addAll(Files.readAllLines(Path.of(requireNonNull(App.class.getResource("dic/en_US.dic")).toURI())));
        } catch (IOException | URISyntaxException e) {
            System.err.println("Error while loading dictionary files: " + e.getMessage());
        }
    }

    public static void uploadDictionaries() {
        DICTIONARY = null;
    }
}