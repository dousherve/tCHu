package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
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
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Classe publique, finale et instanciable représentant l'état observable
 * d'une partie de tCHu.
 * Une instance de cette classe contient la partie publique
 * de l'état du jeu, ainsi que la totalité de l'état du joueur
 * auquel l'interface graphique utilisant cette instance correspond.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class ObservableGameState {
    
    private static final Set<List<Station>> DOUBLE_ROUTES_STATIONS = computeDoubleRoutesStations();
    
    // MARK:- Attributs
    
    private PublicGameState gameState;
    private PlayerState playerState;
    
    private final PlayerId playerId;
    
    // Propriétés de l'état de jeu observable

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
        Set<List<Station>> stationPairs = new HashSet<>();
        Set<List<Station>> doubleRoutesStations = new HashSet<>();
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
        Map<PlayerId, IntegerProperty> defaultCounts = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL)
            defaultCounts.put(id, createIntProperty());
        
        return defaultCounts;
    }
    
    // MARK:- Méthodes privées utilisées pour mettre à jour l'état
    
    private void setPublicGameStateProperties(PublicGameState newGameState) {
        ticketsPercentageProperty.set(100 * newGameState.ticketsCount() / ChMap.tickets().size());
        cardsPercentageProperty.set(100 * newGameState.cardState().deckSize() / Constants.TOTAL_CARDS_COUNT);
        
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
        
        final List<Route> newClaimedRoutes = new ArrayList<>(newGameState.claimedRoutes());
        if (gameState != null)
            newClaimedRoutes.removeAll(gameState.claimedRoutes());
        
        for (Route r : newClaimedRoutes) {
            boolean ownerIsCurrentPlayer = newGameState.currentPlayerState()
                    .routes()
                    .contains(r);
            
            routesOwners.get(r).set(
                    ownerIsCurrentPlayer
                            ? newGameState.currentPlayerId()
                            : newGameState.currentPlayerId().next());
        }
    }
    
    private void setPublicPlayerStatesProperties(PublicGameState newGameState) {
        for (PlayerId id : PlayerId.ALL) {
            PublicPlayerState newPlayerState = newGameState.playerState(id);
            
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
    
    private void setPlayerStateProperties(PublicGameState newGameState, PlayerState newPlayerState) {
        ticketsProperty.setAll(newPlayerState.tickets().toList());
        
        for (Card card : Card.ALL) {
            cardCountsPerType.get(card)
                    .set(newPlayerState.cards().countOf(card));
        }
        
        for (Route r : ChMap.routes()) {
            boolean isClaimable;
            isClaimable = (playerId == newGameState.currentPlayerId());
            isClaimable &= newPlayerState.canClaimRoute(r);
            isClaimable &= ! newGameState.claimedRoutes().contains(r);
            
            /*
                Si à ce stade le joueur peut s'emparer de la route
                et que c'est une route double, on vérifie que sa
                route voisine n'est pas déjà prise.
            */
            if (isClaimable && DOUBLE_ROUTES_STATIONS.contains(r.stations())) {
                isClaimable = newGameState.claimedRoutes()
                        .stream()
                        .noneMatch(claimed -> claimed.stations().containsAll(r.stations()));
            }
            
            routesClaimability.get(r).set(isClaimable);
        }
    }
    
    // MARK:- Contructeur et méthodes publiques
    
    /**
     * Construit un état de jeu observable rattaché au joueur
     * dont l'identité est passée en argument.
     * Initialise la totalité des propriétés de l'état aux valeurs
     * <code>null</code> pour celles contenant un objet, 0 pour celles
     * contenant un entier et <code>false</code> pour celles contenant
     * une valeur booléenne.
     * 
     * @param playerId
     *          l'identité du joueur auquel cette instance est rattachée
     * @throws NullPointerException
     *          si l'identité passée est <code>null</code>
     */
    public ObservableGameState(PlayerId playerId) {
        this.playerId = Objects.requireNonNull(playerId);
        
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
                        card -> createIntProperty()));
        this.routesClaimability = ChMap.routes().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        route -> createBoolProperty()));
    }
    
    /**
     * Met à jour la totalité des propriétés de l'état de jeu observable courant,
     * en fonction des deux états passés en argument.
     * 
     * @param newGameState
     *          le nouvel état public de jeu
     * @param newPlayerState
     *          le nouvel état public du joueur 
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        Preconditions.requireNonNull(newGameState, newPlayerState);
        
        setPublicGameStateProperties(newGameState);
        setPublicPlayerStatesProperties(newGameState);
        setPlayerStateProperties(newGameState, newPlayerState);
    
        this.gameState = newGameState;
        this.playerState = newPlayerState;
    }
    
    // MARK:- Méthodes correspondant directement à l'état de jeu courant
    
    /**
     * Appelle la méthode correspondante sur l'état de jeu courant.
     * 
     * @return vrai si et seulement si il est possible de tirer des billets
     */
    public boolean canDrawTickets() {
        return gameState.canDrawTickets();
    }
    
    /**
     * Appelle la méthode correspondante sur l'état de jeu courant.
     *
     * @return vrai si et seulement si il est possible de tirer des cartes
     */
    public boolean canDrawCards() {
        return gameState.canDrawCards();
    }
    
    // MARK:- Méthode correspondant directement à l'état du joueur courant
    
    /**
     * Appelle la méthode correspondante sur l'état du joueur courant.
     *
     * @return 
     *          la liste de tous les ensembles de cartes
     *          que le joueur pourrait utiliser pour prendre
     *          possession de la route donnée
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }
    
    // MARK:- Méthodes d'accès en lecture seule aux propriétés
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * le pourcentage de billets restants dans la pioche.
     * 
     * @return
     *          une propriété en lecture-seule contenant
     *          le pourcentage de billets restants dans la pioche
     */
    public ReadOnlyIntegerProperty ticketsPercentage() {
        return ticketsPercentageProperty;
    }
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * le pourcentage de cartes restantes dans la pioche.
     *
     * @return
     *          une propriété en lecture-seule contenant
     *          le pourcentage de cartes restantes dans la pioche
     */
    public ReadOnlyIntegerProperty cardsPercentage() {
        return cardsPercentageProperty;
    }
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * la carte face visible à l'emplacement <code>slot</code>
     * donné.
     *
     * @param slot
     *          l'emplacement de la carte face visible
     * @return
     *          une propriété en lecture-seule contenant
     *          la carte face visible à l'emplacement donné
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * l'identité du joueur propriétaire de la route donnée,
     * ou <code>null</code> si elle n'appartient à personne.
     * 
     * @param route
     *          la route dont on souhaite connaître le propriétaire
     * @return
     *          une propriété en lecture-seule contenant
     *          l'identité du joueur propriétaire de la route donnée,
     *          ou <code>null</code> si elle n'appartient à personne
     */
    public ReadOnlyObjectProperty<PlayerId> routeOwner(Route route) {
        return routesOwners.get(route);
    }
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * le nombre de billets que le joueur dont l'identité
     * est passée en argument a en main.
     * 
     * @param id
     *          l'identité du joueur dont on souhaite connaître
     *          le nombre de billets qu'il possède
     * @return
     *          une propriété en lecture-seule contenant
     *          le nombre de billets que le joueur dont l'identité
     *          est passée en argument a en main
     */
    public ReadOnlyIntegerProperty ticketCount(PlayerId id) {
        return ticketCounts.get(id);
    }
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * le nombre de cartes que le joueur dont l'identité
     * est passée en argument a en main.
     *
     * @param id
     *          l'identité du joueur dont on souhaite connaître
     *          le nombre de cartes qu'il possède
     * @return
     *          une propriété en lecture-seule contenant
     *          le nombre de cartes que le joueur dont l'identité
     *          est passée en argument a en main
     */
    public ReadOnlyIntegerProperty cardCount(PlayerId id) {
        return cardCounts.get(id);
    }
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * le nombre de wagons dont dispose le joueur dont l'identité
     * est passée en argument.
     *
     * @param id
     *          l'identité du joueur dont on souhaite connaître
     *          le nombre de wagons qu'il possède
     * @return
     *          une propriété en lecture-seule contenant
     *          le nombre de wagons dont dispose le joueur dont l'identité
     *          est passée en argument
     */
    public ReadOnlyIntegerProperty carCount(PlayerId id) {
        return carCounts.get(id);
    }
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * le nombre de points de construction obtenus par le joueur
     * dont l'identité est passée en argument.
     *
     * @param id
     *          l'identité du joueur dont on souhaite connaître
     *          le nombre de points de construction
     * @return
     *          une propriété en lecture-seule contenant
     *          le nombre de points de construction obtenus par le joueur
     *          dont l'identité est passée en argument
     */
    public ReadOnlyIntegerProperty claimPoints(PlayerId id) {
        return claimPoints.get(id);
    }
    
    /**
     * Retourne une liste observable contenant les billets
     * que le joueur courant a en main.
     * 
     * @return
     *          une liste observable contenant les billets
     *          que le joueur courant a en main
     */
    public ObservableList<Ticket> tickets() {
        return FXCollections.unmodifiableObservableList(ticketsProperty);
    }
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * le nombre de cartes du type passé en argument
     * que le joueur a en main.
     *
     * @param card
     *          le type de carte dont on souhaite connaître
     *          le nombre d'exemplaires dont le joueur dispose
     * @return
     *          une propriété en lecture-seule contenant
     *          le nombre de cartes du type passé en argument
     *          que le joueur a en main
     */
    public ReadOnlyIntegerProperty cardCountOf(Card card) {
        return cardCountsPerType.get(card);
    }
    
    /**
     * Retourne une propriété en lecture-seule contenant
     * une valeur booléenne indiquant si le joueur auquel
     * cette instance est rattachée peut actuellement s'emparer
     * de la route donnée.
     * 
     * @param route
     *          la route dont on souhaite savoir si
     *          le joueur peut s'en emparer
     * @return
     *          une propriété en lecture-seule contenant
     *          une valeur booléenne indiquant si le joueur auquel
     *          cette instance est rattachée peut actuellement s'emparer
     *          de la route donnée
     */
    public ReadOnlyBooleanProperty claimable(Route route) {
        return routesClaimability.get(route);
    }
    
}
