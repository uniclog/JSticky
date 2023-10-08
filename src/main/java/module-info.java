module io.github.uniclog.jsticky {
    requires javafx.controls;
    requires javafx.fxml;

    //requires com.dlsc.formsfx;
    requires java.desktop;

    opens io.github.uniclog.jsticky to javafx.fxml;
    exports io.github.uniclog.jsticky;
    exports io.github.uniclog.jsticky.control;
    opens io.github.uniclog.jsticky.control to javafx.fxml;
}