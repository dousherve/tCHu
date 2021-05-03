package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public final class ObservableGameState {

    private PublicGameState gameState;
    private PlayerState playerState;

    private final PlayerId playerId;

    private final IntegerProperty ticketsPercentageProperty = new SimpleIntegerProperty(0);
    private final IntegerProperty cardsPercentageProperty = new SimpleIntegerProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards = createFaceUpCards();
    private final List<ObjectProperty<PlayerId>> routesOwners = createRoutesOwners();

    private final List<IntegerProperty> ticketsCounts;
    private final List<IntegerProperty> cardsCounts;
    private final List<IntegerProperty> carsCounts;
    private final List<IntegerProperty> claimPoints;

    private final ObservableList<Ticket> ticketsProperty;
    private final List<IntegerProperty> cardsCountPerType;
    private final List<BooleanProperty> routesClaimabilty;

    private static List<ObjectProperty<Card>> createFaceUpCards() {
        return Constants.FACE_UP_CARD_SLOTS.stream()
                .map(i -> new SimpleObjectProperty<Card>(null))
                .collect(Collectors.toList());
    }

    private static List<ObjectProperty<PlayerId>> createRoutesOwners() {
        return ChMap.routes().stream()
                .map(route -> new SimpleObjectProperty<PlayerId>(null))
                .collect(Collectors.toList());
    }

    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;

        List<IntegerProperty> defaultCountProperties =
                List.of(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        this.ticketsCounts = List.copyOf(defaultCountProperties);
        this.cardsCounts = List.copyOf(defaultCountProperties);
        this.carsCounts = List.copyOf(defaultCountProperties);
        this.claimPoints = List.copyOf(defaultCountProperties);

        this.ticketsProperty = FXCollections.observableArrayList();
        this.cardsCountPerType = Card.ALL.stream()
                .map(card -> new SimpleIntegerProperty(0))
                .collect(Collectors.toList());
        this.routesClaimabilty = ChMap.routes().stream()
                .map(route -> new SimpleBooleanProperty(false))
                .collect(Collectors.toList());
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        ticketsPercentageProperty.set(newGameState.ticketsCount() / ChMap.tickets().size());
        cardsPercentageProperty.set(newGameState.cardState().deckSize() / Constants.TOTAL_CARDS_COUNT);

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }

        for (int i = 0; i < ChMap.routes().size(); ++i) {
            final Route route = ChMap.routes().get(i);
            if (newGameState.claimedRoutes().contains(route)) {
                PlayerId claimer = newGameState.playerState(playerId).routes().contains(route)
                        ? playerId
                        : playerId.next();
                routesOwners.get(i).set(claimer);
            } else {
                routesOwners.get(i).set(null);
            }
        }

        for (PlayerId id : PlayerId.ALL) {
            final int index = id.ordinal();
            final PublicPlayerState currentPlayerState = newGameState.playerState(id);

            ticketsCounts.get(index)
                    .set(currentPlayerState.ticketCount());
            cardsCounts.get(index)
                    .set(currentPlayerState.cardCount());
            carsCounts.get(index)
                    .set(currentPlayerState.carCount());
            claimPoints.get(index)
                    .set(currentPlayerState.claimPoints());
        }

        ticketsProperty.setAll(newPlayerState.tickets().toList());

        for (Card c : Card.ALL) {
            cardsCountPerType.get(c.ordinal())
                    .set(newPlayerState.cards().countOf(c));
        }

        for (int i = 0; i < ChMap.routes().size(); ++i) {
            final Route route = ChMap.routes().get(i);

            final boolean claimable =
                    (playerId == newGameState.currentPlayerId())
                    && ! newGameState.claimedRoutes().contains(route) //TODO: FINIR POUR LES ROUTES DOUBLES !
                    && newPlayerState.canClaimRoute(route);
            routesClaimabilty.get(i).set(claimable);
        }

        this.gameState = newGameState;
        this.playerState = newPlayerState;
    }

    public boolean canDrawTickets() {
        return gameState.canDrawTickets();
    }

    public boolean canDrawCards() {
        return gameState.canDrawCards();
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }
}
