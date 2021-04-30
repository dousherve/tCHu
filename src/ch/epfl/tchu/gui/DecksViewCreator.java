package ch.epfl.tchu.gui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

import static ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import static ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;

final class DecksViewCreator {

    private DecksViewCreator() {}

    public static Node createCardsView(ObservableGameState ogs, ObjectProperty<DrawTicketsHandler> drawTickets, ObjectProperty<DrawCardHandler> drawCard) {
        return null;
    }

    public static Node createHandView(ObservableGameState ogs) {
        return null;
    }

}
