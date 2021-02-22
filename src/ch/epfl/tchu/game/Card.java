package ch.epfl.tchu.game;

import java.util.List;

public enum Card {
    
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    
    LOCOMOTIVE(null);
    
    private Color color;
    
    Card(Color color) {
        this.color = color;
    }
    
    public final static List<Card> ALL = List.of(values());
    public final static int COUNT = ALL.size();
    
    // For CARS, we take the sublist of ALL starting from the beginning,
    // until the last element which is LOCOMOTIVE, since we don't want it.
    // Though it relies on LOCOMOTIVE being the last element, it's the cleanest way we found.
    public final static List<Card> CARS = ALL.subList(0, COUNT - 1);
    
    public Color color() {
        return color;
    }
    
    public static Card of(Color color) {
        for (Card c : CARS) {
            if (c.color() == color)
                return c;
        }
        
        return null;
    }
    
}
