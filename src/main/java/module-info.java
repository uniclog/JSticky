module io.github.uniclog.jsticky {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.desktop;
    requires com.google.gson;

    opens io.github.uniclog.jsticky.model to com.google.gson;
    opens io.github.uniclog.jsticky to javafx.fxml;
    exports io.github.uniclog.jsticky;
    exports io.github.uniclog.jsticky.control;
    opens io.github.uniclog.jsticky.control to javafx.fxml;
    exports io.github.uniclog.jsticky.control.services;
    opens io.github.uniclog.jsticky.control.services to javafx.fxml;
}