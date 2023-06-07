package com.example.s201;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;

public class SeismeEvent {
    @FXML
    private Label welcomeText;
    @FXML
    private BorderPane root;

    //Cette fonction est utilisé par le bouton "Choisir Données" et sert à
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