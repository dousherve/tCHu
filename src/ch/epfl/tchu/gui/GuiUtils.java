package ch.epfl.tchu.gui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * Classe finale et non instanciable contenant toutes les constantes
 * et méthodes utilitaires statiques
 * utilisées pour l'interface graphique de tCHu.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
final class GuiUtils {
    
    private GuiUtils() {}
    
    /**
     * Joue le son dont le nom du fichier audio
     * correspondant est passé en argument.
     *
     * @param sound
     *          le nom du fichier audio à jouer,
     *          sans extension (.wav)
     * @param count 
     *          le nombre de fois qu'il faut
     *          jouer le son
     */
    static void playSound(String sound, int count) {
        MediaPlayer mp = new MediaPlayer(new Media(
                new File(String.format("resources/%s.wav", sound))
                        .toURI().toString()
        ));
        
        mp.setCycleCount(count);
        mp.play();
    }
    
    /**
     * Joue le son dont le nom du fichier audio
     * correspondant est passé en argument.
     * 
     * @param sound
     *          le nom du fichier audio à jouer,
     *          sans extension (.wav)
     */
    static void playSound(String sound) {
        playSound(sound, 1);
    }
    
    /**
     * Ressource de la carte en mode sombre.
     */
    static final String DARK_MAP_URL = "map-dark.png";
    
    /**
     * Ressource de la carte normale.
     */
    static final String NORMAL_MAP_URL = "map.png";
    
    /**
     * Le nom du fichier audio à lire lors de la pioche
     * d'une carte.
     */
    static final String CARD_SOUND = "card";
    
    /**
     * Le nom du fichier audio à lire lors de la prise
     * d'une route.
     */
    static final String HAMMER_SOUND = "hammer";
    
    /**
     * Le nom du fichier audio à lire lors de la prise
     * d'une route
     */
    static final String TRAIN_SOUND = "train_alarm";
    
    /**
     * Le titre de la fenêtre de jeu, prêt à être formattée
     * à l'aide de la méthode <code>format</code> de {@link String}
     * pour y incorporer le nom du joueur auquel correspond
     * l'interface.
     */
    static final String WINDOW_TITLE = "tCHu \u2014 %s";
    
    // MARK:- Dimensions, constantes numériques
    
    /**
     * La largeur de la piste.
     */
    static final int TRACK_WIDTH = 36;
    
    /**
     * La hauteur de la piste.
     */
    static final int TRACK_HEIGHT = 12;
    
    /**
     * Le rayon des cercles sur les pistes des routes
     * prises par un joueur.
     */
    static final int CLAIMED_CIRCLE_RADIUS = 3;
    
    /**
     * La coordonnée en x des cercles sur les pistes des routes
     * prises par un joueur.
     */
    static final int CLAIMED_CIRCLE_CX = 12;
    
    /**
     * La coordonnée en y des cercles sur les pistes des routes
     * prises par un joueur.
     */
    static final int CLAIMED_CIRCLE_CY = 6;
    
    /**
     * La largeur du rectangle englobant la vue d'une carte.
     */
    static final int OUTSIDE_CARD_WIDTH = 60;
    
    /**
     * La hauteur du rectangle englobant la vue d'une carte.
     */
    static final int OUTSIDE_CARD_HEIGHT = 90;
    
    /**
     * La largeur du rectangle représentant une carte.
     */
    static final int INSIDE_CARD_WIDTH = 40;
    
    /**
     * La hauteur du rectangle représentant une carte.
     */
    static final int INSIDE_CARD_HEIGHT = 70;
    
    /**
     * La largeur de la jauge des boutons de pioche.
     */
    static final int GAUGE_WIDTH = 50;
    
    /**
     * La hauteur de la jauge des boutons de pioche.
     */
    static final int GAUGE_HEIGHT = 5;
    
    /**
     * Le rayon du cercle de couleur dans la vue des informations.
     */
    static final int STATS_CIRCLE_RADIUS = 5;
    /**
     * Le nombre maximal d'informations affichées à la fois
     * dans la vue des informations.
     */
    static final int MAX_INFOS_COUNT = 5;
    
    /**
     * Nom du fichier contenant l'icône du train tCHu.
     */
    static final String TRAIN_ICON = "train-icon.png";
    
    // MARK:- URIs des feuilles de styles
    
    /**
     * Nom de la feuille de styles pour les fenêtres modales
     * de choix.
     */
    static final String CHOOSER_STYLES = "chooser.css";
    
    /**
     * Nom de la feuille de styles pour les couleurs. 
     */
    static final String COLORS_STYLES = "colors.css";
    
    /**
     * Nom de la feuille de styles pour les couleurs du mode sombre. 
     */
    static final String COLORS_DARK_STYLES = "colors-dark.css";
    
    /**
     * Nom de la feuille de styles pour les pioches. 
     */
    static final String DECKS_STYLES = "decks.css";
    
    /**
     * Nom de la feuille de styles pour la vue des informations.
     */
    static final String INFO_STYLES = "info.css";
    
    /**
     * Nom de la feuille de styles pour la vue de la carte.
     */
    static final String MAP_STYLES = "map.css";
    
    /**
     * Nom de la feuille de styles pour le mode sombre.
     */
    static final String DARK_STYLES = "dark.css";
    
    // MARK:- Classes CSS
    
    /**
     * Classe CSS pour les éléments en arrière-plan. 
     */
    static final String BACKGROUND_CLASS = "background";
    
    /**
     * Classe CSS pour les éléments au premier plan. 
     */
    static final String FOREGROUND_CLASS = "foreground";
    
    /**
     * Classe CSS pour les routes. 
     */
    static final String ROUTE_CLASS = "route";
    
    /**
     * Classe CSS pour les routes neutres. 
     */
    static final String NEUTRAL_CLASS = "NEUTRAL";
    
    /**
     * Classe CSS pour les pistes. 
     */
    static final String TRACK_CLASS = "track";
    
    /**
     * Classe CSS pour les rectangles pleins. 
     */
    static final String FILLED_CLASS = "filled";
    
    /**
     * Classe CSS pour les wagons. 
     */
    static final String CAR_CLASS = "car";
    
    /**
     * Classe CSS pour les cartes. 
     */
    static final String CARD_CLASS = "card";
    
    /**
     * Classe CSS pour les rectangles englobants les cartes. 
     */
    static final String OUTSIDE_CARD_CLASS = "outside";
    
    /**
     * Classe CSS pour les rectangles représentant les cartes. 
     */
    static final String INSIDE_CARD_CLASS = "inside";
    
    /**
     * Classe CSS pour l'image du train. 
     */
    static final String TRAIN_IMAGE_CLASS = "train-image";
    
    /**
     * Classe CSS pour le compteur de cartes. 
     */
    static final String COUNT_CLASS = "count";
    
    /**
     * Classe CSS pour les jauges des boutons de pioche. 
     */
    static final String GAUGED_CLASS = "gauged";
    
    /**
     * Classe CSS pour les jauges à moitié pleines. 
     */
    static final String IS_MEDIUM_CLASS = "is-medium";
    
    /**
     * Classe CSS pour les jauges presque vides. 
     */
    static final String IS_LOW_CLASS = "is-low";
    
    // MARK:- Identifiants CSS
    
    /**
     * Identifiant CSS pour les billets.
     */
    static final String TICKETS_ID = "tickets";
    
    /**
     * Identifiant CSS pour la vue de la main.
     */
    static final String CARD_PANE_ID = "hand-pane";
    
    /**
     * Identifiant CSS pour les statistiques du joueur
     * dans la vue des informations.
     */
    static final String PLAYER_STATS_ID = "player-stats";
    
    /**
     * Identifiant CSS pour les informations de la partie.
     */
    static final String GAME_INFO_ID = "game-info";
    
    /**
     * Une chaîne de caractère prête à être formattée
     * contenant la propriété CSS de la couleur de fond.
     */
    static final String BG_COLOR_PROP = "-fx-background-color: %s";
    
    /**
     * La couleur CSS du joueur 1.
     */
    static final String P1_COLOR = "lightblue";
    
    /**
     * La couleur CSS du joueur 2.
     */
    static final String P2_COLOR = "lightpink";
    
}
