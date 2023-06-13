package com.example.s201;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CustomMapLayer extends MapLayer {
    private ObservableList<MapPoint> mapPoint;
    private ObservableList<Circle> cerclePoint;

    public CustomMapLayer() {
        this.mapPoint = FXCollections.observableArrayList();
        this.cerclePoint = FXCollections.observableArrayList();
    }

    public void ajouterPoint(MapPoint mapPoint, Double intensite){
        this.mapPoint.add(mapPoint);
        Circle cercleTemp = new Circle(5, Color.YELLOW);
        if (intensite >= 0 && intensite <= 4) {
            cercleTemp.setFill(Color.YELLOW);
            cercleTemp.setStroke(Color.YELLOW);
        }
        else if (intensite > 4 && intensite <= 8) {
            cercleTemp.setFill(Color.ORANGE);
            cercleTemp.setStroke(Color.ORANGE);
        }
        else if (intensite > 8 && intensite <= 12) {
            cercleTemp.setFill(Color.RED);
            cercleTemp.setStroke(Color.RED);
        }
        this.cerclePoint.add(cercleTemp);
        this.getChildren().setAll(cerclePoint);
    }

    /* La fonction est appelée à chaque rafraichissement de la carte */
    @Override
    protected void layoutLayer() {
        /* Conversion du MapPoint vers Point2D */
        Point2D point2d;
        for (int i=0; i < mapPoint.size(); i+=1){
            point2d = getMapPoint(mapPoint.get(i).getLatitude(), mapPoint.get(i).getLongitude());

            /* Déplace l'épingle selon les coordonnées du point */
            cerclePoint.get(i).setTranslateX(point2d.getX());
            cerclePoint.get(i).setTranslateY(point2d.getY());
        }
    }
}