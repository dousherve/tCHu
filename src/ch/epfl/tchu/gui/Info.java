package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe publique, finale et immuable représentant un générateur de messages
 * qui permet de générer les textes décrivant le déroulement de la partie.
 * 
 * Les messages dépendent des chaînes de caractères de <code>{@link StringsFr}</code>
 * prêtes à être formattées à l'aide de la méthode statique
 * <code>format</code> de la classe <code>{@link String}</code>.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Info {
    
    private final String playerName;
    
    private static String routeDescription(Route route) {
        return String.join(
                StringsFr.EN_DASH_SEPARATOR,
                route.station1().name(),
                route.station2().name()
        );
    }
    
    private static String cardsDescription(SortedBag<Card> cards) {
        List<String> cardsDescriptions = new ArrayList<>();
        
        for (Card c : cards.toSet()) {
            final int count = cards.countOf(c);
            cardsDescriptions.add(count + " " + cardName(c, count));
        }
        
        StringBuilder descrB = new StringBuilder();
        
        var descrIt = cardsDescriptions.listIterator();
        while (descrIt.hasNext()) {
            descrB.append(descrIt.next());
    
            if (descrIt.nextIndex() < cardsDescriptions.size() - 1)
                descrB.append(", ");
            else if (descrIt.nextIndex() == cardsDescriptions.size() - 1)
                descrB.append(StringsFr.AND_SEPARATOR);
        }
        
        return descrB.toString();
    }
    
    /**
     * Méthode publique et statique qui retourne le nom (français) de la carte donnée,
     * au singulier si et seulement si la valeur absolue du second argument vaut 1.
     * 
     * @param card
     *          la carte dont on veut le nom
     * @param count
     *          le nombre de cartes qui détermine l'accord en nombre du nom de la carte
     * @return
     *          le nom accordé en genre et en nombre de la carte <code>card</code> donnée
     */
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
                return "Carte inconnue";
        }
        
        return cardDescription + StringsFr.plural(count);
    }
    
    /**
     * Méthode publique et statique qui retourne le message déclarant que les joueurs,
     * dont les noms sont ceux donnés, ont terminé la partie ex æqo
     * en ayant chacun remporté les points donnés.
     * 
     * @param playerNames
     *          une liste contenant les noms des joueurs
     * @param points
     *          le nombre de points remportés par les deux joueurs (qui sont ex æqo)
     * @return
     *          le message déclarant que les joueurs ont terminé la partie ex æqo
     */
    public static String draw(List<String> playerNames, int points) {
        return String.format(
                StringsFr.DRAW,
                String.join(StringsFr.AND_SEPARATOR, playerNames),
                points
        );
    }
    
    /**
     * Construit un générateur de messages liés au joueur ayant le nom donné.
     * 
     * @param playerName
     *          le nom du joueur
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }
    
    /**
     * Retourne le message déclarant que le joueur jouera en premier.
     * 
     * @return le message déclarant que le joueur jouera en premier
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }
    
    /**
     * Retourne le message déclarant que le joueur a gardé le nombre de billets donné.
     * 
     * @param count
     *          le nombre de billets que le joueur a décidé de garder
     * @return
     *          le message déclarant que le joueur a gardé le nombre de billets donné
     */
    public String keptTickets(int count) {
        return String.format(
                StringsFr.KEPT_N_TICKETS,
                playerName,
                count, StringsFr.plural(count)
        );
    }
    
    /**
     * Retourne le message déclarant que le joueur peut jouer.
     * 
     * @return le message déclarant que le joueur peut jouer
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }
    
    /**
     * Retourne le message déclarant que le joueur a tiré le nombre donné de billets.
     * 
     * @param count
     *          le nombre de billets tirés par le joueur
     * @return le message déclarant que le joueur a tiré le nombre donné de billets
     */
    public String drewTickets(int count) {
        return String.format(
                StringsFr.DREW_TICKETS,
                playerName,
                count, StringsFr.plural(count)
        );
    }
    
    /**
     * Retourne le message déclarant que le joueur a tiré une carte du sommet de la pioche.
     * 
     * @return le message déclarant que le joueur a tiré une carte du sommet de la pioche
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }
    
    /**
     * Retourne le message déclarant que le joueur a tiré la carte disposée face visible donnée.
     * 
     * @param card
     *          la carte face visible tirée par le joueur
     * @return
     *          le message déclarant que le joueur a tiré la carte disposée face visible donnée
     */
    public String drewVisibleCard(Card card) {
        return String.format(
                StringsFr.DREW_VISIBLE_CARD,
                playerName, cardName(card, 1)
        );
    }
    
    /**
     * Retourne le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données.
     * 
     * @param route
     *          la route dont le joueur s'est emparé
     * @param cards
     *          les cartes au moyen desquelles le joueur s'est emparé de la route donnée
     * @return
     *          le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        final Card card = cards.get(0);
        final int cardCount = cards.countOf(card);
        
        return String.format(
                StringsFr.CLAIMED_ROUTE,
                playerName,
                routeDescription(route),
                cardsDescription(cards)
        );
    }
    
    /**
     * Retourne le message déclarant que le joueur désire s'emparer
     * de la route en tunnel donnée en utilisant initialement les cartes données.
     * 
     * @param route
     *          la route en tunnel dont le joueur désire s'emparer
     * @param initialCards
     *          les cartes utilisées initialement par le joueur qui désire s'emparer
     *          de la route en tunnel donnée
     * @return
     *          le message déclarant que le joueur désire s'emparer
     *          de la route en tunnel donnée en utilisant initialement les cartes données
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(
                StringsFr.ATTEMPTS_TUNNEL_CLAIM,
                playerName,
                routeDescription(route),
                cardsDescription(initialCards)
        );
    }
    
    /**
     * Retourne le message déclarant que le joueur a tiré les trois cartes additionnelles données,
     * et qu'elles impliquent un coût additionel du nombre de cartes donné.
     * 
     * @param drawnCards
     *          les trois cartes additionnelles tirées par le joueur
     * @param additionalCost
     *          le coût additionnel impliqué par le tirage des cartes données
     * @return
     *          le message déclarant que le joueur a tiré les trois cartes additionnelles données,
     *          et qu'elles impliquent un coût additionel du nombre de cartes donné
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        StringBuilder descrB = new StringBuilder();
        
        descrB.append(String.format(
                StringsFr.ADDITIONAL_CARDS_ARE,
                cardsDescription(drawnCards)
        ));
        
        if (additionalCost == 0) {
            descrB.append(StringsFr.NO_ADDITIONAL_COST);
        } else {
            descrB.append(String.format(
                    StringsFr.SOME_ADDITIONAL_COST,
                    additionalCost, StringsFr.plural(additionalCost)
            ));
        }
        
        return descrB.toString();
    }
    
    /**
     * Retourne le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné.
     * 
     * @param route
     *          la route en tunnel dont le joueur ne s'est pas emparé
     * @return
     *          le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     */
    public String didNotClaimRoute(Route route) {
        return String.format(
                StringsFr.DID_NOT_CLAIM_ROUTE,
                playerName,
                routeDescription(route)
        );
    }
    
    /**
     * Retourne le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égal à 2) de wagons,
     * et que le dernier tour commence donc.
     * 
     * @param carCount
     *          le nombre de wagons dont le joueur dispose 
     * @return
     *          le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égal à 2) de wagons,
     *          et que le dernier tour commence donc
     */
    public String lastTurnBegins(int carCount) {
        return String.format(
                StringsFr.LAST_TURN_BEGINS,
                playerName,
                carCount, StringsFr.plural(carCount)
        );
    }
    
    /**
     * Retourne le message déclarant que le joueur obtient le bonus de fin de partie
     * grâce au chemin donné, qui est le plus long, ou l'un des plus longs.
     * 
     * @param longestTrail 
     *          le plus long chemin de la partie, ou l'un des plus longs
     * @return
     *          le message déclarant que le joueur obtient le bonus de fin de partie
     *          grâce au chemin donné
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        final String trailDescr = 
                longestTrail.station1().name()
                + StringsFr.EN_DASH_SEPARATOR
                + longestTrail.station2().name();
        
        return String.format(StringsFr.GETS_BONUS, playerName, trailDescr);
    }
    
    /**
     * Retourne le message déclarant que le joueur remporte la partie avec le nombre de points donnés,
     * son adversaire n'en ayant obtenu que <code>loserPoints</code>.
     * 
     * @param points
     *          le nombre de points remportés par le joueur lié au récepteur (le vainqueur)
     * @param loserPoints
     *          le nombre de points remportés par le perdant
     * @return
     *          le message déclarant que le joueur remporte la partie avec le nombre de points donnés
     */
    public String won(int points, int loserPoints) {
        return String.format(
                StringsFr.WINS,
                playerName,
                points, StringsFr.plural(points),
                loserPoints, StringsFr.plural(loserPoints)
        );
    }
    
}
