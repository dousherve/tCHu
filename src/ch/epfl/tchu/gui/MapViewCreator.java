package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

import static ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import static ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;

final class MapViewCreator {

    private MapViewCreator() {}

    public static Pane createMapView(ObservableGameState ogs, ObjectProperty<ClaimRouteHandler> handler, CardChooser cardChooser) {
        Pane mapView = new Pane();
        mapView.getStylesheets().addAll("map.css", "colors.css");

        ImageView mapImageView = new ImageView();
        mapView.getChildren().add(mapImageView);

        for (Route route : ChMap.routes()) {
            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            routeGroup.getStyleClass().add("route");
            routeGroup.getStyleClass().add(route.level().name());
            routeGroup.getStyleClass().add(
                    route.color() == null
                            ? "NEUTRAL"
                            : route.color().name()
            );

            for (int i = 1; i <= route.length(); ++i) {
                Group cellGroup = new Group();
                cellGroup.setId(route.id() + "_" + i);

                // TODO: Créer des constantes
                // ---- Rectangle voie ----
                Rectangle trackRect = new Rectangle(36, 12);
                trackRect.getStyleClass().addAll("track", "filled");

                // ---- Groupe Wagon ----
                // TODO: si l'identité d'un joueur est attachée au wagon
                boolean visible = false;

                Group carGroup = new Group();
                carGroup.getStyleClass().add("car");
                carGroup.setVisible(visible);

                Rectangle carRect = new Rectangle(36, 12);
                carRect.getStyleClass().add("filled");

                Circle circle1 = new Circle(12, 6, 3);
                Circle circle2 = new Circle(24, 6, 3);

                // ---- Ajouts enfants ----
                carGroup.getChildren().addAll(carRect, circle1, circle2);
                cellGroup.getChildren().addAll(trackRect, carGroup);
                routeGroup.getChildren().add(cellGroup);
            }

            mapView.getChildren().add(routeGroup);
        }

        return mapView;
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }

}
