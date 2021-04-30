package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;

public final class ObservableGameState {

    private PublicGameState gameState;
    private PlayerState playerState;
    //private final PlayerId playerId;

    public ObservableGameState(PlayerId playerId) {

    }

    public void setState(PublicGameState publicGameState, PlayerState p1State) {
    }
}
