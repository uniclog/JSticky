package io.github.uniclog.jsticky.model;

import lombok.Data;

@Data
public class JStickyWindowSettings {
    private Double width = 310D;
    private Double height = 280D;

    private Double posX = 0D;
    private Double posY = 0D;

    public boolean isModify(Double width, Double heigth, Double posX, Double posY) {
        return !this.width.equals(width) || !this.height.equals(heigth)
                || !this.posX.equals(posX) || !this.posY.equals(posY);
    }

    public void modifySettings(Double width, Double heigth, Double posX, Double posY) {
        this.width = width;
        this.height = heigth;
        this.posX = posX;
        this.posY = posY;
    }

    public Double getPosX() {
        if (posX < 0D) {
            posX = 0D;
        }
        return posX;
    }

    public Double getPosY() {
        if (posY < 0D) {
            posY = 0D;
        }
        return posY;
    }

    public Double getWidth() {
        if (width < 152D) {
            width = 152D;
        }
        return width;
    }

    public Double getHeight() {
        if (height < 74D) {
            height = 74D;
        }
        return height;
    }
}