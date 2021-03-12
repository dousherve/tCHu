package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Trail;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfoTest {

    Info m = new Info("Mallo");
    Info l = new Info("Louis");

    SortedBag<Card> cards = SortedBag.of(
            2, Card.GREEN, 1, Card.BLACK
    );

    SortedBag<Card> cards2 = SortedBag.of(
            2, Card.VIOLET, 1, Card.LOCOMOTIVE
    );

    SortedBag<Card> cardsUnion = cards.union(cards2);

    @Test
    void cardNameWorks() {
        assertEquals("verte", Info.cardName(Card.GREEN, 1));
        assertEquals("bleues", Info.cardName(Card.BLUE, 3));
        assertEquals("rouges", Info.cardName(Card.RED, 2));
        assertEquals("orange", Info.cardName(Card.ORANGE, 1));
        assertEquals("noire", Info.cardName(Card.BLACK, 1));
        assertEquals("violettes", Info.cardName(Card.VIOLET, 15));
        assertEquals("jaune", Info.cardName(Card.YELLOW, -1));
        assertEquals("blanches", Info.cardName(Card.WHITE, 108));
        assertEquals("locomotives", Info.cardName(Card.LOCOMOTIVE, 141));
    }

    @Test
    void drawWorks() {
        assertEquals("\nMallo et Loulou sont ex æqo avec 15 points !\n", Info.draw(List.of("Mallo", "Loulou"), 15));
    }

    @Test
    void willPlayFirstWorks() {
        assertEquals("Mallo jouera en premier.\n\n", m.willPlayFirst());
    }

    @Test
    void keptTicketsWorks() {
        assertEquals("Louis a gardé 1 billet.\n", l.keptTickets(1));
        assertEquals("Louis a gardé 141 billets.\n", l.keptTickets(141));
    }

    @Test
    void canPlayWorks() {
        assertEquals("\nC'est à Mallo de jouer.\n", m.canPlay());
    }

    @Test
    void drewTicketsWorks() {
        assertEquals("Louis a tiré 1 billet...\n", l.drewTickets(1));
        assertEquals("Louis a tiré 141 billets...\n", l.drewTickets(141));
    }

    @Test
    void drewBlindCardWorks() {
        assertEquals("Mallo a tiré une carte de la pioche.\n", m.drewBlindCard());
    }

    @Test
    void drewVisibleCardWorks() {
        assertEquals("Louis a tiré une carte noire visible.\n", l.drewVisibleCard(Card.BLACK));
        assertEquals("Mallo a tiré une carte rouge visible.\n", m.drewVisibleCard(Card.RED));
    }

    @Test
    void claimedRouteWorks() {
        assertEquals("Louis a pris possession de la route Fribourg – Lausanne au moyen de 1 noire et 2 vertes.\n", l.claimedRoute(ChMap.routes().get(136 - 92), cards));
        assertEquals("Mallo a pris possession de la route Fribourg – Lausanne au moyen de 1 noire.\n", m.claimedRoute(ChMap.routes().get(136 - 92), SortedBag.of(Card.BLACK)));
        assertEquals("Mallo a pris possession de la route Genève – Lausanne au moyen de 1 noire, 2 violettes, 2 vertes et 1 locomotive.\n", m.claimedRoute(ChMap.routes().get(138 - 92), cardsUnion));
    }

    @Test
    void attemptsTunnelClaimWorks() {
        assertEquals("Louis tente de s'emparer du tunnel La Chaux-de-Fonds – Yverdon au moyen de 1 noire, 2 violettes, 2 vertes et 1 locomotive !\n", l.attemptsTunnelClaim(ChMap.routes().get(150-92), cardsUnion));
    }

    @Test
    void drewAdditionalCardsWorks() {
        assertEquals("Les cartes supplémentaires sont 2 violettes et 1 locomotive. Elles impliquent un coût additionnel de 3 cartes.\n", m.drewAdditionalCards(cards2, 3));
        assertEquals("Les cartes supplémentaires sont 2 violettes et 1 locomotive. Elles impliquent un coût additionnel de 1 carte.\n", l.drewAdditionalCards(cards2, 1));
        assertEquals("Les cartes supplémentaires sont 1 noire et 2 vertes. Elles n'impliquent aucun coût additionnel.\n", m.drewAdditionalCards(cards, 0));
        assertEquals("Les cartes supplémentaires sont 1 rouge. Elles n'impliquent aucun coût additionnel.\n", m.drewAdditionalCards(SortedBag.of(Card.RED), 0));
    }

    @Test
    void didNotClaimRouteWorks() {
        assertEquals("Louis n'a pas pu (ou voulu) s'emparer de la route Fribourg – Lausanne.\n", l.didNotClaimRoute(ChMap.routes().get(136 - 92)));
    }

    @Test
    void lastTurnBegins() {
        assertEquals("\nMallo n'a plus que 1 wagon, le dernier tour commence !\n", m.lastTurnBegins(1));
        assertEquals("\nLouis n'a plus que 2 wagons, le dernier tour commence !\n", l.lastTurnBegins(2));
    }

    @Test
    void getsLongestTrailBonus() {
        assertEquals(
                "\nMallo reçoit un bonus de 10 points pour le plus long trajet (Fribourg – Lausanne).\n",
                m.getsLongestTrailBonus(
                        Trail.longest(
                                List.of(ChMap.routes().get(136 - 92))
                        )
                )
        );
    }

    @Test
    void won() {
        assertEquals("\nLouis remporte la victoire avec 141 points, contre 108 points !\n", l.won(141, 108));
        assertEquals("\nMallo remporte la victoire avec 1 point, contre 1 point !\n", m.won(1, 1));
    }

}