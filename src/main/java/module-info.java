module com.example.s201 {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
            requires com.dlsc.formsfx;
                    requires org.kordamp.bootstrapfx.core;

    requires com.gluonhq.maps;

    opens com.example.s201 to javafx.fxml;
    exports com.example.s201;
}