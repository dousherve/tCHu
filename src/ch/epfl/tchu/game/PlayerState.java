package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class PlayerState extends PublicPlayerState {
    
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        
        return new PlayerState(
                SortedBag.of(),
                initialCards,
                Collections.emptyList()
        );
    }
    
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        
        this.tickets = tickets;
        this.cards = cards;
    }
    
    public SortedBag<Ticket> tickets() {
        return tickets;
    }
    
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(
                tickets.union(newTickets),
                cards,
                routes()
        );
    }
    
    public SortedBag<Card> cards() {
        return cards;
    }
    
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(
                tickets,
                cards.union(SortedBag.of(card)),
                routes()
        );
    }
    
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(
                tickets,
                cards.union(additionalCards),
                routes()
        );
    }
    
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
        // TODO : implémenter
        return Collections.emptyList();
    }
    
}
