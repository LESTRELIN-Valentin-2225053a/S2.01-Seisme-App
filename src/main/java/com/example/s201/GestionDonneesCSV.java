package com.example.s201;

import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

public class GestionDonneesCSV {
    private static List<List<String>> Donnees = new ArrayList<>();
    // Les données sur les seismes
    //Ces 4 attributs sont les filtres par defaut par rapport au fichier
    private static List<Number> identifiantDonnees = new ArrayList<>();
    private static List<LocalDate> dateDonnees = new ArrayList<>();
    //Les dates contenues dans les données avec leurs identifiants, mais mis tout au format "yyyy/MM/dd",
    // c'est à dire avec des valeurs par défaut
    private static List<String> regionDonnees = new ArrayList<>();
    private static List<Double> intensiteDonnees = new ArrayList<>();
    private static List<Pair<Double, Double>> posGPSDonnees = new ArrayList<>();

    private static LocalDate dateMin;
    private static LocalDate dateMax;
    private static Double intensiteMin;
    private static Double intensiteMax;

    //Cette methode permet de lire le fichier CSV donné,puis de le
    // transformer en une liste manipulable
    public static void lectureCSV(File fichier) throws ParseException {
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
        for (int i=0; i < Donnees.size(); i+=1){
            identifiantDonnees.add(Integer.valueOf(Donnees.get(i).get(0)));
            dateDonnees.add(parseDate(Donnees.get(i).get(1)));
            regionDonnees.add(Donnees.get(i).get(4));
            intensiteDonnees.add(Double.valueOf(Donnees.get(i).get(10)));
            if (Donnees.get(i).get(8).equals("") || Donnees.get(i).get(9).equals(""))
                posGPSDonnees.add(new Pair<>(null,null));
            else {
                posGPSDonnees.add(new Pair<>(Double.valueOf(Donnees.get(i).get(8)),
                        Double.valueOf(Donnees.get(i).get(9))));
            }
        }
        minMaxFiltre();
    }

    //Permet d'avoir les filtres par défaut du fichier, c'est à dire le minimum est le maximum du csv
    public static void minMaxFiltre() throws ParseException {
        List<Double> intensiteTri = new ArrayList<>();
        List<LocalDate> dateTri = new ArrayList<>();
        for (int i=0; i < intensiteDonnees.size(); i+=1) {
            intensiteTri.add(intensiteDonnees.get(i));
            dateTri.add(dateDonnees.get(i));
        }
        Collections.sort(intensiteTri);
        intensiteMin = intensiteTri.get(0);
        intensiteMax = intensiteTri.get(intensiteTri.size() - 1);
        Collections.sort(dateTri, new Comparator<LocalDate>() {
            @Override
            public int compare(LocalDate pair1, LocalDate pair2) {
                return pair1.compareTo(pair2);
            }
        });
        dateMin = dateTri.get(0);
        dateMax = dateTri.get(dateDonnees.size() - 1);
    }

    //Permet le formatage de date, sous le format "yyyy/MM/dd"
    public static LocalDate parseDate(String dateStr) {
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

    public static List<List<String>> getDonnees() {
        return Donnees;
    }

    public static List<Number> getIdentifiantDonnees() {
        return identifiantDonnees;
    }

    public static List<LocalDate> getDateDonnees() {
        return dateDonnees;
    }

    public static List<String> getRegionDonnees() {
        return regionDonnees;
    }

    public static List<Double> getIntensiteDonnees() {
        return intensiteDonnees;
    }

    public static List<Pair<Double, Double>> getPosGPSDonnees() {
        return posGPSDonnees;
    }

    public static LocalDate getDateMin() {
        return dateMin;
    }

    public static LocalDate getDateMax() {
        return dateMax;
    }

    public static Double getIntensiteMin() {
        return intensiteMin;
    }

    public static Double getIntensiteMax() {
        return intensiteMax;
    }
}
