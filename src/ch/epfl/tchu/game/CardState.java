package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> discards;


    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        return new CardState(
                deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList(),
                deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT),
                SortedBag.of()
        );
    }

    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discards) {
        super(faceUpCards, deck.size(), discards.size());
        this.deck = deck;
        this.discards = discards;
    }

    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(! deck.isEmpty());
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        List<Card> newFaceUpCards = new ArrayList<>(faceUpCards());
        newFaceUpCards.set(slot, deck.topCard());

        return new CardState(
                Collections.unmodifiableList(newFaceUpCards),
                deck.withoutTopCard(),
                discards
        );
    }

    public Card topDeckCard() {
        Preconditions.checkArgument(! deck.isEmpty());

        return deck.topCard();
    }

    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(! deck.isEmpty());

        return new CardState(
                faceUpCards(),
                deck.withoutTopCard(),
                discards
        );
    }

    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());

        return new CardState(
                faceUpCards(),
                Deck.of(discards, rng),
                SortedBag.of()
        );
    }

    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(
                faceUpCards(),
                deck,
                discards.union(additionalDiscards)
        );
    }

}
