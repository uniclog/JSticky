package io.github.uniclog.jsticky.model;

import lombok.Data;

@Data
public class JStickyWindowSettings {
    private Double width = 310D;
    private Double heigth = 280D;

    private Double posX = 0D;
    private Double posY = 0D;

    public boolean isModify(Double width, Double heigth, Double posX, Double posY) {
        return !this.width.equals(width) || !this.heigth.equals(heigth)
                || !this.posX.equals(posX) || !this.posY.equals(posY);
    }

    public void modifySettings(Double width, Double heigth, Double posX, Double posY) {
        this.width = width;
        this.heigth = heigth;
        this.posX = posX;
        this.posY = posY;
    }
}