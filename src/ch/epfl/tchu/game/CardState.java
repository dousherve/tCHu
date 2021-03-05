package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class CardState extends PublicCardState {

    private


    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        return null;
    }



}
