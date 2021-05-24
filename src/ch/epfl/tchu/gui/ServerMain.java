package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

/**
 * Classe publique, finale qui contient le programme principal
 * du serveur de tCHu. Il s'agit d'une application JavaFX, elle
 * hérite donc de {@link Application}.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class ServerMain extends Application {
    
    private static final List<String> DEFAULT_PLAYERS = List.of("Ada", "Charles");
    private static final int DEFAULT_PORT = 5108;
    
    /**
     * Démarre l'application JavaFX.
     * Prend en argument le nom d'hôte ainsi que le port du serveur,
     * auxquels le client doit se connecter.
     *
     * @param args
     *          noms des joueurs, séparés par un espace
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        List<String> names = getParameters().getRaw();
        if (names.size() < PlayerId.COUNT)
            names = DEFAULT_PLAYERS;
        
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL)
            playerNames.put(id, names.get(id.ordinal()));
    
        try {
            ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
            Socket socket = serverSocket.accept();
            
            GraphicalPlayerAdapter graphicalPlayer = new GraphicalPlayerAdapter();
            Player playerProxy = new RemotePlayerProxy(socket);
            
            Thread gameThread = new Thread(() -> Game.play(
                    Map.of(PLAYER_1, graphicalPlayer, PLAYER_2, playerProxy),
                    playerNames,
                    SortedBag.of(ChMap.tickets()),
                    new Random()
            ));
            gameThread.setDaemon(true);
            gameThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
