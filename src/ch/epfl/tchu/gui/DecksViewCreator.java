package ch.epfl.tchu.gui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import static ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import static ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;

final class DecksViewCreator {

    private DecksViewCreator() {}

    public static Node createHandView(ObservableGameState ogs) {
        HBox handView = new HBox();
        ListView ticketsView = new ListView();


        return null;
    }

    public static Node createCardsView(ObservableGameState ogs, ObjectProperty<DrawTicketsHandler> drawTickets, ObjectProperty<DrawCardHandler> drawCard) {
        return null;
    }

}
