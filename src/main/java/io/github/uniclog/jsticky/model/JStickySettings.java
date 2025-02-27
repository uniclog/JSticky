package io.github.uniclog.jsticky.model;

import javafx.scene.paint.Color;
import lombok.Data;

import static java.lang.Math.min;

@Data
public class JStickySettings {
    private Boolean textWrap = false;
    private Integer textSize = 12;
    private String textFontFamily = "System";

    private String appThemeColor = "#FFEB82";
    private String textFontColor = "#000000";

    public Color getAppThemeColor() {
        return Color.web(appThemeColor);
    }

    public String getAppThemeColorText2() {
        var theme = getAppThemeColor();
        double red = theme.getRed(),
                green = theme.getGreen(),
                blue = theme.getBlue();
        return new Color(red, green, blue, 0.8)
                .toString()
                .replaceAll("0x", "#");
    }

    public Color getTextFontColor() {
        return Color.web(textFontColor);
    }

    public String getTextFontColorAsString2() {
        var color = getTextFontColor();
        double red = min(1.0, color.getRed() + 0.2),
                green = color.getGreen(),
                blue = color.getBlue();
        return new Color(red, green, blue, 0.8)
                .toString()
                .replaceAll("0x", "#");
    }

    public String getAppThemeColorText() {
        return appThemeColor.replaceAll("0x", "#");
    }

    public String getTextFontColorText() {
        return textFontColor.replaceAll("0x", "#");
    }
}
