package io.github.uniclog.jsticky.control;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.*;

import static javafx.scene.Cursor.*;

/**
 * App control helper-service
 * - resize app
 * - move app
 *
 * @version 1.0
 */
public class SceneControlService {

    private final Point deltaMove = new Point(0, 0);
    private final Point deltaResize = new Point(0, 0);
    private final int borderSizePixel = 4;

    //region control button

    /**
     * Exit button, close app
     */
    public static void onExit() {
        System.exit(0);
    }

    /**
     * Minimize button
     *
     * @param stage stage
     */
    public static void onMin(Stage stage) {
        stage.setIconified(true);
    }

    //endregion

    /**
     * Set temp values by mouse pressed
     *
     * @param mouseEvent mouse event
     * @param root       root
     * @param stage      stage
     */
    public void setOnMousePressed(MouseEvent mouseEvent, Parent root, Stage stage) {
        root.requestFocus();
        deltaMove.setLocation(stage.getX() - mouseEvent.getScreenX(), stage.getY() - mouseEvent.getScreenY());
        deltaResize.setLocation(stage.getWidth() - mouseEvent.getSceneX(), stage.getHeight() - mouseEvent.getSceneY());
    }

    /**
     * Set cursor type by mouse move
     *
     * @param mouseEvent mouse event
     * @param scene      scene
     */
    public void setOnMouseMoved(MouseEvent mouseEvent, Scene scene) {
        var mouseEventX = mouseEvent.getSceneX();
        var mouseEventY = mouseEvent.getSceneY();
        var sceneWidth = scene.getWidth();
        var sceneHeight = scene.getHeight();
        var cursorEvent = DEFAULT;
        if (mouseEventX < borderSizePixel && mouseEventY < borderSizePixel) {
            cursorEvent = NW_RESIZE;
        } else if (mouseEventX < borderSizePixel && mouseEventY > sceneHeight - borderSizePixel) {
            cursorEvent = SW_RESIZE;
        } else if (mouseEventX > sceneWidth - borderSizePixel && mouseEventY < borderSizePixel) {
            cursorEvent = NE_RESIZE;
        } else if (mouseEventX > sceneWidth - borderSizePixel && mouseEventY > sceneHeight - borderSizePixel) {
            cursorEvent = SE_RESIZE;
        } else if (mouseEventX < borderSizePixel) {
            cursorEvent = W_RESIZE;
        } else if (mouseEventX > sceneWidth - borderSizePixel) {
            cursorEvent = E_RESIZE;
        } else if (mouseEventY < borderSizePixel) {
            //cursorEvent = N_RESIZE;
        } else if (mouseEventY > sceneHeight - borderSizePixel) {
            cursorEvent = S_RESIZE;
        }
        scene.setCursor(cursorEvent);
    }

    /**
     * Set new stage position or stage resize
     *
     * @param mouseEvent mouse event
     * @param scene      scene
     * @param stage      stage
     */
    public void setOnMouseDragged(MouseEvent mouseEvent, Scene scene, Stage stage) {
        // region stage move
        if (scene.getCursor().equals(DEFAULT)) {
            stage.setX(mouseEvent.getScreenX() + deltaMove.getX());
            stage.setY(mouseEvent.getScreenY() + deltaMove.getY());
            return;
        }
        // region horizontal stage resize
        if (!scene.getCursor().equals(W_RESIZE) && !scene.getCursor().equals(E_RESIZE)) {
            horizontalResize(mouseEvent, scene, stage);
        }
        // region vertical stage resize
        if (!scene.getCursor().equals(N_RESIZE) && !scene.getCursor().equals(S_RESIZE)) {
            verticalResize(mouseEvent, scene, stage);
        }
    }

    private void horizontalResize(MouseEvent mouseEvent, Scene scene, Stage stage) {
        var minHeight = stage.getMinHeight() > (borderSizePixel * 2) ? stage.getMinHeight() : (borderSizePixel * 2);
        if (scene.getCursor().equals(NW_RESIZE) || scene.getCursor().equals(N_RESIZE) || scene.getCursor().equals(NE_RESIZE)) {
            if (stage.getHeight() > minHeight || deltaResize.getY() < 0) {
                stage.setHeight(stage.getY() - mouseEvent.getScreenY() + stage.getHeight());
                stage.setY(mouseEvent.getScreenY());
            }
        } else {
            if (stage.getHeight() > minHeight || mouseEvent.getSceneY() + deltaResize.getY() - stage.getHeight() > 0) {
                stage.setHeight(mouseEvent.getSceneY() + deltaResize.getY());
            }
        }
    }

    private void verticalResize(MouseEvent mouseEvent, Scene scene, Stage stage) {
        var minWidth = stage.getMinWidth() > (borderSizePixel * 2) ? stage.getMinWidth() : (borderSizePixel * 2);
        if (scene.getCursor().equals(NW_RESIZE) || scene.getCursor().equals(W_RESIZE) || scene.getCursor().equals(SW_RESIZE)) {
            if (stage.getWidth() > minWidth || deltaResize.getX() < 0) {
                stage.setWidth(stage.getX() - mouseEvent.getScreenX() + stage.getWidth());
                stage.setX(mouseEvent.getScreenX());
            }
        } else {
            if (stage.getWidth() > minWidth || mouseEvent.getSceneX() + deltaResize.getX() - stage.getWidth() > 0) {
                stage.setWidth(mouseEvent.getSceneX() + deltaResize.getX());
            }
        }
    }
}
