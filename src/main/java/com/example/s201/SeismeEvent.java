package com.example.s201;

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import com.example.s201.SeismeData;


import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SeismeEvent{
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
    private ScatterChart diagrammePoint;
    @FXML
    private MapView Map;

    NumberAxis xAxis = new NumberAxis();
    NumberAxis yAxis = new NumberAxis();
    LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

    @FXML
    private ChoiceBox regions;
    ArrayList<String> regionsList = new ArrayList<>();

    @FXML
    private TextField dateDebut;
    @FXML
    private TextField dateFin;

    @FXML
    private Button diagrammeaPoint;
    @FXML
    private  Button actualiser;
    @FXML
    private Button courbeButton;

    @FXML
    private HBox bas;
    @FXML
    private AnchorPane carte;
    private CustomMapLayer mapLayout;
    private SeismeData data;



    public void initialize() {
        data = new SeismeData();
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

        for (String s : regionsList) {
            regions.getItems().add(s);
        }

        //Initialisation de la map
        InitMap();

        //Initialisation du Barchart
        SeismeData.SerieDonneesScatterchart.setData(SeismeData.getDonneesScatterchart());
        diagrammePoint.getData().add(SeismeData.SerieDonneesScatterchart);

        SeismeData.SerieDonneesLineChart.setData(SeismeData.getDonneesLineChart());
        lineChart.getData().add(SeismeData.SerieDonneesLineChart);

        //Initialisation du camembert
        SeismeData seismeData = new SeismeData();
        //seismeData.prepDonneesCamembert(donnees, sliderMin, sliderMax);
        camembert.setData(SeismeData.getDonneesCamembert());

        //Initialisation du bouton actualiser
        actualiser.setDisable(true);

        //Initialisation des TextField
        dateDebut.setText(null);
        dateFin.setText(null);

        //Initialisation du bouton nuage de points
        diagrammeaPoint.setDisable(true);

    }

    //Cette fonction est utilisé par le bouton "Inserer" et sert à
    //choisir le fichier csv que l'on veut. La fonction lancera ensuite les
    // autres fonctions qui vont préparer les données pour l'affichage.
    public void insererBouton() throws ParseException {
        FileChooser choixFichier = new FileChooser();
        File fichierCSV = choixFichier.showOpenDialog(root.getScene().getWindow());
        if (fichierCSV != null) {
            //lance la configuration du CSV
            GestionDonneesCSV.lectureCSV(fichierCSV);
            SeismeData.prepDonneesScatterchart(GestionDonneesCSV.getDateMin(), GestionDonneesCSV.getDateMax(),
                    GestionDonneesCSV.getIntensiteMin(), GestionDonneesCSV.getIntensiteMax(), "");
            String[] anneeDebut = String.valueOf(GestionDonneesCSV.getDateMin()).split("-+");
            String[] anneeFin = String.valueOf(GestionDonneesCSV.getDateMax()).split("-+");
            SeismeData.prepdonneesCourbe(Integer.parseInt(anneeDebut[0]), Integer.parseInt(anneeFin[0]),
                    GestionDonneesCSV.getIntensiteMin(), GestionDonneesCSV.getIntensiteMax(), "");
            SetPointOnMap(GestionDonneesCSV.getDateMin(), GestionDonneesCSV.getDateMax(),
                    GestionDonneesCSV.getIntensiteMin(), GestionDonneesCSV.getIntensiteMax(), "");

        } else {
            showAlert("Veuillez choisir un fichier !");
        }
    }

    //Cette méthode est utilisé pour savoir si une date passé en paramètre
    //respecte le bon format classique utilisé en france, 00/00/0000
    public boolean isValidDate(String date) {
        String datePattern = "^\\d{4}/(0?[1-9]|1[012])/(0?[1-9]|1[0-9]|2[0-9]|3[01])$";
        return date.matches(datePattern);
    }

    //Cette méthode est utilisé pour afficher une alerte avec un message entré en paramètre
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //Les deux prochaines méthodes sont utilisées pour affichier une erreur
    //si la date saisi dans les textField ne correspondent pas au format souhaité
    @FXML
    public void textField1dOnAction() {
        String date = dateDebut.getText();
        if (!isValidDate(date)){
            showAlert("Format de date invalide !");
            dateDebut.setText(null);
        }
        actualiserIsOk();
    }
    @FXML
    public void textField2dOnAction() {
        String date = dateFin.getText();
        if (!isValidDate(date)){
            showAlert("Format de date invalide !");
            dateFin.setText(null);
        }
        else if (dateDebut.getText().compareTo(date) > 0){
            showAlert("La date de fin doit être supérieure à la date de début !");
            dateFin.setText(null);
        }
        actualiserIsOk();
    }

    @FXML
    public void reinitialiserOnAction(){
        regions.setValue(null);
        dateDebut.setText(null);
        dateFin.setText(null);
        sliderMin.setValue(2);
        sliderMax.setValue(12);
        actualiser.setDisable(true);
    }



    @FXML
    public void courbeOnAction(){
        /* lorsque l'on appuie sur le boutton courbe :
         - rendre le boutton courbe disable
         - rendre le boutton barchart able
         - on enlève tous les enfants de la HBox "bas" avec clear : bas.getChildren().clear();
         - déclarer la LineChart avec les bons paramètres
         - et on l'affiche dans la HBox : bas.getChildren().add(Nom de la LineChart);
        */
        courbeButton.setDisable(true);
        diagrammeaPoint.setDisable(false);
        bas.getChildren().clear();
        bas.getChildren().add(lineChart);
        lineChart.setPrefWidth(1100);
        lineChart.setPrefHeight(200);
        lineChart.setTitle("Nombres de seismes par années");
        yAxis.setLabel("Nombres");
        xAxis.setLabel("Années");
        xAxis.setLowerBound(1100);
        xAxis.setUpperBound(2100);
        lineChart.setStyle("-fx-background-color: white; -fx-background-radius: 15");
        lineChart.setLegendVisible(true);
        //data.prepdonneesCourbe(data.getDonnees(), data.getDateMin(), data.getDateMax(),
        //        data.getIntensiteMin(), data.getIntensiteMax());
    }
    @FXML
    public void diagrammeOnAction(){
        courbeButton.setDisable(false);
        diagrammeaPoint.setDisable(true);
        bas.getChildren().clear();
        bas.getChildren().add(diagrammePoint);
    }
    @FXML
    public void actualiserOnAction() throws ParseException {
        diagrammeaPoint.setDisable(true);
        courbeButton.setDisable(false);
        String[] anneeDebut = dateDebut.getText().split("/+");
        String[] anneeFin = dateFin.getText().split("/+");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        bas.getChildren().clear();
            SeismeData.getDonneesLineChart().clear();
            SeismeData.prepdonneesCourbe(Integer.parseInt(anneeDebut[0]), Integer.parseInt(anneeFin[0]), sliderMin.getValue(), sliderMax.getValue(), "");

            SeismeData.getDonneesScatterchart().clear();
            SeismeData.prepDonneesScatterchart(LocalDate.parse(dateDebut.getText(), formatter), LocalDate.parse(dateFin.getText(), formatter), sliderMin.getValue(), sliderMax.getValue(), "");
        bas.getChildren().add(diagrammePoint);

        carte.getChildren().clear();
            SeismeData.getPointMapDonnees().clear();
            //SeismeData.prepDonneesMap(LocalDate.parse(dateDebut.getText(), formatter), LocalDate.parse(dateFin.getText(), formatter), sliderMin.getValue(), sliderMax.getValue());
            SetPointOnMap(LocalDate.parse(dateDebut.getText(), formatter), LocalDate.parse(dateFin.getText(), formatter), sliderMin.getValue(), sliderMax.getValue(), "");
            carte.getChildren().add(Map);
    }

    //Initialise la carte
    public void InitMap(){
        System.setProperty("javafx.platform", "desktop");
        System.setProperty("http.agent", "Gluon Mobile/1.0.3");
        MapPoint centrerFrance = new MapPoint(46.227638, 2.213749);
        mapLayout = new CustomMapLayer();
        Map.addLayer(mapLayout);
        //mapLayout.ajouterPoint(centrerFrance, 5.0);
        Map.setCenter(centrerFrance);
        Map.setZoom(5);
    }

    //Affiche les points sur la carte selon les données et les filtres
    public void SetPointOnMap(LocalDate minDate, LocalDate maxDate, Double minIntensite, Double maxIntensite, String Region){
        data.prepDonneesMap(minDate, maxDate, minIntensite, maxIntensite, Region);
        mapLayout.misAJourList();
        for (int i=0; i < data.getPointMapDonnees().size(); i+=1){
            mapLayout.ajouterPoint(data.getPointMapDonnees().get(i).getKey(), data.getPointMapDonnees().get(i).getValue());
        }
    }

    public  void actualiserIsOk(){
        if(regions.getValue() != null && dateDebut.getText() != null && dateFin.getText() != null) {
            actualiser.setDisable(false);
        }
    }













}