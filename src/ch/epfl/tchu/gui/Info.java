package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

public final class Info {
    
    private final String playerName;
    
    private static String routeDescription(Route route) {
        return String.join(
                StringsFr.EN_DASH_SEPARATOR,
                route.station1().name(),
                route.station2().name()
        );
    }
    
    // TODO: optimiser ?
    private static String cardsDescription(SortedBag<Card> cards) {
        List<String> cardsDescriptions = new ArrayList<>();
        
        for (Card c : cards.toSet()) {
            final int COUNT = cards.countOf(c);
            cardsDescriptions.add(
                    COUNT + " " + cardName(c, COUNT)
            );
        }
        
        StringBuilder descrBuilder = new StringBuilder();
        
        var it = cardsDescriptions.listIterator();
        while (it.hasNext()) {
            descrBuilder.append(it.next());
    
            if (it.nextIndex() < cardsDescriptions.size() - 1) {
                descrBuilder.append(", ");
            } else if (it.nextIndex() == cardsDescriptions.size() - 1) {
                descrBuilder.append(StringsFr.AND_SEPARATOR);
            }
        }
        
        return descrBuilder.toString();
    }
    
    public static String cardName(Card card, int count) {
        String cardDescription;
        
        switch (card) {
            case BLACK:
                cardDescription = StringsFr.BLACK_CARD;
                break;
            case VIOLET:
                cardDescription = StringsFr.VIOLET_CARD;
                break;
            case BLUE:
                cardDescription = StringsFr.BLUE_CARD;
                break;
            case GREEN:
                cardDescription = StringsFr.GREEN_CARD;
                break;
            case YELLOW:
                cardDescription = StringsFr.YELLOW_CARD;
                break;
            case ORANGE:
                cardDescription = StringsFr.ORANGE_CARD;
                break;
            case RED:
                cardDescription = StringsFr.RED_CARD;
                break;
            case WHITE:
                cardDescription = StringsFr.WHITE_CARD;
                break;
            case LOCOMOTIVE:
                cardDescription = StringsFr.LOCOMOTIVE_CARD;
                break;
            default:
                return "Error: Unknown card";
        }
        
        return cardDescription + StringsFr.plural(count);
    }
    
    public static String draw(List<String> playerNames, int points) {
        return String.format(
                StringsFr.DRAW,
                String.join(StringsFr.AND_SEPARATOR, playerNames),
                points
        );
    }
    
    public Info(String playerName) {
        this.playerName = playerName;
    }
    
    public String willPlayFirst() {
        return String.format(
                StringsFr.WILL_PLAY_FIRST,
                playerName
        );
    }
    
    public String keptTickets(int count) {
        return String.format(
                StringsFr.KEPT_N_TICKETS,
                playerName,
                count, StringsFr.plural(count)
        );
    }
    
    public String canPlay() {
        return String.format(
                StringsFr.CAN_PLAY,
                playerName
        );
    }
    
    public String drewTickets(int count) {
        return String.format(
                StringsFr.DREW_TICKETS,
                playerName,
                count, StringsFr.plural(count)
        );
    }
    
    public String drewBlindCard() {
        return String.format(
                StringsFr.DREW_BLIND_CARD,
                playerName
        );
    }
    
    public String drewVisibleCard(Card card) {
        return String.format(
                StringsFr.DREW_VISIBLE_CARD,
                playerName, cardName(card, 1)
        );
    }
    
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        final Card CARD = cards.get(0);
        final int CARD_COUNT = cards.countOf(CARD);
        
        return String.format(
                StringsFr.CLAIMED_ROUTE,
                playerName,
                routeDescription(route),
                cardsDescription(cards)
        );
    }
    
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(
                StringsFr.ATTEMPTS_TUNNEL_CLAIM,
                playerName,
                routeDescription(route),
                cardsDescription(initialCards)
        );
    }
    
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        StringBuilder descrBuilder = new StringBuilder();
        
        descrBuilder.append(String.format(
                StringsFr.ADDITIONAL_CARDS_ARE,
                cardsDescription(drawnCards)
        ));
        
        if (additionalCost == 0) {
            descrBuilder.append(StringsFr.NO_ADDITIONAL_COST);
        } else {
            descrBuilder.append(String.format(
                    StringsFr.SOME_ADDITIONAL_COST,
                    additionalCost, StringsFr.plural(additionalCost)
            ));
        }
        
        return descrBuilder.toString();
    }
    
    public String didNotClaimRoute(Route route) {
        return String.format(
                StringsFr.DID_NOT_CLAIM_ROUTE,
                playerName,
                routeDescription(route)
        );
    }
    
    public String lastTurnBegins(int carCount) {
        return String.format(
                StringsFr.LAST_TURN_BEGINS,
                playerName,
                carCount, StringsFr.plural(carCount)
        );
    }
    
    public String getsLongestTrailBonus(Trail longestTrail) {
        final String TRAIL_DESCRIPTION = 
                longestTrail.station1().name()
                + StringsFr.EN_DASH_SEPARATOR
                + longestTrail.station2().name();
        
        return String.format(
                StringsFr.GETS_BONUS,
                playerName,
                TRAIL_DESCRIPTION
        );
    }
    
    public String won(int points, int loserPoints) {
        return String.format(
                StringsFr.WINS,
                playerName,
                points, StringsFr.plural(points),
                loserPoints, StringsFr.plural(loserPoints)
        );
    }
    
}
