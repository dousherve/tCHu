package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class TestServer {
    
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!\n");
        
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            
            var playerNames = Map.of(
                    PLAYER_1, "Ada",
                    PLAYER_2, "Charles"
            );
            playerProxy.initPlayers(PLAYER_1, playerNames);
            System.out.println("Initialized players.\n");
            
            playerProxy.receiveInfo("!! Test info !!");
            System.out.println("Sent info test.\n");
            
            playerProxy.setInitialTicketChoice(SortedBag.of(ChMap.tickets().subList(0, 3)));
            System.out.println("Chose initial tickets:");
            System.out.printf("\t%s\n\n", playerProxy.chooseInitialTickets());
    
            System.out.printf("Draw slot: %d\n\n", playerProxy.drawSlot());
        }
        
        System.out.println("Server done!");
    }
}