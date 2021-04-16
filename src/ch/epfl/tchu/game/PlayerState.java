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
 * Elle hérite de <code>{@link PublicPlayerState}</code>.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class PlayerState extends PublicPlayerState {
    
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    
    // Nombre maximum de cartes différentes lors de la prise d'un tunnel
    private static final int MAX_CARD_TYPES_COUNT = 2;

    /**
     * Retourne l'état initial d'un joueur auquel les cartes initiales données ont été distribuées.
     * Dans cet état, le joueur ne possède donc encore aucun billet et ne s'est emparé d'aucune route.
     *
     * @param initialCards
     *          les cartes initiales distribuées au joueur
     * @throws IllegalArgumentException
     *          si le nombre de cartes initiales ne vaut pas <code>Constant.INITIAL_CARDS_COUNT</code>
     * @return
     *          l'état du joueur avec seulement ses cartes initiales
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
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
     * @return les billets du joueur
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * Retourne un état identique au récepteur si ce n'est que le joueur possède
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
     * @return les cartes wagons/locomotives du joueur
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

    /**
     * Retourne vrai si et seulement si le joueur peut s'emparer de la route donnée,
     * c'est-à-dire s'il lui reste assez de wagons et s'il possède les cartes nécessaires.
     * 
     * @implNote
     *          Ne lançera pas d'<code>{@link IllegalArgumentException}</code> si le joueur
     *          ne possède pas assez de cartes car la fonction retournera <code>false</code>
     *          avant d'exécuter l'appel à <code>possibleClaimCards</code>.
     * @param route
     *          la route dont le joueur désire s'emparer
     * @return
     *          vrai si et seulement si il lui reste assez de wagons et qu'il possède les cartes nécessaires
     */
    public boolean canClaimRoute(Route route) {
        return carCount() >= route.length() && ! possibleClaimCards(route).isEmpty();
    }
    
    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser
     * pour prendre possession de la route donnée.
     * 
     * @param route
     *          la route dont le joueur veut s'emparer
     * @throws IllegalArgumentException
     *          si le joueur n'a pas assez de wagons pour s'emparer de la route
     * @return
     *          la liste de tous les ensembles de cartes que le joueur pourrait utiliser
     *          pour prendre possession de la route donnée
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());
        
        return route.possibleClaimCards().stream()
                .filter(cards::contains)
                .collect(Collectors.toUnmodifiableList());
    }
    
    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour s'emparer d'un tunnel,
     * trié par ordre croissant du nombre de cartes locomotives, sachant qu'il a initialement posé
     * les cartes <code>initialCards</code>, que les 3 cartes tirées du sommet de la pioche sont <code>drawnCards</code>,
     * et que ces dernières forcent le joueur à poser encore <code>additionalCardsCount</code> cartes.
     * 
     * @param additionalCardsCount
     *          le nombre de cartes additionnelles que le joueur doit poser
     * @param initialCards
     *          les cartes initialement posées par le joueur
     * @param drawnCards
     *          les 3 cartes tirées du sommet de la pioche
     * @throws IllegalArgumentException
     *          si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus),
     *          si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents,
     *          ou bien si l'ensemble des cartes tirées ne contient pas exactement 3 cartes
     * @return
     *          la liste de tous les ensembles de cartes que le joueur pourrait utiliser
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount > 0);
        Preconditions.checkArgument(additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(! initialCards.isEmpty());
        Preconditions.checkArgument(initialCards.toSet().size() <= MAX_CARD_TYPES_COUNT);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        
        SortedBag.Builder<Card> usableCardsB = new SortedBag.Builder<>();
        
        for (Card remaining : cards.difference(initialCards))
            if (initialCards.contains(remaining) || remaining == Card.LOCOMOTIVE)
                usableCardsB.add(remaining);
            
        // Si le nombre de cartes additionnelles possibles est inférieur
        // au nombre de cartes additionnelles à jouer, on retourne une liste vide
        if (usableCardsB.size() < additionalCardsCount)
            return List.of();
        
        final List<SortedBag<Card>> possibilities = new ArrayList<>(
                usableCardsB
                        .build()
                        .subsetsOfSize(additionalCardsCount)
        );
        
        possibilities.sort(
                Comparator.comparingInt(bag -> bag.countOf(Card.LOCOMOTIVE))
        );
        
        return Collections.unmodifiableList(possibilities);
    }
    
    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur s'est de plus
     * emparé de la route donnée au moyen des cartes données.
     * 
     * @param route
     *          la route dont le joueur s'est emparé
     * @param claimCards
     *          les cartes au moyen desquelles le joueur s'est emparé de la route donnée
     * @return
     *          un état identique au récepteur, mais qui contient la route
     *          dont il vient de s'emparer
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        final List<Route> newRoutes = new ArrayList<>(routes());
        newRoutes.add(route);
        
        return new PlayerState(
                tickets,
                cards.difference(claimCards),
                newRoutes
        );
    }
    
    /**
     * Retourne le nombre de points (éventuellement négatif) obtenus par le joueur grâce à ses billets.
     * 
     * @return le nombre de points obtenus par le joueur grâce à ses billets
     */
    public int ticketPoints() {
        final int maxStationId = routes().stream()
                .flatMap(r -> r.stations().stream())
                .mapToInt(Station::id)
                .max()
                .orElse(0);
    
        final StationPartition.Builder partitionB = new StationPartition.Builder(maxStationId + 1);
        routes().forEach(r -> partitionB.connect(r.station1(), r.station2()));
        
        final StationPartition partition = partitionB.build();
        return tickets.stream()
                .mapToInt(t -> t.points(partition))
                .sum();
    }
    
    /**
     * Retourne la totalité des points obtenus par le joueur à la fin de la partie.
     * 
     * @return la totalité des points obtenus par le joueur à la fin de la partie
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }

}
