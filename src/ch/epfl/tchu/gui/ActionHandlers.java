package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Interface publique contenant 5 interfaces fonctionnelles imbriquées
 * décrivant différents gestionnaires d'actions.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public interface ActionHandlers {
    
    /**
     * Interface fonctionnelle décrivant un
     * gestionnaire de tirage de billets.
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * Méthode appellée lorsque le joueur
         * désire tirer des billets.
         */
        void onDrawTickets();
    }
    
    /**
     * Interface fonctionnelle décrivant un
     * gestionnaire de tirage de cartes.
     */
    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * Méthode appellée lorsque le joueur
         * désire tirer une carte à l'emplacement donné.
         * @param slot
         *          l'emplacement auquel le joueur
         *          désire tirer une carte : de 0 à 4 pour les cartes face visible,
         *          <code>Constants.DECK_SLOT</code> pour la pioche.
         */
        void onDrawCard(int slot);
    }
    
    /**
     * Interface fonctionnelle décrivant un
     * gestionnaire de prise de route.
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * Méthode appellée losrque le joueur désire
         * s'emparer de la route donnée au moyen des
         * cartes initiales données.
         * 
         * @param route
         *          la route dont le joueur souhaite s'emparer
         * @param initialCards
         *          les cartes initiales avec lesquelles le joueur
         *          désire s'emparer de la route donnée
         */
        void onClaimRoute(Route route, SortedBag<Card> initialCards);
    }
    
    /**
     * Interface fonctionnelle décrivant
     * un gestionnaire de choix de billets.
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * Méthode appellée lorsque le joueur a choisi
         * de garder les billets donnés suite à un tirage de billets.
         * 
         * @param tickets les billets gardés par le joueur
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }
    
    /**
     * Interface fonctionnelle décrivant
     * un gestionnaire de choix de cartes.
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * Méthode appellée lorsque le joueur a choisi
         * d'utiliser les cartes données comme cartes initiales
         * ou additionnelles lors de la prise de possession d'une route.
         * S'il s'agit de cartes additionnelles, un multiensemble vide
         * signifie que le joueur ne peut pas ou renonce à s'emparer de tunnel.
         *
         * @param cards les cartes choisies
         */
        void onChooseCards(SortedBag<Card> cards);
    }

}
