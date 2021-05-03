package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ObservableGameState {
    
    private static final List<Route> DOUBLE_ROUTES = computeDoubleRoutes();
    
    // MARK:- Attributs
    
    private PublicGameState gameState;
    private PlayerState playerState;
    
    private final PlayerId playerId;
    
    // MARK:- Propriétés de l'état de jeu observable

    private final IntegerProperty ticketsPercentageProperty;
    private final IntegerProperty cardsPercentageProperty;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routesOwners;

    private final Map<PlayerId, IntegerProperty> ticketsCounts;
    private final Map<PlayerId, IntegerProperty> cardsCounts;
    private final Map<PlayerId, IntegerProperty> carsCounts;
    private final Map<PlayerId, IntegerProperty> claimPoints;

    private final ObservableList<Ticket> ticketsProperty;
    private final Map<Card, IntegerProperty> cardsCountPerType;
    private final Map<Route, BooleanProperty> routesClaimabilty;
    
    // MARK:- Méthodes privées et statiques
    
    private static SimpleIntegerProperty defaultIntProperty() {
        return new SimpleIntegerProperty(0);
    }
    
    private static SimpleBooleanProperty defaultBoolProperty() {
        return new SimpleBooleanProperty(false);
    }
    
    private static List<Route> computeDoubleRoutes() {
        final Set<List<Station>> stationPairs = new HashSet<>();
        final List<Route> doubleRoutes = new ArrayList<>();
        for (Route r : ChMap.routes()) {
            if (! stationPairs.add(r.stations()))
                doubleRoutes.add(r);
        }
        
        return Collections.unmodifiableList(doubleRoutes);
    }

    private static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
        for (int slot : Constants.FACE_UP_CARD_SLOTS) 
            faceUpCards.add(new SimpleObjectProperty<>(null));
        
        return faceUpCards;
    }

    private static Map<Route, ObjectProperty<PlayerId>> createRoutesOwners() {
        Map<Route, ObjectProperty<PlayerId>> routesOwners = new HashMap<>();
        ChMap.routes().forEach(r -> routesOwners.put(r, new SimpleObjectProperty<>(null)));
        
        return routesOwners;
    }
    
    private static Map<PlayerId, IntegerProperty> defaultCountProperties() {
        final Map<PlayerId, IntegerProperty> defaultCounts = new EnumMap<>(PlayerId.class);
        PlayerId.ALL.forEach(id -> defaultCounts.put(id, new SimpleIntegerProperty(0)));
        
        return defaultCounts;
    }
    
    // MARK:- Méthodes privées
    
    private void updatePublicGameStateProperties(PublicGameState newGameState) {
        ticketsPercentageProperty.set(newGameState.ticketsCount() / ChMap.tickets().size());
        cardsPercentageProperty.set(newGameState.cardState().deckSize() / Constants.TOTAL_CARDS_COUNT);
        
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            final Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
        
        for (Route route : ChMap.routes()) {
            PlayerId claimer = null;
            if (newGameState.claimedRoutes().contains(route)) {
                claimer = newGameState.playerState(playerId).routes().contains(route)
                        ? playerId
                        : playerId.next();
            }
            routesOwners.get(route).set(claimer);
        }
    }
    
    private void updatePublicPlayerStatesProperties(PublicGameState newGameState) {
        for (PlayerId id : PlayerId.ALL) {
            final PublicPlayerState currentPlayerState = newGameState.playerState(id);
            
            ticketsCounts.get(id)
                    .set(currentPlayerState.ticketCount());
            cardsCounts.get(id)
                    .set(currentPlayerState.cardCount());
            carsCounts.get(id)
                    .set(currentPlayerState.carCount());
            claimPoints.get(id)
                    .set(currentPlayerState.claimPoints());
        }
    }
    
    private void updatePlayerStateProperties(PublicGameState newGameState, PlayerState newPlayerState) {
        ticketsProperty.setAll(newPlayerState.tickets().toList());
        
        for (Card card : Card.ALL) {
            cardsCountPerType
                    .get(card)
                    .set(newPlayerState.cards().countOf(card));
        }
        
        final List<Route> unclaimedRoutes = new ArrayList<>(ChMap.routes());
        unclaimedRoutes.removeAll(newGameState.claimedRoutes());
        
        for (Route route : unclaimedRoutes) {
            boolean isClaimable =
                    playerId == newGameState.currentPlayerId()
                    && newPlayerState.canClaimRoute(route);
            
            if (isClaimable && DOUBLE_ROUTES.contains(route)) {
                for (Route claimed : newGameState.claimedRoutes()) {
                    if (claimed.stations().containsAll(route.stations())) {
                        isClaimable = false;
                        break;
                    }
                }
            }
            
            routesClaimabilty.get(route).set(isClaimable);
        }
    }
    
    // MARK:- Contructeur et méthodes publiques

    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
        
        this.ticketsPercentageProperty = defaultIntProperty();
        this.cardsPercentageProperty = defaultIntProperty();
        this.faceUpCards = createFaceUpCards();
        this.routesOwners = createRoutesOwners();

        this.ticketsCounts = defaultCountProperties();
        this.cardsCounts = defaultCountProperties();
        this.carsCounts = defaultCountProperties();
        this.claimPoints = defaultCountProperties();

        this.ticketsProperty = FXCollections.observableArrayList();
        this.cardsCountPerType = Card.ALL.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        card -> defaultIntProperty())
                );
        this.routesClaimabilty = ChMap.routes().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        route -> defaultBoolProperty())
                );
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        updatePublicGameStateProperties(newGameState);
        updatePublicPlayerStatesProperties(newGameState);
        updatePlayerStateProperties(newGameState, newPlayerState);
    
        this.gameState = newGameState;
        this.playerState = newPlayerState;
    }
    
    // MARK:- Méthodes correspondant directement à l'état de jeu courant
    
    public boolean canDrawTickets() {
        return gameState.canDrawTickets();
    }

    public boolean canDrawCards() {
        return gameState.canDrawCards();
    }
    
    // MARK:- Méthodes correspondant directement à l'état du joueur courant

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }
    
    // MARK:- Méthodes d'accès en lecture seule aux propriétés
    
    public ReadOnlyIntegerProperty ticketsPercentage() {
        return ticketsPercentageProperty;
    }
    
    public ReadOnlyIntegerProperty cardsPercentage() {
        return cardsPercentageProperty;
    }
    
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }
    
    public ReadOnlyObjectProperty<PlayerId> routeOwner(Route route) {
        return routesOwners.get(route);
    }
    
    public ReadOnlyIntegerProperty ticketsCount(PlayerId id) {
        return ticketsCounts.get(id);
    }
    
    public ReadOnlyIntegerProperty cardsCount(PlayerId id) {
        return cardsCounts.get(id);
    }
    
    public ReadOnlyIntegerProperty carsCount(PlayerId id) {
        return carsCounts.get(id);
    }
    
    public ReadOnlyIntegerProperty claimPoints(PlayerId id) {
        return claimPoints.get(id);
    }
    
    public ObservableList<Ticket> tickets() {
        return FXCollections.unmodifiableObservableList(ticketsProperty);
    }
    
    public ReadOnlyIntegerProperty cardCount(Card card) {
        return cardsCountPerType.get(card);
    }
    
    public ReadOnlyBooleanProperty canClaimRoute(Route route) {
        return routesClaimabilty.get(route);
    }
    
}
