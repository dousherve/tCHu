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
import static ch.epfl.tchu.gui.ConstantsGui.*;

/**
 * Classe finale et non instanciable permettant de créer la vue de la carte.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
final class MapViewCreator {

    private MapViewCreator() {}
    
    private static Group createRouteGroup(Route route) {
        Group routeGroup = new Group();
        routeGroup.setId(route.id());
        routeGroup.getStyleClass().addAll(
                ROUTE_CLASS,
                route.level().name(),
                route.color() == null
                        ? NEUTRAL_CLASS
                        : route.color().name()
        );
        
        return routeGroup;
    }
    
    private static Group createCellGroup() {
        Group cellGroup = new Group();
        
        // Rectangle de la voie
        Rectangle trackRect = new Rectangle(TRACK_WIDTH, TRACK_HEIGHT);
        trackRect.getStyleClass().addAll(TRACK_CLASS, FILLED_CLASS);
        
        // Groupe du wagon
        Group carGroup = new Group();
        carGroup.getStyleClass().add(CAR_CLASS);
        
        Rectangle carRect = new Rectangle(TRACK_WIDTH, TRACK_HEIGHT);
        carRect.getStyleClass().add(FILLED_CLASS);
        
        Circle circle1 = new Circle(CLAIMED_CIRCLE_CX, CLAIMED_CIRCLE_CY, CLAIMED_CIRCLE_RADIUS);
        Circle circle2 = new Circle(2 * CLAIMED_CIRCLE_CX, CLAIMED_CIRCLE_CY, CLAIMED_CIRCLE_RADIUS);
        
        // Ajout des enfants
        carGroup.getChildren().addAll(carRect, circle1, circle2);
        cellGroup.getChildren().addAll(trackRect, carGroup);
        
        return cellGroup;
    }
    
    /**
     * Interface fonctionnelle décrivant un
     * sélectionneur de cartes.
     */
    @FunctionalInterface
    interface CardChooser {
        /**
         * Méthode appelée lorsque le joueur doit choisir les cartes
         * qu'il désire utiliserpour s'emparer d'une route.
         * Les possibilités qui s'offrent à lui sont données par l'argument <code>options</code>,
         * tandis que le gestionnaire d'action <code>handler</code> est destiné
         * à être utilisé lorsqu'il a fait son choix.
         * 
         * @param options
         *          les possibilités parmi lesquelles le joueur peut choisir
         * @param handler
         *          le gestionnaire d'action à utiliser lorsque le joueur a fait son choix.
         */
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
    
    /**
     * Méthode permettant de créer la vue de la carte.
     * 
     * @param gameState
     *          l'état du jeu observable
     * @param claimRouteHP
     *          la propriété contenant le gestionnaire d'action à utiliser
     *          lorsque le joueur désire s'emparer d'une route
     * @param cardChooser
     *          le sélectionneur de cartes à utiliser
     * @return
     *          la vue de la carte de tCHu
     */
    public static Pane createMapView(ObservableGameState gameState, ObjectProperty<ClaimRouteHandler> claimRouteHP, CardChooser cardChooser) {
        Pane mapView = new Pane();
        mapView.getStylesheets().addAll(MAP_STYLES, COLORS_STYLES);

        ImageView mapImageView = new ImageView();
        mapView.getChildren().add(mapImageView);

        for (Route route : ChMap.routes()) {
            Group routeGroup = createRouteGroup(route);
            
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
                Group cellGroup = createCellGroup();
                cellGroup.setId(route.id() + "_" + i);
                routeGroup.getChildren().add(cellGroup);
            }
            
            mapView.getChildren().add(routeGroup);
        }

        return mapView;
    }
    
}
