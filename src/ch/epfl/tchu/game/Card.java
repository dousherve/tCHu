package ch.epfl.tchu.game;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Représente les différents types de cartes du jeu :
 * huit cartes wagon, une carte locomotive.
 * 
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
    
    private Color color;
    
    /**
     * Construit une nouvelle carte en prenant sa couleur en paramètre.
     * 
     * @param color
     *          la couleur de la carte (peut être null)
     */
    Card(Color color) {
        this.color = color;
    }
    
    /**
     * Liste immuable contenant toutes les cartes du type énuméré,
     * dans leur ordre de définition.
     */
    public static final List<Card> ALL = List.of(values());
    /**
     * Nombre total de cartes.
     */
    public static final int COUNT = ALL.size();
    
    // Pour CARS, on prend la sous-liste de ALL qui va du début
    // jusqu'à l'avant-dernier élément, le dernier étant LOCOMOTIVE.
    /**
     * Liste immuable contenant uniquement les cartes wagon,
     * dans leur ordre de définition.
     */
    public final static List<Card> CARS = Collections.unmodifiableList(
            ALL.subList(0, COUNT - 1)
    );
    
    /**
     * Retourne l'unique carte wagon dont la couleur est celle 
     * passée en paramètre.
     * 
     * @param color
     *          la couleur de la carte désirée (non-null)
     * @throws NullPointerException
     *          si la couleur est null
     * @return l'unique carte wagon dont la couleur est color
     */
    public static Card of(Color color) {
        Objects.requireNonNull(color);
        
        for (Card car : CARS) {
            if (car.color == color)
                return car;
        }
        
        // Ne retournera jamais null
        return null;
    }
    
    /**
     * Retourne la couleur de cette carte si c'est un wagon,
     * ou null si c'est une locomotive.
     *
     * @return la couleur de cette carte si c'est un wagon, 
     *          ou null si c'est une locomotive.
     */
    public Color color() {
        return color;
    }
    
}
