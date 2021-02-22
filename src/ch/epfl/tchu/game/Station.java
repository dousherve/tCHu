package ch.epfl.tchu.game;

public final class Station {
    
    private final int id;
    private final String name;
    
    public Station(int id, String name) {
        if (id < 0)
            throw new IllegalArgumentException(); // TODO: EXCEPTION SI id > 50 
        
        this.id = id;
        this.name = name;
    }
    
    public int id() {
        return id;
    }
    
    public String name() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
