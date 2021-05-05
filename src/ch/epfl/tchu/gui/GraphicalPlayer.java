package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.gui.ActionHandlers.*;

// TODO: regarder la visibilité
public final class GraphicalPlayer {

    private final ObservableGameState gameState;
    private List<String> messages;
    private final ObservableList<Text> infosText;

    private final ObjectProperty<DrawTicketsHandler> drawTicketsHP;
    private final ObjectProperty<DrawCardHandler> drawCardHP;
    private final ObjectProperty<ClaimRouteHandler> claimRouteHP;

    private static <T> SimpleObjectProperty<T> createObjectProperty() {
        return new SimpleObjectProperty<>(null);
    }

    private static ObservableList<Text> createInfosTexts() {
        ObservableList<Text> list = FXCollections.observableArrayList();

        for (int i = 0; i < 5; ++i)
            list.add(new Text());

        return list;
    }

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.gameState = new ObservableGameState(playerId);
        this.infosText = createInfosTexts();
        this.messages = new ArrayList<>();
        this.drawTicketsHP = createObjectProperty();
        this.drawCardHP = createObjectProperty();
        this.claimRouteHP = createObjectProperty();
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        gameState.setState(newGameState, newPlayerState);
    }

    public void receiveInfo(String info) {
        messages.add(info);
        if (messages.size() > 5)
            messages = messages.subList(messages.size() - 5, messages.size());

        for (int i = 0; i < messages.size(); ++i)
            infosText.get(i).setText(messages.get(i));
    }

    public void startTurn(DrawTicketsHandler drawTicketsH, DrawCardHandler drawCardH, ClaimRouteHandler claimRouteH) {
        drawTicketsHP.set(gameState.canDrawTickets() ? drawTicketsH : null);
        drawCardHP.set(gameState.canDrawCards() ? drawCardH : null);
        claimRouteHP.set(claimRouteH);
    }

    public void chooseTickets(SortedBag<Ticket> drawnTickets, DrawTicketsHandler drawTicketsH) {
        Preconditions.checkArgument(drawnTickets.size() == 3 || drawnTickets.size() == 5);

    }

    public void drawCard(DrawCardHandler drawCardH) {

    }

    public void chooseClaimCards(SortedBag<Card> initialCards, ChooseCardsHandler chooseCardsH) {

    }

    public void chooseAdditionalCards(List<SortedBag<Card>> options, ChooseCardsHandler chooseCardsH) {

    }

}
