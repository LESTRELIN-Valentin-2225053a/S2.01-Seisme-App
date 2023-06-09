package com.example.s201;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.scene.chart.PieChart;


import java.io.File;
import java.util.ArrayList;

public class SeismeEvent {
    @FXML
    private Label welcomeText;
    @FXML
    private BorderPane root;

    @FXML
    private PieChart camembert;

    @FXML
    private Slider sliderMin;
    @FXML
    private Slider sliderMax;
    @FXML
    private BarChart diagrammeBandes;
    @FXML
    private ChoiceBox regions;
    ArrayList<String> regionsList = new ArrayList<>();

    @FXML
    private TextField dateDebut;
    @FXML
    private TextField dateFin;


    //Cette fonction permet d'initialiser les deux sliders contenu dans le filtre
    public void initialize() {
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

        //Initialisation de la ChoiceBox
        regionsList.add("AUVERGNE-RHÔNE-ALPES");
        regionsList.add("BOURGOGNE-FRANCHE-COMTÉ");
        regionsList.add("BRETAGNE");
        regionsList.add("CENTRE-VAL DE LOIRE");
        regionsList.add("CORSE");
        regionsList.add("GRAND EST");
        regionsList.add("HAUTS-DE-FRANCE");
        regionsList.add("ÎLE-DE-FRANCE");
        regionsList.add("NORMANDIE");
        regionsList.add("NOUVELLE-AQUITAINE");
        regionsList.add("OCCITANIE");
        regionsList.add("PAYS DE LA LOIRE");
        regionsList.add("PROVENCE-ALPES-CÔTE D'AZUR");
        // Régions d'outre-mer
        regionsList.add("GUADELOUPE");
        regionsList.add("MARTINIQUE");
        regionsList.add("GUYANE");
        regionsList.add("LA RÉUNION");
        regionsList.add("MAYOTTE");

        for (int i = 0; i < regionsList.size(); i++) {
            regions.getItems().add(regionsList.get(i));
        }

        //Initialisation du Barchart
        diagrammeBandes.getData().add(SeismeData.SerieDonneesBarchart);

        //Initialisation du camembert
        SeismeData seismeData = new SeismeData();
        //seismeData.prepDonneesCamembert(donnees, sliderMin, sliderMax);
        camembert.setData(seismeData.getDonneesCamembert());

    }

    //Cette fonction est utilisé par le bouton "Inserer" et sert à
    //choisir le fichier csv que l'on veut. La fonction lancera ensuite les
    // autres fonctions qui vont préparer les données pour l'affichage.
    public void insererBouton() {
        FileChooser choixFichier = new FileChooser();
        File fichierCSV = choixFichier.showOpenDialog(root.getScene().getWindow());
        if (fichierCSV != null) {
            //lance la configuration du CSV
            SeismeData.lectureCSV(fichierCSV);
        } else {
            //Faudra mettre un label qui dit "faut choisir un fichier"
        }
    }

    //Cette méthode est utilisé pour savoir si une date passé en paramètre
    //respecte le bon format classique utilisé en france, 00/00/0000
    public boolean isValidDate(String date) {
        String datePattern = "^(0?[1-9]|1[0-9]|2[0-9]|3[01])/(0?[1-9]|1[012])/\\d{4}$";
        return date.matches(datePattern);
    }

    //Cette méthode est utilisé pour afficher une alerte avec un message entré en paramètre
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Vérification de format");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Les deux prochaines méthodes sont utilisées pour affichier une erreur
    //si la date saisi dans les textField ne correspondent pas au format souhaité
    @FXML
    public void textFiel1dOnAction() {
        String date = dateDebut.getText();
        if (!isValidDate(date)) showAlert("Format de date invalide !");
    }
    @FXML
    public void textFiel2dOnAction() {
        String date = dateFin.getText();
        if (!isValidDate(date)) showAlert("Format de date invalide !");
    }

    @FXML
    public void ReinitialiserOnAction(){
        regions.setValue(null);
        dateDebut.setText(null);
        dateFin.setText(null);
        sliderMin.setValue(2);
        sliderMax.setValue(12);
    }


}