package io.github.uniclog.jsticky.model;

import javafx.scene.paint.Color;
import lombok.Data;

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
        double value = 0.20;
        var theme = Color.web(appThemeColor);
        double red = theme.getRed(),
                green = theme.getGreen(),
                blue = theme.getBlue();
        // var dominance = Math.max(red, Math.max(green, blue));
        // if (red >= value && red == dominance) red -= value;
        // if (green >= value && green == dominance) green -= value;
        // if (blue >= value && blue == dominance) blue -= value;

        return new Color(red, green, blue, 0.8)
                .toString()
                .replaceAll("0x", "#");
    }

    public Color getTextFontColor() {
        return Color.web(textFontColor);
    }

    public String getAppThemeColorText() {
        return appThemeColor.replaceAll("0x", "#");
    }

    public String getTextFontColorText() {
        return textFontColor.replaceAll("0x", "#");
    }
}
