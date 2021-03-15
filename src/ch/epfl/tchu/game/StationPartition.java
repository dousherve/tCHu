package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Classe publique, finale et immuable qui représente une partition (aplatie) de gares.
 *
 * Elle implémente l'interface <code>{@link StationConnectivity}</code> car ses instances ont pour but
 * d'être passées à la méthode <code>points()</code> de <code>{@link Ticket}</code>.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class StationPartition implements StationConnectivity {
    
    private final int[] links;
    
    private StationPartition(int[] links) {
        this.links = new int[links.length];
        System.arraycopy(links, 0, this.links, 0, links.length);
    }
    
    /**
     * Méthode de <code>{@link StationConnectivity}</code> qui retourne vrai si et seulement si les
     * gares passées en paramètre sont reliées par le réseau du joueur.
     *
     * @param s1
     *          la gare de départ
     * @param s2
     *          la gare d'arrivée
     * @return un booléen indiquant si les gares passées en paramètre
     *          sont reliées par le réseau du joueur
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() >= links.length || s2.id() >= links.length) {
            // TODO: amélioration : surcharger Station.equals() ?
            return s1.id() == s2.id();
        } else {
            return links[s1.id()] == links[s2.id()];
        }
    }

    /**
     * Classe publique et finale imbriquée statiquement dans <code>{@link StationPartition}</code>,
     * qui représente un bâtisseur de partition de gare qui construit la version profonde de la partition
     * et qui l'aplatit juste avant d'appeler le constructeur de <code>{@link StationPartition}</code>
     * dans la méthode <code>build</code>.
     */
    public static final class Builder {
        
        private int[] links;

        private int representative(int id) {
            while (links[id] != id)
                id = links[id];
            
            return id;
        }

        /**
         * Construit un bâtisseur de partition d'un ensemble de gares dont l'identité est comprise entre
         * 0 (inclus) et <code>stationCount</code> (exclus)
         *
         * @param stationCount
         * @throws IllegalArgumentException
         *          si <code>stationCount</code> est strictement négatif (< 0)
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            
            this.links = new int[stationCount];
            for (int i = 0; i < stationCount; ++i)
                this.links[i] = i;
        }

        /**
         * Joint les sous-ensembles contenant les deux gares passées en argument.
         * Élit l'un des deux représentants comme représentant du sous-ensemble joint.
         *
         * @param s1
         *          la première gare à joindre
         * @param s2
         *          la deuxième gare à joindre
         * @return
         *          le bâtisseur
         */
        public Builder connect(Station s1, Station s2) {
            // On choisit arbitrairement le représentant de s1
            links[s2.id()] = representative(s1.id());
            return this;
        }

        /**
         * Retourne la partition aplatie des gares correspondant à la partition profonde
         * en cours de construction par ce bâtisseur.
         *
         * @return
         *          la partition aplatie de la partiton profonde en cours de construction par le bâtisseur
         */
        public StationPartition build() {
            for (int i = 0; i < links.length; ++i)
                links[i] = representative(i);
            
            return new StationPartition(links);
        }
        
    }
    
}
