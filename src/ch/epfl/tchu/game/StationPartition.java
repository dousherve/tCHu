package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class StationPartition implements StationConnectivity {
    
    private final int[] links;
    
    private StationPartition(int[] links) {
        this.links = links;
    }
    
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() >= links.length || s2.id() >= links.length) {
            // TODO: amélioration : surcharger Station.equals() ?
            return s1.id() == s2.id();
        } else {
            return links[s1.id()] == links[s2.id()];
        }
    }
    
    public static final class Builder {
        
        private int[] links;
        
        private int representative(int id) {
            while (links[id] != id)
                id = links[id];
            
            return id;
        }
    
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            
            this.links = new int[stationCount];
            for (int i = 0; i < stationCount; ++i)
                this.links[i] = i;
        }
        
        public Builder connect(Station s1, Station s2) {
            // On choisit arbitrairement le représentant de s1
            links[s2.id()] = representative(s1.id());
            return this;
        }
        
        public StationPartition build() {
            for (int i = 0; i < links.length; ++i)
                links[i] = representative(i);
            
            return new StationPartition(links);
        }
        
    }
    
}
