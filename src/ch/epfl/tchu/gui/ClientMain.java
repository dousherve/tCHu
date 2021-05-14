package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public final class ClientMain extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getRaw();
        String host = parameters.get(0);
        int port = Integer.parseInt(parameters.get(1));
        
        RemotePlayerClient client = new RemotePlayerClient(new GraphicalPlayerAdapter(), host, port);
        new Thread(client).start();
    }
    
}
