package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Classe finale et immuable représentant une route reliant deux villes voisines.
 *
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Route {

    private final String id;
    private final Station station1, station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Type énuméré représentant les deux niveaux auquel une route peut se trouver.
     */
    public enum Level {
        OVERGROUND, UNDERGROUND
    }

    /**
     * Construit une route avec les paramètres donnés.
     *
     * @param id
     *          l'identité unique de la route
     * @param station1
     *          la gare de départ
     * @param station2
     *          la gare d'arrivée
     * @param length
     *          la longueur de la route
     * @param level
     *          le niveau de la route
     * @param color
     *          la couleur de la route, null si elle est de couleur neutre
     *
     * @throws IllegalArgumentException
     *          si les deux fares sont égales (au sens de la methode <code>equals</code>
     *          ou si la longueur n'est pas comrpise entre <code>Constants.MIN_ROUTE_LENGTH</code> et <code>Constants.MAX_ROUTE_LENGTH</code>
     *
     * @throws NullPointerException
     *          si l'identité, l'une des 2 gares ou le niveau sont nuls
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        final boolean IS_IN_BOUNDS = (
                length >= Constants.MIN_ROUTE_LENGTH
                && length <= Constants.MAX_ROUTE_LENGTH
        );

        Preconditions.checkArgument(
                ! station1.equals(station2) && IS_IN_BOUNDS
        );

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }

    public String id() {
        return id;
    }

    public Station station1() {
        return station1;
    }

    public Station station2() {
        return station2;
    }

    public int length() {
        return length;
    }

    public Level level() {
        return level;
    }

    public Color color() {
        return color;
    }

    public List<Station> stations() {
        return List.of(station1, station2);
    }

    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(
                station.equals(station1) || station.equals(station2)
        );

        return station.equals(station1) ? station2 : station1;
    }

    public List<SortedBag<Card>> possibleClaimCards() {
        return Collections.emptyList();
    }
}
