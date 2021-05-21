package ch.epfl.tchu.gui;

/**
 * Classe finale et non instanciable contenant toutes les constantes
 * utilisées pour l'interface graphique de tCHu.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
final class ConstantsGui {
    
    private ConstantsGui() {}
    
    static final String WINDOW_TITLE = "tCHu \u2014 %s";
    
    // MARK:- Dimensions, constantes numériques
    
    static final int TRACK_WIDTH = 36;
    static final int TRACK_HEIGHT = 12;
    
    static final int CLAIMED_CIRCLE_RADIUS = 3;
    static final int CLAIMED_CIRCLE_CX = 12;
    static final int CLAIMED_CIRCLE_CY = 6;
    
    static final int OUTSIDE_CARD_WIDTH = 60;
    static final int OUTSIDE_CARD_HEIGHT = 90;
    static final int INSIDE_CARD_WIDTH = 40;
    static final int INSIDE_CARD_HEIGHT = 70;
    
    static final int GAUGE_WIDTH = 50;
    static final int GAUGE_HEIGHT = 5;
    
    static final int STATS_CIRCLE_RADIUS = 5;
    static final int MAX_INFOS_COUNT = 5;
    
    // MARK:- URIs des feuilles de styles
    
    static final String CHOOSER_STYLES = "chooser.css";
    static final String COLORS_STYLES = "colors.css";
    static final String DECKS_STYLES = "decks.css";
    static final String INFO_STYLES = "info.css";
    static final String MAP_STYLES = "map.css";
    
    // MARK:- Classes CSS
    
    static final String BACKGROUND_CLASS = "background";
    static final String FOREGROUND_CLASS = "foreground";
    
    static final String ROUTE_CLASS = "route";
    static final String NEUTRAL_CLASS = "NEUTRAL";
    static final String TRACK_CLASS = "track";
    static final String FILLED_CLASS = "filled";
    static final String CAR_CLASS = "car";
    static final String CARD_CLASS = "card";
    static final String OUTSIDE_CARD_CLASS = "outside";
    static final String INSIDE_CARD_CLASS = "inside";
    static final String TRAIN_IMAGE_CLASS = "train-image";
    static final String COUNT_CLASS = "count";
    static final String GAUGED_CLASS = "gauged";
    
    // MARK:- Identifiants CSS
    
    static final String TICKETS_ID = "tickets";
    static final String CARD_PANE_ID = "hand-pane";
    static final String PLAYER_STATS_ID = "player-stats";
    static final String GAME_INFO_ID = "game-info";
    
}
