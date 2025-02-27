package io.github.uniclog.jsticky.model;

import lombok.Data;

@Data
public class JStickyWindowSettings {
    private Double width = 310.0D;
    private Double height = 280.0D;

    private Double posX;
    private Double posY;

    private Double opacity = 1.0D;
    private Boolean mouseHoverOpacity = false;

    /// Use in safe timer - dynamic parameters
    public boolean isModify(Double width, Double height, Double posX, Double posY) {
        return isModifyD(this.width, width) || isModifyD(this.height, height)
                || isModifyD(this.posX, posX) || isModifyD(this.posY, posY);
    }

    private boolean isModifyD(Double d1, Double d2) {
        return d1 != null && !d1.isNaN() && d2 != null && !d2.isNaN() && !d1.equals(d2);
    }

    public void modifySettings(Double width, Double height, Double posX, Double posY) {
        if (!width.isNaN()) {
            this.width = width;
        }
        if (!height.isNaN()) {
            this.height = height;
        }
        if (!posX.isNaN()) {
            this.posX = posX;
        }
        if (!posY.isNaN()) {
            this.posY = posY;
        }
    }

    public Double getOpacity() {
        if (opacity == null || opacity.isNaN() || opacity < 0.004D || opacity > 1.0D) {
            opacity = 1.0D;
        }
        return opacity;
    }

    public Boolean isMouseHoverOpacity() {
        return mouseHoverOpacity;
    }

    public Double getPosX() {
        if (posX == null || posX.isNaN()) {
            return null;
        }
        if (posX < 0.0D) {
            posX = 0D;
        }
        return posX;
    }

    public Double getPosY() {
        if (posY == null || posY.isNaN()) {
            return null;
        }
        if (posY < 0D) {
            posY = 0D;
        }
        return posY;
    }

    public Double getWidth() {
        if (width == null || width.isNaN()) {
            return null;
        }
        if (width < 152D) {
            width = 152D;
        }
        return width;
    }

    public Double getHeight() {
        if (height == null || height.isNaN()) {
            return null;
        }
        if (height < 74D) {
            height = 74D;
        }
        return height;
    }
}