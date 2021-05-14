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

public final class ServerMain extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        List<String> names = getParameters().getRaw();
        if (names.isEmpty())
            names = List.of("Ada", "Charles");
        
        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL)
            playerNames.put(id, names.get(id.ordinal()));
    
        try (
            ServerSocket serverSocket = new ServerSocket(5108);
            Socket socket = serverSocket.accept()
        ) {
            GraphicalPlayerAdapter graphicalPlayer = new GraphicalPlayerAdapter();
            Player playerProxy = new RemotePlayerProxy(socket);
            
            new Thread(() -> Game.play(
                    Map.of(PLAYER_1, graphicalPlayer, PLAYER_2, playerProxy),
                    playerNames,
                    SortedBag.of(ChMap.tickets()),
                    new Random()
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
