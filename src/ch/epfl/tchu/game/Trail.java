package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe publique, finale et immuable représentant
 * un chemin dans le réseau d'un joueur.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Trail {
    
    private static final Trail EMPTY_TRAIL = new Trail(null, null, List.of());
    
    private final int length;
    private final Station station1, station2;
    private final List<Route> routes;

    /**
     * Retourne le plus long chemin du réseau composé de routes parmi celles données.
     * S'il y a plusieurs chemins de longueur maximale, celui qui est retourné est le
     * premier qui aura été traité par l'algorithme de recherche.
     *
     * @param routes
     *          la liste de routes
     *
     * @return
     *          le plus long chemin du réseau constitué des routes données ;
     *          si la liste est vide, retourne un chemin de longueur zéro
     *          dont les gares valent <code>null</code>
     */
    public static Trail longest(List<Route> routes) {
        List<Trail> trails = new ArrayList<>();
        // On ajoute tous les chemins constitués d'une seule route
        for (Route r : routes) {
            trails.add(new Trail(r.station1(), r.station2(), List.of(r)));
            trails.add(new Trail(r.station2(), r.station1(), List.of(r)));
        }
        
        // Par défaut, on initialise le chemin vide
        // au chemin le plus long
        Trail longestTrail = EMPTY_TRAIL;
        
        while (! trails.isEmpty()) {
            List<Trail> tempTrails = new ArrayList<>();
            
            for (Trail t : trails) {
                if (t.length > longestTrail.length)
                    longestTrail = t;
                
                List<Route> newRoutes = new ArrayList<>(routes);
                // On retire les routes qui appartiennent déjà au chemin...
                newRoutes.removeAll(t.routes);
                // ... ainsi que celles qui ne contiennent pas sa gare d'arrivée
                newRoutes.removeIf(r -> ! r.stations().contains(t.station2));
                
                for (Route r : newRoutes) {
                    List<Route> newTrailRoutes = new ArrayList<>(t.routes);
                    newTrailRoutes.add(r);
    
                    // La gare de départ ne change pas, mais la nouvelle gare
                    // d'arrivée est l'opposée de l'ancienne par rapport à la route ajoutée
                    tempTrails.add(new Trail(
                            t.station1, r.stationOpposite(t.station2), newTrailRoutes
                    ));
                }
            }
            
            trails = tempTrails;
        }
    
        return longestTrail;
    }
    
    private Trail(Station station1, Station station2, List<Route> routes) {
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;
        
        this.length = routes.stream()
                .mapToInt(Route::length)
                .sum();
    }

    /**
     * Retourne la longueur du chemin.
     *
     * @return la longueur du chemin
     */
    public int length() {
        return length;
    }

    /**
     * Retourne la gare de départ du chemin.
     *
     * @return
     *          la gare de départ du chemin
     *          ou <code>null</code> si le chemin est de longueur zéro
     */
    public Station station1() {
        return station1;
    }

    /**
     * Retourne la gare d'arrivée du chemin.
     *
     * @return
     *          la gare d'arrivée du chemin
     *          ou <code>null</code> si le chemin est de longueur zéro
     */
    public Station station2() {
        return station2;
    }

    /**
     * Retourne la représentation textuelle du chemin, qui contient toutes les gares
     * qui s'y trouvent, ainsi que sa longueur entre parenthèses.
     *
     * @return
     *          la représentation textuelle du chemin
     *          ou "Chemin vide" si sa longueur est zéro
     */
    @Override
    public String toString() {
        if (length == 0)
            return "Chemin vide";
        
        StringBuilder sB = new StringBuilder();
        // On ajoute le nom de la gare de départ du chemin
        sB.append(station1.name());
        
        Station previousStation = station1;
        for (Route r : routes) {
            Station newStation = r.stationOpposite(previousStation);
            sB.append(" - ").append(newStation.name());
            previousStation = newStation;
        }
        
        sB.append(" (").append(length).append(")");
        
        return sB.toString();
    }
    
}
