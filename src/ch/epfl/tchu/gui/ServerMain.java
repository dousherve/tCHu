package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public final class ServerMain extends Application {
    
    private Thread gameThread;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private void terminate(Event event) {
        try {
            if (gameThread != null && gameThread.isAlive())
                // Deprecated
                gameThread.stop();
            Platform.exit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    
    private void showGui(Stage primaryStage) {
        Platform.setImplicitExit(false);
        
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
            gameThread = new Thread(new Server(name1TF.getText(), name2TF.getText()));
            gameThread.setDaemon(true);
            gameThread.start();
            launchBtn.disableProperty().unbind();
            launchBtn.disableProperty().set(true);
        });
    
        Button exitBtn = new Button("Quitter");
        exitBtn.setOnAction(this::terminate);
    
        VBox vbox = new VBox(10d, name1TF, name2TF, new HBox(10d, launchBtn, exitBtn));
        vbox.setAlignment(Pos.CENTER);
        vbox.requestFocus();
    
        primaryStage.setOnCloseRequest(this::terminate);
        primaryStage.setScene(new Scene(vbox));
        primaryStage.setTitle("tCHu \u2014 Serveur");
        primaryStage.show();
    }
    
    @Override
    public void start(Stage primaryStage) {
        List<String> names = getParameters().getRaw();
        
        if (names.isEmpty() || names.get(0).equalsIgnoreCase("gui"))
            showGui(primaryStage);
        else if (names.size() < PlayerId.COUNT)
            new Server().run();
        else
            new Server(names.subList(0, PlayerId.COUNT)).run();
    }
    
}
