package com.example.s201;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;

public class SeismeEvent {
    @FXML
    private Label welcomeText;
    @FXML
    private BorderPane root;

    @FXML
    private Slider sliderMin;
    @FXML
    private Slider sliderMax;
    @FXML
    private BarChart diagrammeBandes;


    //Cette fonction permet d'initialiser les deux sliders contenu dans le filtre
    public void initialize(){
        //Initialisation du slider minimum
        sliderMin.setBlockIncrement(1);
        sliderMin.setShowTickLabels(true);
        sliderMin.setShowTickMarks(true);
        sliderMin.setMajorTickUnit(1);
        sliderMin.setMinorTickCount(0);
        sliderMin.setSnapToTicks(true);
        sliderMin.valueProperty().addListener((observable, oldValue, newValue) -> {
            int intValue = newValue.intValue();
        });

        //Initialisation du slider maximum
        sliderMax.setBlockIncrement(1);
        sliderMax.setShowTickLabels(true);
        sliderMax.setShowTickMarks(true);
        sliderMax.setMajorTickUnit(1);
        sliderMax.setMinorTickCount(0);
        sliderMax.setSnapToTicks(true);
        sliderMax.minProperty().bind(sliderMin.valueProperty());
        sliderMax.valueProperty().addListener((observable, oldValue, newValue) -> {
            int intValue = newValue.intValue();
        });

        //Initialisation du Barchart
        diagrammeBandes.getData().add(SeismeData.SerieDonneesBarchart);
    }

    //Cette fonction est utilisé par le bouton "Inserer" et sert à
    //choisir le fichier csv que l'on veut. La fonction lancera ensuite les
    // autres fonctions qui vont préparer les données pour l'affichage.
    public void insererBouton(){
        FileChooser choixFichier = new FileChooser();
        File fichierCSV = choixFichier.showOpenDialog(root.getScene().getWindow());
        if (fichierCSV != null){
            //lance la configuration du CSV
            SeismeData.lectureCSV(fichierCSV);
        }
        else{
            //Faudra mettre un label qui dit "faut choisir un fichier"
        }
    }
}