package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Deck<C extends Comparable<C>> {

    private List<C> cards;

    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
       return null;
    }

    private Deck(List<C> cards) {
        this.cards = Collections.unmodifiableList(cards);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public C topCard() {
        Preconditions.checkArgument(! isEmpty());

        return cards.get(size() - 1);
    }

    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(! isEmpty());

        List<C> withoutTopCard = new ArrayList<>(cards);
        withoutTopCard.remove(size() - 1);

        return new Deck<C>(withoutTopCard);
    }

    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());

        return SortedBag.of(cards.subList(size() - count, size() - 1));
    }

    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());

        return new Deck<C>(cards.subList(0, size() - 1 - count));
    }

}
