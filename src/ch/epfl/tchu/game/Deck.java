package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Classe publique, finale et immuable qui représente un tas de cartes quelconques :
 * des wagons, des locomotives ou des billets.
 *
 * Elle est générique, le type de cartes n'étant pas fixé à l'avance.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Deck<C extends Comparable<C>> {

    private final List<C> cards;
    
    private Deck(List<C> cards) {
        this.cards = List.copyOf(cards);
    }

    /**
     * Retourne un tas de cartes ayant les mêmes cartes que le multiensemble <code>cards</code> donné,
     * mais les cartes sont mélangées au moyen du générateur de nombre aléatoires <code>rng</code>.
     *
     * @param cards
     *          le multiensemble de cartes donné
     * @param rng
     *          générateur de nombre aléatoires pour le mélange
     * @param <C>
     *          le type des cartes contenues dans le tas
     * @return
     *          un tas de cartes ayant les mêmes cartes que le multiensemble, mais mélangées
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        final List<C> toBeShuffled = cards.toList();
        Collections.shuffle(toBeShuffled, rng);

        return new Deck<>(toBeShuffled);
    }

    /**
     * Retourne la taille du tas, c'est-à-dire le nombre de cartes qu'il contient.
     *
     * @return la taille du tas
     */
    public int size() {
        return cards.size();
    }

    /**
     * Retourne vrai si et seulement si le tas est vide.
     *
     * @return
     *          vrai si le tas est vide,
     *          faux dans le cas contraire
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Retourne la carte au sommet du tas.
     *
     * @implNote Le sommet du tas se situe à la fin de la liste.
     * @throws IllegalArgumentException
     *          si le tas de cartes est vide
     * @return
     *          la carte au sommet du tas
     */
    public C topCard() {
        Preconditions.checkArgument(! isEmpty());
        return cards.get(size() - 1);
    }

    /**
     * Retourne un tas identique au récepteur mais sans la carte au sommet.
     *
     * @throws IllegalArgumentException
     *          si le tas de cartes est vide
     * @return
     *          un tas identique au récepteur mais sans la carte au sommet
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(! isEmpty());
        return withoutTopCards(1);
    }

    /**
     * Retourne un multiensemble contenant les <code>count</code> cartes se trouvant au sommet du tas.
     *
     * @param count
     *          le nombre de cartes du sommet du tas à retourner
     * @throws IllegalArgumentException
     *          si <code>count</code> n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     * @return
     *          un multiensemble contenant les <code>count</code> cartes au sommet du tas
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        return SortedBag.of(cards.subList(size() - count, size()));
    }

    /**
     * Retourne un tas identique au récepteur mais sans les <code>count</code> cartes du sommet.
     *
     * @param count
     *          le nombre de cartes à enlever du sommet du tas
     * @throws IllegalArgumentException
     *          si <code>count</code> n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     * @return
     *          un tas identique au récepteur mais sans les <code>count</code> cartes du sommet
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());
        return new Deck<>(cards.subList(0, size() - count));
    }

}
