package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;

import java.util.Arrays;
import java.util.List;

public final class ServerMainCli {
    
    private static final List<String> DEFAULT_PLAYERS = List.of("Ada", "Charles");
    
    public static void main(String[] args) {
        List<String> names = (args.length >= PlayerId.COUNT)
                ? Arrays.asList(args).subList(0, PlayerId.COUNT)
                : DEFAULT_PLAYERS;
                
	    Server.runServer(names);
    }
    
}
