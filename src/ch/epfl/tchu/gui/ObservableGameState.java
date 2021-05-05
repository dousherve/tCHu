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

/**
 * Classe finale instanciable représentant l'état observable
 * d'une partie de tCHu.
 * Une instance de cette classe contient la partie publique
 * de l'état du jeu, ainsi que la totalité de l'état du joueur
 * auquel l'interface graphique utilisant cette instance correspond.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
final class ObservableGameState {
    
    private static final Set<List<Station>> DOUBLE_ROUTES_STATIONS = computeDoubleRoutesStations();
    
    // MARK:- Attributs
    
    private PublicGameState gameState;
    private PlayerState playerState;
    
    private final PlayerId playerId;
    
    // MARK:- Propriétés de l'état de jeu observable

    private final IntegerProperty ticketsPercentageProperty;
    private final IntegerProperty cardsPercentageProperty;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routesOwners;

    private final Map<PlayerId, IntegerProperty> ticketCounts;
    private final Map<PlayerId, IntegerProperty> cardCounts;
    private final Map<PlayerId, IntegerProperty> carCounts;
    private final Map<PlayerId, IntegerProperty> claimPoints;

    private final ObservableList<Ticket> ticketsProperty;
    private final Map<Card, IntegerProperty> cardCountsPerType;
    private final Map<Route, BooleanProperty> routesClaimability;
    
    // MARK:- Méthodes utilitaires privées et statiques
    
    private static SimpleIntegerProperty createIntProperty() {
        return new SimpleIntegerProperty(0);
    }
    
    private static SimpleBooleanProperty createBoolProperty() {
        return new SimpleBooleanProperty(false);
    }
    
    private static <T> SimpleObjectProperty<T> createObjectProperty() {
        return new SimpleObjectProperty<>(null);
    }
    
    private static Set<List<Station>> computeDoubleRoutesStations() {
        final Set<List<Station>> stationPairs = new HashSet<>();
        final Set<List<Station>> doubleRoutesStations = new HashSet<>();
        for (Route r : ChMap.routes()) {
            if (! stationPairs.add(r.stations()))
                doubleRoutesStations.add(r.stations());
        }
        
        return Collections.unmodifiableSet(doubleRoutesStations);
    }

    private static List<ObjectProperty<Card>> createFaceUpCardsProperties() {
        List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
        for (int slot : Constants.FACE_UP_CARD_SLOTS) 
            faceUpCards.add(createObjectProperty());
        
        return faceUpCards;
    }

    private static Map<Route, ObjectProperty<PlayerId>> createRoutesOwnersProperties() {
        Map<Route, ObjectProperty<PlayerId>> routesOwners = new HashMap<>();
        for (Route r : ChMap.routes())
            routesOwners.put(r, createObjectProperty());
        
        return routesOwners;
    }
    
    private static Map<PlayerId, IntegerProperty> createCountsProperties() {
        final Map<PlayerId, IntegerProperty> defaultCounts = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL)
            defaultCounts.put(id, createIntProperty());
        
        return defaultCounts;
    }
    
    // MARK:- Méthodes privées utilisées pour mettre à jour l'état
    
    private void updatePublicGameStateProperties(PublicGameState newGameState) {
        ticketsPercentageProperty.set(100 * newGameState.ticketsCount() / ChMap.tickets().size());
        cardsPercentageProperty.set(100 * newGameState.cardState().deckSize() / Constants.TOTAL_CARDS_COUNT);
        
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            final Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
        
        final List<Route> newClaimedRoutes = new ArrayList<>(newGameState.claimedRoutes());
        if (gameState != null)
            newClaimedRoutes.removeAll(gameState.claimedRoutes());
        
        for (Route r : newClaimedRoutes) {
            final boolean ownerIsCurrentPlayer = newGameState.currentPlayerState()
                    .routes()
                    .contains(r);
            
            routesOwners.get(r).set(
                    ownerIsCurrentPlayer
                            ? newGameState.currentPlayerId()
                            : newGameState.currentPlayerId().next());
        }
    }
    
    private void updatePublicPlayerStatesProperties(PublicGameState newGameState) {
        for (PlayerId id : PlayerId.ALL) {
            final PublicPlayerState newPlayerState = newGameState.playerState(id);
            
            ticketCounts.get(id)
                    .set(newPlayerState.ticketCount());
            cardCounts.get(id)
                    .set(newPlayerState.cardCount());
            carCounts.get(id)
                    .set(newPlayerState.carCount());
            claimPoints.get(id)
                    .set(newPlayerState.claimPoints());
        }
    }
    
    private void updatePlayerStateProperties(PublicGameState newGameState, PlayerState newPlayerState) {
        ticketsProperty.setAll(newPlayerState.tickets().toList());
        
        for (Card card : Card.ALL) {
            cardCountsPerType.get(card)
                    .set(newPlayerState.cards().countOf(card));
        }
        
        final List<Route> unclaimedRoutes = new ArrayList<>(ChMap.routes());
        unclaimedRoutes.removeAll(newGameState.claimedRoutes());
        
        for (Route r : unclaimedRoutes) {
            boolean isClaimable;
            isClaimable = (playerId == newGameState.currentPlayerId());
            isClaimable &= newPlayerState.canClaimRoute(r);
            
            /*
                Si à ce stade le joueur peut s'emparer de la route
                et que c'est une route double, on vérifie que sa
                route voisine n'est pas déjà prise.
            */
            if (isClaimable && DOUBLE_ROUTES_STATIONS.contains(r.stations())) {
                isClaimable = newGameState.claimedRoutes()
                        .stream()
                        .noneMatch(cR -> cR.stations().containsAll(r.stations()));
            }
            
            routesClaimability.get(r).set(isClaimable);
        }
    }
    
    // MARK:- Contructeur et méthodes publiques

    public ObservableGameState(PlayerId playerId) {
        this.playerId = playerId;
        
        this.ticketsPercentageProperty = createIntProperty();
        this.cardsPercentageProperty = createIntProperty();
        this.faceUpCards = createFaceUpCardsProperties();
        this.routesOwners = createRoutesOwnersProperties();

        this.ticketCounts = createCountsProperties();
        this.cardCounts = createCountsProperties();
        this.carCounts = createCountsProperties();
        this.claimPoints = createCountsProperties();

        this.ticketsProperty = FXCollections.observableArrayList();
        this.cardCountsPerType = Card.ALL.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        card -> createIntProperty())
                );
        this.routesClaimability = ChMap.routes().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        route -> createBoolProperty())
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
    
    // MARK:- Méthode correspondant directement à l'état du joueur courant

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
    
    public ReadOnlyIntegerProperty ticketCount(PlayerId id) {
        return ticketCounts.get(id);
    }
    
    public ReadOnlyIntegerProperty cardCount(PlayerId id) {
        return cardCounts.get(id);
    }
    
    public ReadOnlyIntegerProperty carCount(PlayerId id) {
        return carCounts.get(id);
    }
    
    public ReadOnlyIntegerProperty claimPoints(PlayerId id) {
        return claimPoints.get(id);
    }
    
    public ObservableList<Ticket> tickets() {
        return FXCollections.unmodifiableObservableList(ticketsProperty);
    }
    
    public ReadOnlyIntegerProperty cardCountOf(Card card) {
        return cardCountsPerType.get(card);
    }
    
    public ReadOnlyBooleanProperty claimable(Route route) {
        return routesClaimability.get(route);
    }
    
}
