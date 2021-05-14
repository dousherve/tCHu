package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * Classe publique et immuable qui représente (une partie de) l'état des cartes
 * wagons/locomotive qui ne sont pas dans la main des joueurs.
 *
 * Cette classe représente donc les 5 cartes disposées face visible
 * à côté du plateau, la pioche ainsi que la défausse.
 *
 * Il s'agit de la partie de l'état des cartes connue de tous les joueurs.
 * Par exemple, elle contient la taille de la pioche, mais pas son contenu exact.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public class PublicCardState {
    
    private final List<Card> faceUpCards;
    private final int deckSize, discardsSize;

    /**
     * Construit un état public des cartes dans lequel les cartes
     * face visible sont celles données dans <code>faceUpCards</code>,
     * la pioche contient <code>deckSize</code> cartes
     * et la défausse en contient <code>discardsSize</code>.
     *
     * @param faceUpCards
     *          les cartes face visible données
     * @param deckSize
     *          la taille de la pioche
     * @param discardsSize
     *          la taille de la défausse
     * @throws IllegalArgumentException
     *          si <code>faceUpCards</code> ne contient pas le bon nombre d'éléments
     *          (<code>Constants.FACE_UP_CARDS_COUNT</code>),
     *          si la taille de la pioche est négative,
     *          ou bien si la taille de la défausse est négative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0);

        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Retourne les 5 cartes face visible sous la forme d'une liste de 5 éléments.
     *
     * @return retourne une liste de 5 élements formée des cartes face visible
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * Retourne la carte face visible d'index <code>slot</code>.
     *
     * @param slot
     *          index de la carte face visible à retourner
     * @throws IndexOutOfBoundsException
     *          si <code>slot</code> n'est pas compris entre 0 (inclus)
     *          et <code>Constants.FACE_UP_CARDS_COUNT</code> (exclus)
     * @return
     *          la carte d'index <code>slot</code> des <code>faceUpCards</code>
     */
    public Card faceUpCard(int slot) {
        return faceUpCards.get(
                Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT));
    }

    /**
     * Retourne la taille de la pioche.
     *
     * @return la taille de la pioche
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Retourne vrai si et seulement si la pioche est vide.
     *
     * @return
     *          vrai si la pioche est vide
     *          faux dans le cas contraire
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * Retourne la taille de la défausse.
     *
     * @return la taille de la défausse
     */
    public int discardsSize() {
        return discardsSize;
    }
    
}
