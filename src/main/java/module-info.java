module io.github.uniclog.jsticky {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;

    requires java.desktop;
    requires com.google.gson;
    requires lombok;
    requires org.fxmisc.flowless;

    exports io.github.uniclog.jsticky.model;
    exports io.github.uniclog.jsticky;
    exports io.github.uniclog.jsticky.controllers;
    exports io.github.uniclog.jsticky.controllers.services;

    opens io.github.uniclog.jsticky.model to com.google.gson;
    opens io.github.uniclog.jsticky.controllers to javafx.fxml;
    opens io.github.uniclog.jsticky to javafx.fxml;
    opens io.github.uniclog.jsticky.controllers.services to javafx.fxml;
}