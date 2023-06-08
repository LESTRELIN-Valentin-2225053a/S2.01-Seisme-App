package com.example.s201;

import javafx.collections.FXCollections;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;

public class SeismeData implements Initializable {
    private static List<List<String>> Donnees = new ArrayList<>();
    // Les données sur les seismes

    private static ObservableList<XYChart.Data<Number, Number>> DonneesBarchart = FXCollections.observableArrayList();
    //Les données pour le barchart

    public static XYChart.Series<Number, Number> SerieDonneesBarchart = new XYChart.Series<>();
    //Contient toutes les données du barchart et permet l'évolution des données;

    //Cette methode permet de lire le fichier CSV donné,puis de le
    // transformer en une liste manipulable
    public static void lectureCSV(File fichier){
        Pattern separateur = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
        try (BufferedReader csvLecture = new BufferedReader (new FileReader(fichier))) {
            String ligne;
            while ((ligne = csvLecture.readLine()) != null){
                String[] Seisme = separateur.split(ligne);
                Donnees.add(Arrays.asList(Seisme));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Test pour vérifier que tout marche (à enlever plus tard)
 /*       for (int i = 0; i < Données.size(); i+=1){
            for (int y = 0; y < Données.get(i).size(); y+=1){
                System.out.println(Données.get(i).get(y));
            }
            System.out.println("Suivant");
        }*/
    }

    public void prepDonneesBarchart(List<List<String>> Donnees, int minDate,int maxDate,
                                    int minIntensité, int maxIntensité){
        if (DonneesBarchart.size() != 0)
            DonneesBarchart.removeAll();
        for (int i=0; i < Donnees.size(); i+=1){
            String[] tempDate = Donnees.get(i).get(1).split("/+");
            int tempAnnee = Integer.valueOf(tempDate[0]);
            int tempIntensité = Integer.valueOf(Donnees.get(i).get(Donnees.size()-2));
            if (tempIntensité >= minIntensité && tempIntensité <= maxIntensité
                    && tempAnnee >= minDate && tempAnnee <= maxDate){
                DonneesBarchart.add(new XYChart.Data<>(tempAnnee, tempIntensité));
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SerieDonneesBarchart.setData(DonneesBarchart);
    }
}
