package ch.epfl.tchu.game;

import java.util.List;

public enum PlayerId {
    
    PLAYER_1, PLAYER_2;
    
    public static final List<PlayerId> ALL = List.of(values());
    public static final int COUNT = ALL.size();
    
    public PlayerId next() {
        // TODO: equals() ou this == ?
        return (this == PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }
    
}
