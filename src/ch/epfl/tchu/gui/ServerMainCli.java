package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;

import java.util.Arrays;
import java.util.List;

public final class ServerMainCli {
    
    public static void main(String[] args) {
        List<String> names = (args.length >= PlayerId.COUNT)
                ? Arrays.asList(args).subList(0, PlayerId.COUNT)
                : ServerCli.DEFAULT_NAMES;
                
	    new ServerCli(names).run();
    }
    
}
