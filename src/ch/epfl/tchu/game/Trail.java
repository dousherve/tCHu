package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
     * Retourne le plus long chemin du réseau constitué des routes données.
     * S'il y a plusieurs chemins de longueur maximale, celui qui est retourné n'est pas spécifié.
     *
     * @param routes
     *          la liste de route
     *
     * @return
     *          le plus long chemin de réseau constitué des routes données
     *          si la liste est vide, retourne un chemin de longueur zéro dont les gares sont égales à null
     */
    public static Trail longest(List<Route> routes) {
        // Liste des chemins constitués d'une seule route
        List<Trail> trails = new ArrayList<>();
        int maxLength = -1;
        Trail longestTrail = null;
        
        final List<Route> LENGTH_ONE_ROUTES = routes
                .stream()
                .filter(route -> route.length() == 1)
                .collect(Collectors.toUnmodifiableList());
        
        for (Route r : LENGTH_ONE_ROUTES) {
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
        
        while (! trails.isEmpty()) {
            
            List<Trail> tempTrails = new ArrayList<>();
            for (Trail t : trails) {
                // TODO: utiliser removeAll() et pas contains()
                final List<Route> NEW_ROUTES = routes
                        .stream()
                        .filter(r -> (! t.routes.contains(r) && r.stations().contains(t.station2)))
                        .collect(Collectors.toUnmodifiableList());
                
                for (Route r : NEW_ROUTES) {
                    List<Route> newTrailRoutes = new ArrayList<>(List.copyOf(t.routes));
                    newTrailRoutes.add(r);
    
                    // La gare de départ ne change pas,
                    // mais la nouvelle gare d'arrivée est 
                    // l'opposée de l'ancienne par rapport à la route ajoutée
                    final Trail NEW_TRAIL = new Trail(
                            t.station1,
                            r.stationOpposite(t.station2),
                            newTrailRoutes
                    );
                    
                    tempTrails.add(NEW_TRAIL);
                    
                    if (NEW_TRAIL.length > maxLength) {
                        maxLength = NEW_TRAIL.length();
                        longestTrail = NEW_TRAIL;
                    }
                }
            }
            
            trails = tempTrails;
            
        }
    
        if (longestTrail != null) {
            return longestTrail;
        }
        
        // Retourne un chemin vide s'il est de longueur 0
        return new Trail(null, null, Collections.emptyList());
    }
    
    private Trail(Station station1, Station station2, List<Route> routes) {
        this.length = routes.stream().mapToInt(Route::length).sum();
        this.station1 = station1;
        this.station2 = station2;
        this.routes = routes;
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
     * Retourne la première gare
     *
     * @return
     *          la première gare
     *          ou null si le chemin est de longueur zéro
     */
    public Station station1() {
        return station1;
    }

    /**
     * Retourne la deuxième gare
     *
     * @return
     *          la deuxième gare
     *          ou null si le chemin est de longueur zéro
     */
    public Station station2() {
        return station2;
    }

    /**
     * Retourne la représentation textuelle du chemin qui doit contenir le nom de la première,
     * de la dernière (dans cette ordre), ainsi que la longueur du chemin entre parenthèses.
     *
     * @return
     *          la représentation textuelle du chemin
     *          ou Empty trail si la longueur est zéro
     */
    @Override
    public String toString() {
        if (length == 0) {
            return "Empty trail";
        }
        
        List<String> stationNames = new ArrayList<>();
    
        stationNames.add(station1.name());
        
        Station previousToStation = routes.get(0).stationOpposite(station1);
        for (int i = 1; i < routes.size() - 1; ++i) {
            final Route ROUTE = routes.get(i);
            final Station STATION = (i % 2 == 0) ? ROUTE.station1() : ROUTE.station2();
            
            stationNames.add(STATION.name());
        }
        
        return String.format(
                "%s (%d)",
                String.join(
                        " - ",
                        stationNames
                ),
                length
        );
    }
    
}
