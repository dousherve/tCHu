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

public final class ServerCli implements Runnable {
    
    public static final int DEFAULT_PORT = 5108;
    public static final List<String> DEFAULT_NAMES = List.of("Ada", "Charles");
    
    private final List<String> names;
    private final int port;
    
    public ServerCli(int port, List<String> names) {
        Preconditions.checkArgument(names.size() == PlayerId.COUNT);
        this.names = names;
        this.port = port;
    }
    
    public ServerCli(List<String> names) {
        this(DEFAULT_PORT, names);
    }
    
    public ServerCli(String... names) {
        this(DEFAULT_PORT, List.of(names));
    }
    
    public ServerCli() {
        this(DEFAULT_PORT, DEFAULT_NAMES);
    }
    
    @Override
    public void run() {
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
