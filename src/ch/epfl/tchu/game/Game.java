package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Map;
import java.util.Random;

public final class Game {
    
    private static void broadcastInfo(String info, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        
    }
    
    private static void broadcastStateChange(PublicGameState newState, Map<PlayerId, Player> players) {
        
    }
    
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);
        
        
    }
    
    private Game() {}
        
}
