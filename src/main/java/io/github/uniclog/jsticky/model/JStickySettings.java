package io.github.uniclog.jsticky.model;

import lombok.Data;

@Data
public class JStickySettings {
    private Boolean textWrap = false;
    private Integer textSize = 12;
    private String textFontFamily = "System";
}
