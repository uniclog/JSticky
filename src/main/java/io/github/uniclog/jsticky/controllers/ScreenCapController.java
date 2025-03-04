package io.github.uniclog.jsticky.controllers;

import io.github.uniclog.jsticky.App;
import io.github.uniclog.jsticky.utils.ClipboardUtils;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.uniclog.jsticky.App.J_STICKY_DATA;
import static java.util.Objects.nonNull;

public class ScreenCapController implements ControllersInterface {
    private static boolean alwaysOnTop = false;
    private final Robot robot;
    private final AtomicBoolean screenLockFlag = new AtomicBoolean(false);
    public ToggleButton exit;
    public ToggleButton fix;
    public AnchorPane capturePane;
    public ToggleButton screenLock;
    public AnchorPane mainPane;
    private Timer timerEx;
    private Stage stage;
    private ImageView view;

    public ScreenCapController() {
        robot = new Robot();
    }

    public void initialize() {
    }

    public void settingsReload() {
        var settings = J_STICKY_DATA.getSettings();
        var windowSettings = J_STICKY_DATA.getWindowSettings();

        mainPane.setStyle(String.format("-fx-background-color: %s ;", settings.getAppThemeColorText()));
        stage.setOpacity(windowSettings.getOpacity());
    }

    @Override
    public void setStageAndSetupListeners(Parent root, Scene scene, Stage stage) {
        this.stage = stage;
        setupRootListeners(root, scene, stage);
        exit.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        view = new ImageView();
        capturePane.getChildren().add(view);
        show();
        settingsReload();
        addMouseHoverOpacityListeners(scene, stage);
    }

    public void onExit() {
        timerEx.cancel();
        view.setImage(null);
        stage.close();
        stage = null;
        App.closeScreenCapStage();
        System.gc();
    }

    public void show() {
        var screen = Toolkit.getDefaultToolkit().getScreenSize();
        WritableImage image = robot.getScreenCapture(null, new Rectangle2D(0, 0, screen.getWidth(), screen.getHeight()));
        view.setImage(image);

        stage.centerOnScreen();
        view.setViewport(new Rectangle2D(stage.getX(), stage.getY(), 290, 556));

        timerEx = new Timer("ScreenCapTimer");
        timerEx.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (!screenLockFlag.get() && nonNull(stage)) {
                            Platform.runLater(() -> {
                                try {
                                    var r2d = new Rectangle2D(stage.getX(), stage.getY(), capturePane.getWidth(), capturePane.getHeight());
                                    view.setViewport(r2d);
                                    view.setFitWidth(capturePane.getWidth());
                                    view.setFitHeight(capturePane.getHeight());
                                } catch (NullPointerException ignored) {
                                }
                            });
                        }
                    }
                }, 100, 20);

        stage.show();
        screenLockFlag.set(false);
        screenLock.getStyleClass().removeAll("gui-screen-cap-button-on");
        screenLock.getStyleClass().add("gui-screen-cap-button");
    }

    public void onScreenLock() {
        if (screenLockFlag.get()) {
            screenLock.getStyleClass().removeAll("gui-screen-cap-button-on");
            screenLock.getStyleClass().add("gui-screen-cap-button");
        } else {
            screenLock.getStyleClass().removeAll("gui-screen-cap-button");
            screenLock.getStyleClass().add("gui-screen-cap-button-on");
        }
        screenLockFlag.set(!screenLockFlag.get());
    }

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

    public void onActionCopy() {
        WritableImage viewportImage = new WritableImage((int) capturePane.getWidth(), (int) capturePane.getHeight());
        view.snapshot(null, viewportImage);
        ClipboardUtils.copyToClipboard(viewportImage);
    }

}
