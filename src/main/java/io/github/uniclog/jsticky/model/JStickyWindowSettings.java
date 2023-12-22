package io.github.uniclog.jsticky.model;

import lombok.Data;

@Data
public class JStickyWindowSettings {
    private Double width = 310D;
    private Double heigth = 280D;
    
    public boolean isModify(Double width, Double heigth) {
        return !this.width.equals(width) || !this.heigth.equals(heigth); 
    }
}