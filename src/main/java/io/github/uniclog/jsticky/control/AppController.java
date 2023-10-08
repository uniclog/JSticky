package io.github.uniclog.jsticky.control;

import io.github.uniclog.jsticky.model.JStickyData;
import io.github.uniclog.jsticky.utils.FileServiceWrapper;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

import static io.github.uniclog.jsticky.MainApp.controlService;
import static io.github.uniclog.jsticky.utils.FileServiceWrapper.saveObjectAsJson;
import static java.util.Objects.nonNull;

public class AppController {
    private static final String JSTICKY_DATA_PATH = "jsticky.ini";
    private static final JStickyData jStickyData = new JStickyData();
    private static final Timer dataSaveTimer = new Timer();
    private static boolean alwaysOnTop = false;
    private static boolean onWrap = false;
    public TextArea mainTextArea;
    public ToggleButton exit;
    public ToggleButton fix;
    public ToggleButton wrap;

    /**
     * Controller post construct
     */
    public void initialize() {
        JStickyData actual = FileServiceWrapper.loadObjectFromJson(JSTICKY_DATA_PATH, JStickyData.class);
        if (nonNull(actual)) {
            mainTextArea.setText(actual.getContent());
        }
        TimerTask dataSaveTimerTask = new TimerTask() {
            @Override
            public void run() {
                var actual = mainTextArea.getText();
                if (!actual.equals(jStickyData.getContent())) {
                    jStickyData.setContent(actual);
                    saveObjectAsJson(JSTICKY_DATA_PATH, jStickyData);
                }
            }
        };
        dataSaveTimer.scheduleAtFixedRate(dataSaveTimerTask, 1000, 8000);

        exit.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        mainTextArea.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
        fix.setOnMouseMoved(mouseEvent -> controlService.setOnMouseMoved(mouseEvent, exit.getScene()));
    }

    /**
     * Button: Exit
     */
    public void onExit() {
        var actual = mainTextArea.getText();
        if (!actual.equals(jStickyData.getContent())) {
            jStickyData.setContent(actual);
            saveObjectAsJson(JSTICKY_DATA_PATH, jStickyData);
        }

        SceneControlService.onExit();
    }

    /**
     * Button: Minimize window
     */
    public void onMin() {
        SceneControlService.onMin((Stage) exit.getScene().getWindow());
    }

    /**
     * Button: Window pin
     */
    public void onFix() {
        var stage = (Stage) exit.getScene().getWindow();
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
}