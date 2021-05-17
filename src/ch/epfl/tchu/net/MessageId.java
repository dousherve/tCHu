package ch.epfl.tchu.net;

/**
 * Type énuméré public décrivant les types de messages que le serveur
 * peut envoyer aux clients.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public enum MessageId {

    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS

}
