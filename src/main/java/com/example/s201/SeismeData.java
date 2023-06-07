package com.example.s201;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SeismeData {
    private static List<List<String>> Données = new ArrayList<>(); // Les données sur les seismes

    //Le constructeur permet de lire le fichier CSV donné,puis de le
    // transformer en une liste manipulable
    public static void lectureCSV(File fichier){
        Pattern separateur = Pattern.compile(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");
        try (BufferedReader csvLecture = new BufferedReader (new FileReader(fichier))) {
            String ligne;
            while ((ligne = csvLecture.readLine()) != null){
                String[] Seisme = separateur.split(ligne);
                Données.add(Arrays.asList(Seisme));
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

}
