package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe finale et immuable représentant un chemin dans le réseau d'un joueur.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Trail {
    
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
     *          si la liste est vide, retourne un chemin de longueur zéro dont les gares sont égales à null
     */
    public static Trail longest(List<Route> routes) {
        // Si la liste de routes est vide, on retourne un chemin vide
        // dont les gares sont null
        if (routes.isEmpty()) {
            return new Trail(
                    null, null, Collections.emptyList()
            );
        }
            
        List<Trail> trails = new ArrayList<>();
        // On ajoute tous les chemins constitués d'une seule route
        for (Route r : routes) {
            trails.add(
                    new Trail(
                            r.station1(), r.station2(),
                            Collections.singletonList(r)
                    )
            );
            trails.add(
                    new Trail(
                            r.station2(), r.station1(),
                            Collections.singletonList(r)
                    )
            );
        }
    
        // On sait que trails n'est pas vide, il contient à ce stade
        // au minimum deux chemins, s'il n'y a qu'une route
        Trail longestTrail = trails.get(0);
    
        while (! trails.isEmpty()) {
            List<Trail> tempTrails = new ArrayList<>();
            
            for (Trail t : trails) {
                if (t.length > longestTrail.length) {
                    longestTrail = t;
                }
                
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
                    final Trail NEW_TRAIL = new Trail(
                            t.station1,
                            r.stationOpposite(t.station2),
                            newTrailRoutes
                    );
                    
                    tempTrails.add(NEW_TRAIL);
                }
            }
            
            trails = tempTrails;
        }
    
        return longestTrail;
    }
    
    private Trail(Station station1, Station station2, List<Route> routes) {
        this.station1 = station1;
        this.station2 = station2;
        this.routes = List.copyOf(routes);
    
        // La longueur du chemin est la somme 
        // de celles de toutes les routes qui le composent
        this.length = routes.stream()
                .mapToInt(Route::length)
                .sum();
    }

    /**
     * Retourne la longueur du chemin
     *
     * @return
     *          la longueur du chemin
     */
    public int length() {
        return length;
    }

    /**
     * Retourne la gare de départ du chemin
     *
     * @return
     *          la gare de départ du chemin
     *          ou null si le chemin est de longueur zéro
     */
    public Station station1() {
        return station1;
    }

    /**
     * Retourne la gare d'arrivée du chemin
     *
     * @return
     *          la gare d'arrivée du chemin
     *          ou null si le chemin est de longueur zéro
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
     *          ou "Empty trail" si sa longueur est zéro
     */
    @Override
    public String toString() {
        if (length == 0) {
            return "Empty trail";
        }
        
        List<String> stationNames = new ArrayList<>();
        
        // On ajoute le nom de la gare de départ du chemin
        stationNames.add(station1.name());
        
        Station previousStation = station1;
        for (Route r : routes) {
            final Station NEW_STATION = r.stationOpposite(previousStation);
            stationNames.add(NEW_STATION.name());
            previousStation = NEW_STATION;
        }
        
        return String.format(
                "%s (%d)",
                String.join(" - ", stationNames), length
        );
    }
    
}
