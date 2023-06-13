package com.example.s201;

import com.gluonhq.maps.MapPoint;
import javafx.collections.FXCollections;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.Pair;

public  class  SeismeData { 

    @FXML
    private Label moyenne;

    private static ObservableList<XYChart.Data<String, Number>> DonneesScatterchart = FXCollections.observableArrayList();
    //Les données pour le barchart

    public static XYChart.Series<String, Number> SerieDonneesScatterchart = new XYChart.Series<String, Number>();
    //Contient toutes les données du barchart et permet l'évolution des données;

    private static ObservableList<PieChart.Data> DonneesCamembert = FXCollections.observableArrayList(
            new PieChart.Data("Segment 1", 30),
            new PieChart.Data("Segment 2", 20),
            new PieChart.Data("Segment 3", 50)
    );
    // Les données pour le camembert

    private static ObservableList<XYChart.Data<Number, Number>> DonneesLineChart = FXCollections.observableArrayList();
    // Les données pour la LineChart

    public static XYChart.Series<Number, Number> SerieDonneesLineChart = new XYChart.Series<Number, Number>();

    public static ObservableList<Pair<MapPoint,Double>> pointMapDonnees = FXCollections.observableArrayList();

//*****************************************************************************************************
 //**************************************     ScatterChart     *********************************************

    //Cette fonction prépare les données pour le scatterChart
    public static void prepDonneesScatterchart(LocalDate minDate, LocalDate maxDate,
                                               Double minIntensite, Double maxIntensite, String Region) throws ParseException {
        if (DonneesScatterchart.size() != 0)
            DonneesScatterchart.removeAll();
        ArrayList<Pair<LocalDate, Double>> donneesTemp = new ArrayList<>();
        if (Region.equals("")) {
            for (int i = 0; i < GestionDonneesCSV.getDonnees().size(); i += 1) {
                if (GestionDonneesCSV.getDateDonnees().get(i) == null || GestionDonneesCSV.getIntensiteDonnees().get(i) == null) {
                    continue;
                } else {
                    if (GestionDonneesCSV.getIntensiteDonnees().get(i) <= 12 && GestionDonneesCSV.getIntensiteDonnees().get(i) >= 0) {
                        LocalDate tempDate = GestionDonneesCSV.getDateDonnees().get(i);
                        Double tempIntensite = GestionDonneesCSV.getIntensiteDonnees().get(i);
                        donneesTemp.add(new Pair<>(tempDate, tempIntensite));
                    } else
                        continue;
                }
            }
        } else {
            for (int i = 0; i < GestionDonneesCSV.getDonnees().size(); i += 1) {
                if (GestionDonneesCSV.getDateDonnees().get(i) == null || GestionDonneesCSV.getIntensiteDonnees().get(i) == null) {
                    continue;
                } else {
                    if (GestionDonneesCSV.getIntensiteDonnees().get(i) <= maxIntensite && GestionDonneesCSV.getIntensiteDonnees().get(i) >= minIntensite
                            && (GestionDonneesCSV.getDateDonnees().get(i).isAfter(minDate) && GestionDonneesCSV.getDateDonnees().get(i).isBefore(maxDate))
                            && GestionDonneesCSV.getRegionDonnees().get(i).equals(Region)) {
                        LocalDate tempDate = GestionDonneesCSV.getDateDonnees().get(i);
                        Double tempIntensite = GestionDonneesCSV.getIntensiteDonnees().get(i);
                        donneesTemp.add(new Pair<>(tempDate, tempIntensite));
                    } else
                        continue;
                }
            }
        }
        //Trie de la liste de données pour permettre un affichage trié dans le graphique
        Collections.sort(donneesTemp, new Comparator<Pair<LocalDate, Double>>() {
            @Override
            public int compare(Pair<LocalDate, Double> pair1, Pair<LocalDate, Double> pair2) {
                return pair1.getKey().compareTo(pair2.getKey());
            }
        });
        for (Pair<LocalDate, Double> seisme : donneesTemp) {
            if (seisme.getValue() >= minIntensite && seisme.getValue() <= maxIntensite) {
                assert seisme.getKey() != null;
                if (seisme.getKey().isAfter(minDate) || seisme.getKey().isEqual(minDate)
                        && seisme.getKey().isBefore(maxDate) || seisme.getKey().isEqual(maxDate)) {
                    DonneesScatterchart.add(new XYChart.Data<>(String.valueOf(seisme.getKey()), seisme.getValue()));
                }
            }
        }
    }

    //*****************************************************************************************************
    //**************************************     PieChart     *********************************************

    //Sert à la visualisation des données pour le graphique en camembert.
    public void prepDonneesCamembert(List<List<String>> Donnees, int minIntensite, int maxIntensite) {
        if (DonneesCamembert.size() != 0)
            DonneesCamembert.clear();

        Map<String, Integer> intensiteCountMap = new HashMap<>();

        for (List<String> donnee : Donnees) {
            Double intensite = Double.valueOf(donnee.get(donnee.size() - 2));

            if (intensite >= minIntensite && intensite <= maxIntensite) {
                String intensiteStr = String.valueOf(intensite);

                // Vérifier si l'intensité existe déjà dans le map
                if (intensiteCountMap.containsKey(intensiteStr)) {
                    // Si oui, incrémenter le compteur correspondant
                    intensiteCountMap.put(intensiteStr, intensiteCountMap.get(intensiteStr) + 1);
                } else {
                    // Sinon, ajouter une nouvelle entrée dans le map avec un compteur initial de 1
                    intensiteCountMap.put(intensiteStr, 1);
                }
            }
        }

        // Convertir le map en une liste de données pour le PieChart
        for (Map.Entry<String, Integer> entry : intensiteCountMap.entrySet()) {
            String intensite = entry.getKey();
            Integer count = entry.getValue();
            DonneesCamembert.add(new PieChart.Data(intensite, count));
        }
    }

    //*****************************************************************************************************
    //**************************************     LineChart     *********************************************

    //Cette fonction prepare les donnees pour le linechart, qui montre le nombre de Seismes par années
    public static void prepdonneesCourbe(Integer minDate, Integer maxDate, Double minIntensite, Double maxIntensite, String Region){
        if (DonneesLineChart.size() != 0)
            DonneesLineChart.removeAll();
        List<String> listDateDonnees = new ArrayList<>();
        if (Region.equals("")) {
            for (int i = 0; i < GestionDonneesCSV.getDateDonnees().size(); i += 1) {
                if (GestionDonneesCSV.getDateDonnees().get(i) != null || GestionDonneesCSV.getIntensiteDonnees().get(i) != null) {
                    String[] tempDateStr = String.valueOf(GestionDonneesCSV.getDateDonnees().get(i)).split("-+");
                    Integer tempDate = Integer.valueOf(tempDateStr[0]);
                    if ((tempDate >= minDate && tempDate <= maxDate)
                            && (GestionDonneesCSV.getIntensiteDonnees().get(i) >= minIntensite && GestionDonneesCSV.getIntensiteDonnees().get(i) <= maxIntensite)) {
                        String AnneeStr = String.valueOf(tempDate);
                        listDateDonnees.add(AnneeStr);
                    }
                }
            }
        }
        else {
            for (int i = 0; i < GestionDonneesCSV.getDateDonnees().size(); i += 1) {
                if (GestionDonneesCSV.getDateDonnees().get(i) != null || GestionDonneesCSV.getIntensiteDonnees().get(i) != null) {
                    String[] tempDateStr = String.valueOf(GestionDonneesCSV.getDateDonnees().get(i)).split("-+");
                    Double tempDate = Double.valueOf(tempDateStr[0]);
                    if ((tempDate >= minDate && tempDate <= maxDate)
                            && (GestionDonneesCSV.getIntensiteDonnees().get(i) >= minIntensite && GestionDonneesCSV.getIntensiteDonnees().get(i) <= maxIntensite)
                            && GestionDonneesCSV.getRegionDonnees().get(i).equals(Region)) {
                        String AnneeStr = String.valueOf(tempDate);
                        listDateDonnees.add(AnneeStr);
                    }
                }
            }
        }
        List<XYChart.Data<Integer, Integer>> nbSeimeParAn = new ArrayList<>();
        listDateDonnees.stream()
                .collect(Collectors.groupingBy(s -> s))
                .forEach((k, v) -> nbSeimeParAn.add(new XYChart.Data<>(Integer.valueOf(k), v.size())));
        for (XYChart.Data nbseisme : nbSeimeParAn)
            DonneesLineChart.add(nbseisme);
    }

    //*****************************************************************************************************
    //**************************************     Carte     *********************************************

    //Prépare les données pour les points de la carte
    public static void prepDonneesMap(LocalDate minDate, LocalDate maxDate, Double minIntensite, Double maxIntensite, String Region){
        if(pointMapDonnees.size()!= 0)
            pointMapDonnees.removeAll();
        if (Region.equals("")) {
            for (int i = 0; i < GestionDonneesCSV.getDonnees().size(); i += 1) {
                if ((GestionDonneesCSV.getIntensiteDonnees().get(i) >= minIntensite && GestionDonneesCSV.getIntensiteDonnees().get(i) <= maxIntensite)
                        && (GestionDonneesCSV.getDateDonnees().get(i).isAfter(minDate) && GestionDonneesCSV.getDateDonnees().get(i).isBefore(maxDate))) {
                    if (GestionDonneesCSV.getPosGPSDonnees().get(i).getKey() != null || GestionDonneesCSV.getPosGPSDonnees().get(i).getValue() != null) {
                        MapPoint pointTemp = new MapPoint(GestionDonneesCSV.getPosGPSDonnees().get(i).getKey(),
                                GestionDonneesCSV.getPosGPSDonnees().get(i).getValue());
                        pointMapDonnees.add(new Pair<>(pointTemp, GestionDonneesCSV.getIntensiteDonnees().get(i)));
                    }
                }
            }
        }
        else {
            for (int i = 0; i < GestionDonneesCSV.getDonnees().size(); i += 1) {
                if ((GestionDonneesCSV.getIntensiteDonnees().get(i) >= minIntensite && GestionDonneesCSV.getIntensiteDonnees().get(i) <= maxIntensite)
                        && (GestionDonneesCSV.getDateDonnees().get(i).isAfter(minDate) && GestionDonneesCSV.getDateDonnees().get(i).isBefore(maxDate))
                        && GestionDonneesCSV.getRegionDonnees().get(i).equals(Region)) {
                    if (GestionDonneesCSV.getPosGPSDonnees().get(i).getKey() != null || GestionDonneesCSV.getPosGPSDonnees().get(i).getValue() != null) {
                        MapPoint pointTemp = new MapPoint(GestionDonneesCSV.getPosGPSDonnees().get(i).getKey(),
                                GestionDonneesCSV.getPosGPSDonnees().get(i).getValue());
                        pointMapDonnees.add(new Pair<>(pointTemp, GestionDonneesCSV.getIntensiteDonnees().get(i)));
                    }
                }
            }
        }
    }

    //*****************************************************************************************************
    //**************************************     Getter     *********************************************

    public static ObservableList<XYChart.Data<String, Number>> getDonneesScatterchart() {
        return DonneesScatterchart;
    }

    public static ObservableList<XYChart.Data<Number, Number>> getDonneesLineChart() {
        return DonneesLineChart;
    }

    public static ObservableList<Pair<MapPoint,Double>> getPointMapDonnees() {
        return pointMapDonnees;
    }
}
