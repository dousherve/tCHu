package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.ImageView;

import java.util.List;

import static ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import static ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;

final class MapViewCreator {

    private MapViewCreator() {}

    public static ImageView createMapView(ObservableGameState ogs, ObjectProperty<ClaimRouteHandler> handler, CardChooser cardChooser) {
        return new ImageView("map.png");
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }

}
