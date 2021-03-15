package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe publique, finale et immuable qui représente l'état complet d'un joueur.
 * Elle hérite de PublicPlayerState.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class PlayerState extends PublicPlayerState {
    
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Retourne l'état initial d'un joueur auquel les cartes initiales données ont été distribuées.
     * Dans cet état le joueur ne possède donc encore aucun billet et ne s'est emparé d'aucune route.
     *
     * @param initialCards
     *          les cartes initiales distribuées au joueur
     * @throws IllegalArgumentException
     *          si le nombre de cartes initiales ne vaut pas <code>Constant.INITIAL_CARDS_COUNT</code> (4)
     * @return
     *          l'état du joueur avec seulement ses cartes initiales
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        
        return new PlayerState(
                SortedBag.of(),
                initialCards,
                Collections.emptyList()
        );
    }

    /**
     * Construit l'état d'un joueur possédant les billets, cartes et routes donnés.
     *
     * @param tickets
     *          les billets que le joueur possède
     * @param cards
     *          les cartes que le joueur possède
     * @param routes
     *          la liste des routes que le joueur possède
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        
        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * Retourne les billets du joueur.
     *
     * @return
     *          les billets du joueur
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * Retourne un état identique au récepeteur si ce n'est que le joueur possède
     * en plus les billets donnés.
     *
     * @param newTickets
     *          l'ensemble des nouveaux billets donnés au joueur
     * @return
     *          l'état du joueur avec les nouveaux billets ajoutés
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(
                tickets.union(newTickets),
                cards,
                routes()
        );
    }

    /**
     * Retourne les cartes wagons/locomotives du joueur.
     *
     * @return
     *          les cartes wagons/locomotives du joueur
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * Retourne un état identique au récepteur si ce n'est que le joueur possède
     * en plus la carte donnée.
     *
     * @param card
     *          la nouvelle carte donné au joueur
     * @return
     *          l'état du joueur avec la nouvelle carte ajoutée
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(
                tickets,
                cards.union(SortedBag.of(card)),
                routes()
        );
    }

    /**
     * Retourne un état identique au récepteur si ce n'est que le joueur possède
     * en plus l'ensemble des cartes données.
     *
     * @param additionalCards
     *          l'ensemble des nouvelles cartes données au joueur
     * @return
     *          l'état du joueur avec les nouvelles cartes ajoutées
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(
                tickets,
                cards.union(additionalCards),
                routes()
        );
    }

    // J'ai arrêté la JavaDoc ICI !
    /**
     * Retourne vrai si et seulement si le joueur peut s'emparer de la route donnée,
     * c'est-à-dire s'il lui reste assez de wagons et s'il possède les cartes nécessaires.
     * 
     * @implNote
     *          Ne lançera pas d'<code>{@link IllegalArgumentException}</code> car
     *          la fonction retournera <code>false</code> avant d'exécuter
     *          l'appel à <code>possibleClaimCards</code>.
     * @param route
     *          la route dont le joueur désire s'emparer
     * @return
     *          vrai si et seulement si il lui reste assez de wagons et qu'il possède les cartes nécessaires
     */
    public boolean canClaimRoute(Route route) {
        return carCount() >= route.length() && ! possibleClaimCards(route).isEmpty();
    }
    
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(cardCount() >= route.length());
        
        return route.possibleClaimCards()
                .stream()
                .filter(cards::contains)
                .collect(Collectors.toUnmodifiableList());
    }
    
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3);
        Preconditions.checkArgument(! initialCards.isEmpty());
        Preconditions.checkArgument(initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == 3);
        
        SortedBag.Builder<Card> usableCardsBuilder = new SortedBag.Builder<>();
        
        for (Card remaining : cards.difference(initialCards)) {
            if (initialCards.contains(remaining) || remaining == Card.LOCOMOTIVE) {
                usableCardsBuilder.add(remaining);
            }
        }
        
        List<SortedBag<Card>> possibilities = new ArrayList<>(
                usableCardsBuilder
                        .build()
                        .subsetsOfSize(additionalCardsCount)
        );
        
        possibilities.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE))
        );
        
        return Collections.unmodifiableList(possibilities);
    }

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> newRoutes = new ArrayList<>(routes());
        newRoutes.add(route);
        
        return new PlayerState(
                tickets,
                cards.difference(claimCards),
                newRoutes
        );
    }

    public int ticketPoints() {
        int maxRouteId = 0;
        for (Route r : routes())
            for (Station s : r.stations())
                maxRouteId = Math.max(maxRouteId, s.id());
            
        StationPartition partition = new StationPartition.Builder(maxRouteId + 1).build();
        int points = 0;
        for (Ticket t : tickets)
            points += t.points(partition);
        
        return points;
    }

    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }

}
