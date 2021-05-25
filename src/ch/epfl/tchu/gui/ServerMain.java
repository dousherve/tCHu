package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public final class ServerMain extends Application {
    
    private static final List<String> DEFAULT_PLAYERS = List.of("Ada", "Charles");
    
    private Thread gameThread;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private void showGui(Stage primaryStage) {
        TextField name1TF = new TextField();
        name1TF.setPromptText("Nom du joueur 1");
        TextField name2TF = new TextField();
        name2TF.setPromptText("Nom du joueur 2");
    
        Button launchBtn = new Button("Démarrer le serveur");
        launchBtn.disableProperty().bind(
                Bindings.or(
                        name1TF.textProperty().isEmpty(),
                        name2TF.textProperty().isEmpty()));
    
        launchBtn.setOnAction(e -> {
            gameThread = new Thread(() -> Server.runServer(List.of(name1TF.getText(), name2TF.getText())));
            gameThread.start();
            launchBtn.disableProperty().unbind();
            launchBtn.disableProperty().set(true);
        });
    
        Button exitBtn = new Button("Quitter");
        exitBtn.setOnAction(e -> {
            try {
                if (gameThread != null && gameThread.isAlive())
                    gameThread.join();
                primaryStage.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    
        VBox vbox = new VBox(10d, name1TF, name2TF, new HBox(10d, launchBtn, exitBtn));
        vbox.setAlignment(Pos.CENTER);
        vbox.requestFocus();
    
        primaryStage.setScene(new Scene(vbox));
        primaryStage.setTitle("tCHu \u2014 Serveur");
        primaryStage.show();
    }
    
    @Override
    public void start(Stage primaryStage) {
        List<String> names = getParameters().getRaw();
        
        if (names.isEmpty())
            showGui(primaryStage);
        else if (names.size() < PlayerId.COUNT)
            Server.runServer(DEFAULT_PLAYERS);
        else
            Server.runServer(names.subList(0, PlayerId.COUNT));
    }
    
}
