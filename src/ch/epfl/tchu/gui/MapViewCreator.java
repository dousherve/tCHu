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
    
    private static final int RECT_WIDTH = 36;
    private static final int RECT_HEIGHT = 12;
    private static final int CIRCLE_RADIUS = 3;
    private static final int CIRCLE_CENTER_X = 12;
    private static final int CIRCLE_CENTER_Y = 6;
    
    private static final String MAP_STYLES = "map.css";
    private static final String COLORS_STYLES = "colors.css";
    private static final String ROUTE_CLASS = "route";
    private static final String NEUTRAL_CLASS = "NEUTRAL";
    private static final String TRACK_CLASS = "track";
    private static final String FILLED_CLASS = "filled";
    private static final String CAR_CLASS = "car";
    
    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }

    static Pane createMapView(ObservableGameState gameState, ObjectProperty<ClaimRouteHandler> claimRouteHP, CardChooser cardChooser) {
        Pane mapView = new Pane();
        mapView.getStylesheets().addAll(MAP_STYLES, COLORS_STYLES);

        ImageView mapImageView = new ImageView();
        mapView.getChildren().add(mapImageView);

        for (Route route : ChMap.routes()) {
            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            routeGroup.getStyleClass().addAll(
                    ROUTE_CLASS,
                    route.level().name(),
                    route.color() == null
                            ? NEUTRAL_CLASS
                            : route.color().name()
            );
            
            gameState.routeOwner(route)
                    .addListener((o, oV, id) -> routeGroup.getStyleClass().add(id.name()));
    
            routeGroup.disableProperty().bind(
                    claimRouteHP.isNull().or(gameState.claimable(route).not()));
            
            routeGroup.setOnMouseClicked(e -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);
                ClaimRouteHandler claimRouteH = claimRouteHP.get();
                
                if (possibleClaimCards.size() == 1) {
                    claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                } else if (possibleClaimCards.size() > 1) {
                    cardChooser.chooseCards(
                            possibleClaimCards,
                            chosenCards -> claimRouteH.onClaimRoute(route, chosenCards)
                    );
                }
            });
            
            for (int i = 1; i <= route.length(); ++i) {
                Group cellGroup = new Group();
                cellGroup.setId(route.id() + "_" + i);

                // Rectangle de la voie
                Rectangle trackRect = new Rectangle(RECT_WIDTH, RECT_HEIGHT);
                trackRect.getStyleClass().addAll(TRACK_CLASS, FILLED_CLASS);

                // Groupe du wagon
                Group carGroup = new Group();
                carGroup.getStyleClass().add(CAR_CLASS);
    
                Rectangle carRect = new Rectangle(RECT_WIDTH, RECT_HEIGHT);
                carRect.getStyleClass().add(FILLED_CLASS);
    
                Circle circle1 = new Circle(CIRCLE_CENTER_X, CIRCLE_CENTER_Y, CIRCLE_RADIUS);
                Circle circle2 = new Circle(2 * CIRCLE_CENTER_X, CIRCLE_CENTER_Y, CIRCLE_RADIUS);
    
                // Ajout des enfants
                carGroup.getChildren().addAll(carRect, circle1, circle2);
                cellGroup.getChildren().addAll(trackRect, carGroup);
                routeGroup.getChildren().add(cellGroup);
            }

            mapView.getChildren().add(routeGroup);
        }

        return mapView;
    }

}
