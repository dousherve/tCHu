package ch.epfl.tchu.game;

import java.util.List;

/**
 * Représente les huit couleurs utilisées dans le jeu
 * pour colorer les cartes wagon et les routes.
 * 
 * @author Louis Hervé (312937)
 */
public enum Color {
    
    BLACK,
    VIOLET,
    BLUE,
    GREEN, 
    YELLOW,
    ORANGE,
    RED,
    WHITE;
    
    /**
     * Liste immuable contenant toutes les couleurs du type énuméré,
     * dans leur ordre de déclaration.
     */
    public static final List<Color> ALL = List.of(values());
    
    /**
     * Nombre total de couleurs.
     */
    public static final int COUNT = ALL.size();
    
}
