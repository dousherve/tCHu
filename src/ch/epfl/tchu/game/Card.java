package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Type énuméré qui représente les différents types de cartes du jeu :
 * huit cartes wagon et une carte locomotive.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public enum Card {
    
    BLACK   (Color.BLACK),
    VIOLET  (Color.VIOLET),
    BLUE    (Color.BLUE),
    GREEN   (Color.GREEN),
    YELLOW  (Color.YELLOW),
    ORANGE  (Color.ORANGE),
    RED     (Color.RED),
    WHITE   (Color.WHITE),
    
    LOCOMOTIVE(null);
    
    /**
     * Liste immuable contenant toutes les cartes du type énuméré,
     * dans leur ordre de définition.
     */
    public static final List<Card> ALL = List.of(values());
    /**
     * Nombre total de cartes.
     */
    public static final int COUNT = ALL.size();
    /**
     * Liste immuable contenant uniquement les cartes wagon,
     * dans leur ordre de définition.
     */
    public static final List<Card> CARS = Collections.unmodifiableList(
            // Sous-liste de ALL du premier jusqu'à l'avant-dernier élément,
            // le dernier étant LOCOMOTIVE.
            ALL.subList(0, COUNT - 1)
    );
    
    private final Color color;
    
    /**
     * Retourne l'unique carte wagon dont la couleur est celle 
     * passée en paramètre.
     * 
     * @param color
     *          la couleur de la carte désirée (non-null)
     * @throws NullPointerException
     *          si la couleur passée en paramètre est null
     * @return
     *          l'unique carte wagon dont la couleur est color
     */
    public static Card of(Color color) {
        Objects.requireNonNull(color);
    
        switch (color) {
            case BLACK:
                return Card.BLACK;
            case VIOLET:
                return Card.VIOLET;
            case BLUE:
                return Card.BLUE;
            case GREEN:
                return Card.GREEN;
            case YELLOW:
                return Card.YELLOW;
            case ORANGE:
                return Card.ORANGE;
            case RED:
                return Card.RED;
            case WHITE:
                return Card.WHITE;
            default:
                throw new IllegalArgumentException("Il n'existe aucune carte de la couleur donnée.");
        }
        
    }
    
    /**
     * Construit une nouvelle carte en prenant sa couleur en paramètre.
     *
     * @param color
     *          la couleur de la carte (peut être <code>null</code>)
     */
    Card(Color color) {
        this.color = color;
    }
    
    /**
     * Retourne la couleur de cette carte si c'est un wagon,
     * ou <code>null</code> si c'est une locomotive.
     *
     * @return la couleur de cette carte si c'est un wagon, 
     *          ou <code>null</code> si c'est une locomotive.
     */
    public Color color() {
        return color;
    }
    
}
