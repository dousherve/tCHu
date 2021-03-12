package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Classe finale et immuable héritant de <code>{@link PublicCardState}</code>
 * qui représente l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs,
 * c'est-à-dire les cartes que contiennent la pioche et la défausse.
 * 
 * Elle ajoute à <code>{@link PublicCardState}</code> les éléments privés de l'état
 * ainsi que les méthodes correspondantes.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> discards;
    
    /**
     * Retourne un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné,
     * la pioche est constituée des cartes du tas restantes, et la défausse est vide.
     * 
     * @param deck
     *          le tas de cartes dont on prend les 5 premières
     * @throws IllegalArgumentException
     *          si le tas donné contient moins de 5 cartes
     * @return
     *          l'état décrit ci-dessus dans lequel les 5 cartes disposées faces visibles sont 
     *          les 5 premières du tas donné, la pioche est constituée des cartes du tas restantes,
     *          et la défausse est vide
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);
        
        List<Card> topCards = new ArrayList<>();
        for (int slot : Constants.FACE_UP_CARD_SLOTS)
            topCards.add(deck.withoutTopCards(slot).topCard());

        return new CardState(
                topCards,  // Cartes face visible dans l'ordre de tirage depuis le deck
                deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT),    // Pioche
                SortedBag.of()  // Défausse vide
        );
    }

    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discards) {
        super(faceUpCards, deck.size(), discards.size());
        
        this.deck = deck;
        this.discards = discards;
    }
    
    /**
     * Retourne un état décrivant un ensemble de cartes identique à celui du récepteur,
     * où la carte face visible d'index <code>slot</code>
     * a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même coup retirée.
     * 
     * @param slot
     *          l'index de la carte à remplacer par celle se trouvant au sommet de la pioche
     * @throws IndexOutOfBoundsException
     *          si l'index <code>slot</code> donné n'est pas compris entre 0 inclus et
     *          <code>Constants.FACE_UP_CARDS_COUNT</code> exclus
     * @throws IllegalArgumentException
     *          si la pioche est vide
     * @return
     *          un état dont la carte face visible d'index <code>slot</code> est remplacée par la
     *          <code>deck.topCard()</code>, qui en est retirée du <code>deck</code>
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        // Ici, on vérifie manuellement si la pioche est vide au lieu d'appeler la méthode
        // topDeckCard() afin d'éviter une copie éventuellement inutile de faceUpCards().
        Preconditions.checkArgument(! deck.isEmpty());
        
        List<Card> newFaceUpCards = new ArrayList<>(faceUpCards());
        newFaceUpCards.set(slot, deck.topCard());

        return new CardState(
                newFaceUpCards,
                deck.withoutTopCard(),
                discards
        );
    }
    
    /**
     * Retourne la carte se trouvant au sommet de la pioche.
     * 
     * @throws IllegalArgumentException
     *          si la pioche est vide
     * @return la carte se trouvant au sommet de la pioche
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(! deck.isEmpty());

        return deck.topCard();
    }
    
    /**
     * Retourne un état dont l'ensemble de cartes est identique à celui du récepteur,
     * mais sans la carte se trouvant au sommet de la pioche.
     * 
     * @throws IllegalArgumentException
     *          si la pioche est vide
     * @return
     *          un état dont l'ensemble de cartes est identique à celui du récepteur,
     *          mais sans la carte se trouvant au sommet de la pioche
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(! deck.isEmpty());

        return new CardState(
                faceUpCards(),
                deck.withoutTopCard(),
                discards
        );
    }
    
    /**
     * Retourne un état dont l'ensemble de cartes est identique à celui du récepteur, si ce n'est que
     * les cartes de la défausse ont été mélangées au moyen du générateur aléatoire donné <code>rng</code>
     * afin de constituer la nouvelle pioche.
     * 
     * @param rng
     *          le générateur aléatoire à utiliser pour mélanger les cartes
     * @throws IllegalArgumentException
     *          si la pioche du récepteur n'est pas vide
     * @return un état dont la défausse a été mélangée pour former la nouvelle pioche
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());

        return new CardState(
                faceUpCards(),
                Deck.of(discards, rng),
                SortedBag.of()
        );
    }
    
    /**
     * Retourne un état dont l'ensemble de cartes est identique à celui du récepteur,
     * mais avec les cartes données ajoutées à la défausse.
     * 
     * @param additionalDiscards
     *          les cartes à ajouter à la nouvelle défausse
     * @return
     *          un état dont l'ensemble de cartes est identique à celui du récepteur,
     *          mais avec les cartes données ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(
                faceUpCards(),
                deck,
                discards.union(additionalDiscards)
        );
    }

}
