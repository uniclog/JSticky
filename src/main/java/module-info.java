module io.github.uniclog.jsticky {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.desktop;
    requires com.google.gson;
    requires lombok;

    opens io.github.uniclog.jsticky.model to com.google.gson;
    exports io.github.uniclog.jsticky.model;
    opens io.github.uniclog.jsticky to javafx.fxml;
    exports io.github.uniclog.jsticky;
    exports io.github.uniclog.jsticky.controllers;
    opens io.github.uniclog.jsticky.controllers to javafx.fxml;
    exports io.github.uniclog.jsticky.controllers.services;
    opens io.github.uniclog.jsticky.controllers.services to javafx.fxml;
}