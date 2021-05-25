package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Server {
    
    private Server() {}
    
    static void runServer(List<String> names) {
        Preconditions.checkArgument(names.size() == PlayerId.COUNT);
    
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL)
            playerNames.put(id, names.get(id.ordinal()));
    
        try {
            ServerSocket serverSocket = new ServerSocket(5108);
        
            Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);
            for (PlayerId id : PlayerId.ALL)
                players.put(id, new RemotePlayerProxy(serverSocket.accept()));
        
            Game.play(
                    players,
                    playerNames,
                    SortedBag.of(ChMap.tickets()),
                    new Random()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
