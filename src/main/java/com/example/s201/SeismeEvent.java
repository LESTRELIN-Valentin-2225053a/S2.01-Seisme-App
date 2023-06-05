package com.example.s201;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SeismeEvent {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}