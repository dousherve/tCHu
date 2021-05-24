package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Classe publique, finale qui contient le programme principal
 * du client de tCHu. Il s'agit d'une application JavaFX, elle
 * hérite donc de {@link Application}.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class ClientMain extends Application {
    
    /**
     * Démarre l'application JavaFX.
     * Prend en argument le nom d'hôte ainsi que le port du serveur,
     * auxquels le client doit se connecter.
     * 
     * @param args
     *          nom d'hôte, numéro de port
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getRaw();
        if (parameters.size() < 2)
            throw new IllegalArgumentException("Usage : tchu [hostname] [port]");
        
        String host = parameters.get(0);
        int port = Integer.parseInt(parameters.get(1));
        
        RemotePlayerClient client = new RemotePlayerClient(new GraphicalPlayerAdapter(), host, port);
        
        Thread gameThread = new Thread(client);
        gameThread.setDaemon(true);
        gameThread.start();
    }
    
}
