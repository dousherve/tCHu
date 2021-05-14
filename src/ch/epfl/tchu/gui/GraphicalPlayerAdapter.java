package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

public final class GraphicalPlayerAdapter implements Player {
    
    private final BlockingQueue<SortedBag<Ticket>> ticketsQueue;
    private final BlockingQueue<TurnKind> turnKindQueue;
    private final BlockingQueue<SortedBag<Card>> cardsQueue;
    private final BlockingQueue<Integer> slotQueue;
    private final BlockingQueue<Route> claimedRouteQueue;
    
    private GraphicalPlayer graphicalPlayer;
    
    private static <T> BlockingQueue<T> createBlockingQueue() {
        return new ArrayBlockingQueue<>(1);
    }
    
    public GraphicalPlayerAdapter() {
        this.ticketsQueue = createBlockingQueue();
        this.turnKindQueue = createBlockingQueue();
        this.cardsQueue = createBlockingQueue();
        this.slotQueue = createBlockingQueue();
        this.claimedRouteQueue = createBlockingQueue();
    }
    
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }
    
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }
    
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }
    
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketsQueue::add));
    }
    
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return ticketsQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        throw new Error();
    }
    
    @Override
    public TurnKind nextTurn() {
        runLater(() -> graphicalPlayer.startTurn(
                () -> turnKindQueue.add(TurnKind.DRAW_TICKETS),
                slot -> {
                    turnKindQueue.add(TurnKind.DRAW_CARDS);
                    slotQueue.add(slot);
                },
                (route, initialCards) -> {
                    turnKindQueue.add(TurnKind.CLAIM_ROUTE);
                    claimedRouteQueue.add(route);
                    cardsQueue.add(initialCards);
                }
        ));
        
        try {
            return turnKindQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        throw new Error();
    }
    
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }
    
    @Override
    public int drawSlot() {
        try {
            if (slotQueue.isEmpty()) 
                runLater(() -> graphicalPlayer.drawCard(slotQueue::add));
            
            return slotQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        throw new Error();
    }
    
    @Override
    public Route claimedRoute() {
        try {
            return claimedRouteQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        throw new Error();
    }
    
    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return cardsQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        throw new Error();
    }
    
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cardsQueue::add));
    
        try {
            return cardsQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        throw new Error();
    }
    
}
