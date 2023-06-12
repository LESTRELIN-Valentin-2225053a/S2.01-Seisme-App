package com.example.s201;

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

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.util.Pair;

public  class  SeismeData { 

    @FXML
    private Label moyenne;

    private  List<List<String>> Donnees = new ArrayList<>();
    // Les données sur les seismes

    //Ces 4 attributs sont les filtres par defaut par rapport au fichier
    private  LocalDate dateMin;
    private  LocalDate dateMax;
    private  Double intensiteMin;
    private  Double intensiteMax;


    private  ObservableList<XYChart.Data<String, Number>> DonneesScatterchart = FXCollections.observableArrayList();
    //Les données pour le barchart

    public  XYChart.Series<String, Number> SerieDonneesScatterchart = new XYChart.Series<String, Number>();
    //Contient toutes les données du barchart et permet l'évolution des données;

    private  ObservableList<PieChart.Data> DonneesCamembert = FXCollections.observableArrayList(
            new PieChart.Data("Segment 1", 30),
            new PieChart.Data("Segment 2", 20),
            new PieChart.Data("Segment 3", 50)
    );
    // Les données pour le camembert

    private  List<Pair<Number,LocalDate>> dateDonnees = new ArrayList<>();
    //Les dates contenues dans les données avec leurs identifiants, mais mis tout au format "yyyy/MM/dd",
    // c'est à dire avec des valeurs par défaut

    private static ObservableList<XYChart.Data<Number, Number>> DonneesLineChart = FXCollections.observableArrayList();
    // Les données pour la LineChart

    public  XYChart.Series<Number, Number> SerieDonneesLineChart = new XYChart.Series<Number, Number>();

    public void minMaxFiltre() throws ParseException {
        if (Donnees != null && !Donnees.isEmpty()) {
            List<String> dateString = new ArrayList<>();
            List<Double> intensiteTri = new ArrayList<>();

            for (int i = 0; i < Donnees.size(); i++) {
                if (Donnees.get(i).get(1) == "" || Donnees.get(i).get(Donnees.get(i).size() - 2) == "")
                    continue;
                else {
                    String tempDateString = Donnees.get(i).get(1);
                    dateString.add(tempDateString);
                    intensiteTri.add(Double.valueOf(Donnees.get(i).get(Donnees.get(i).size() - 2)));
                }
            }

            Collections.sort(intensiteTri);
            intensiteMin = intensiteTri.get(0);
            intensiteMax = intensiteTri.get(intensiteTri.size() - 1);

            for (int index = 0; index < dateString.size(); index++) {
                dateDonnees.add(new Pair<>(Integer.valueOf(Donnees.get(index).get(0)), parseDate(dateString.get(index))));
            }

            // Trie par rapport aux Dates
            Collections.sort(dateDonnees, new Comparator<Pair<Number, LocalDate>>() {
                @Override
                public int compare(Pair<Number, LocalDate> pair1, Pair<Number, LocalDate> pair2) {
                    return pair1.getValue().compareTo(pair2.getValue());
                }
            });

            dateMin = dateDonnees.get(0).getValue();
            dateMax = dateDonnees.get(dateDonnees.size() - 1).getValue();
        }
    }

    private LocalDate parseDate(String dateStr) {
        try {
            if (dateStr.matches("\\d{4}/\\d{2}/\\d{2}")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            } else if (dateStr.matches("\\d{4}/\\d{2}")) {
                return LocalDate.parse(dateStr + "/01", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            } else if (dateStr.matches("\\d{4}/")) {
                return LocalDate.parse(dateStr + "01/01", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            }
        } catch (Exception e) {
            // Gérer les erreurs de parsing ici
            System.out.println("Erreur de parsing pour la date : " + dateStr);
        }
        return null;
    }


    //Cette methode permet de lire le fichier CSV donné,puis de le
    // transformer en une liste manipulable
    public void lectureCSV(File fichier){
        Pattern separateur = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
        boolean premiereLigne = true;
        try (BufferedReader csvLecture = new BufferedReader (new FileReader(fichier))) {
            String ligne;
            while ((ligne = csvLecture.readLine()) != null){
                if (premiereLigne) {
                    premiereLigne = false;
                    continue; // Ignorer la première ligne
                }
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

    public void prepDonneesScatterchart(List<List<String>> Donnees, LocalDate minDate, LocalDate maxDate,
                                           Double minIntensite, Double maxIntensite) throws ParseException {
        if (DonneesScatterchart.size() != 0)
            DonneesScatterchart.removeAll();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        ArrayList<Pair<LocalDate, Double>> donneesTemp = new ArrayList<>();
        for (int i=0; i < Donnees.size(); i+=1){
            if (Donnees.get(i).get(1) == "" || Donnees.get(i).get(Donnees.get(i).size() - 2) == "") {
                continue;
            }
            else {
                if (Double.valueOf(Donnees.get(i).get(Donnees.get(i).size() - 2)) <= 12 && Double.valueOf(Donnees.get(i).get(Donnees.get(i).size() - 2)) >= 0){
                    LocalDate tempDate = parseDate(Donnees.get(i).get(1));
                    Double tempIntensite = Double.valueOf(Donnees.get(i).get(Donnees.get(i).size()-2));
                    donneesTemp.add(new Pair<>(tempDate, tempIntensite));
                }
                else
                   continue;
            }
        }
        //Trie de la liste de données pour permttre un affichage trié dans le graphique
        Collections.sort(donneesTemp, new Comparator<Pair<LocalDate, Double>>() {
            @Override
            public int compare(Pair<LocalDate, Double> pair1, Pair<LocalDate, Double> pair2) {
                return pair1.getKey().compareTo(pair2.getKey());
            }
        });
        for (Pair<LocalDate,Double> seisme : donneesTemp){
            if (seisme.getValue() >= minIntensite && seisme.getValue() <= maxIntensite) {
                assert seisme.getKey() != null;
                if (seisme.getKey().isAfter(minDate) || seisme.getKey().isEqual(minDate)
                        && seisme.getKey().isBefore(maxDate) || seisme.getKey().isEqual(maxDate)) {
                    DonneesScatterchart.add(new XYChart.Data<>(String.valueOf(seisme.getKey()),seisme.getValue()));
                }
        }

        }
    }

    //Sert à la visualisation des données pour le graphique en camembert.
    public void prepDonneesCamembert(List<List<String>> Donnees, int minIntensite, int maxIntensite) {
        if (DonneesCamembert.size() != 0)
            DonneesCamembert.removeAll();
        int count = 0;
        int totalIntensite = 0; // Ajout de cette variable pour calculer la somme des intensités

        for (int i = 0; i < Donnees.size(); i += 1) {
            int tempIntensite = Integer.valueOf(Donnees.get(i).get(Donnees.size() - 2));
            if (tempIntensite >= minIntensite && tempIntensite <= maxIntensite) {
                count++;
                totalIntensite += tempIntensite; // Ajout de l'intensité à la somme totale
            }
        }

        for (int i = 0; i < Donnees.size(); i += 1) {
            int tempIntensite = Integer.valueOf(Donnees.get(i).get(Donnees.size() - 2));
            if (tempIntensite >= minIntensite && tempIntensite <= maxIntensite) {
                double percentage = (double) count / Donnees.size() * 100;
                String categorie = "Catégorie " + (i + 1);
                DonneesCamembert.add(new PieChart.Data(categorie, percentage));
            }
        }

        // Calcul de la moyenne des intensités correspondant aux critères
        double moyenneIntensite = calculateMoyenneIntensite(Donnees, minIntensite, maxIntensite);

        // Mise à jour du texte du label moyenne avec la valeur calculée
        moyenne.setText(String.format("%.2f", moyenneIntensite));

    }

   // @Override
   // public void initialize(URL url, ResourceBundle resourceBundle) {
   //     SerieDonneesBarchart.setData(DonneesBarchart);
   // }

    //Récupère les données du graphique en camembert.
    public ObservableList<PieChart.Data> getDonneesCamembert() {
        return DonneesCamembert;
    }
    private double calculateMoyenneIntensite(List<List<String>> Donnees, int minIntensite, int maxIntensite) {
        int count = 0;
        int sum = 0;

        for (int i = 0; i < Donnees.size(); i++) {
            int tempIntensite = Integer.parseInt(Donnees.get(i).get(Donnees.size() - 2));
            if (tempIntensite >= minIntensite && tempIntensite <= maxIntensite) {
                count++;
                sum += tempIntensite;
            }
        }

        if (count > 0) {
            return (double) sum / count;
        } else {
            return 0.0;
        }
    }


    public static void prepdonneesCourbe(List<List<String>> Donnees, int minDate, int maxDate,
                                         int minIntensite, int maxIntensite){
        if (DonneesLineChart.size() != 0)
            DonneesLineChart.removeAll();
        for (int i=0; i < Donnees.size(); i+=1) {
            String[] tempDate = Donnees.get(i).get(1).split("/+");
            int tempAnnee = Integer.valueOf(tempDate[0]);
            int tempIntensité = Integer.valueOf(Donnees.get(i).get(Donnees.size() - 2));
            if (tempIntensité >= minIntensite && tempIntensité <= maxIntensite
                    && tempAnnee >= minDate && tempAnnee <= maxDate) {
                DonneesLineChart.add(new XYChart.Data<>(tempAnnee, tempIntensité));
            }
        }
    }

    public  List<List<String>> getDonnees() {
        return Donnees;
    }

    public  LocalDate getDateMin() {
        return dateMin;
    }

    public  void setDateMin(LocalDate dateMin) {
        this.dateMin = dateMin;
    }

    public  LocalDate getDateMax() {
        return dateMax;
    }

    public  void setDateMax(LocalDate dateMax) {
        this.dateMax = dateMax;
    }

    public  Double getIntensiteMin() {
        return intensiteMin;
    }

    public  void setIntensiteMin(Double intensiteMin) {
        this.intensiteMin = intensiteMin;
    }

    public  Double getIntensiteMax() {
        return intensiteMax;
    }

    public void setIntensiteMax(Double intensiteMax) {
        this.intensiteMax = intensiteMax;
    }

    public ObservableList<XYChart.Data<String, Number>> getDonneesScatterchart() {
        return DonneesScatterchart;
    }
}
