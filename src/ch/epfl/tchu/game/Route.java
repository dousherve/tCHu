package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
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
     * Type énuméré imbriqué représentant les deux niveaux auxquels une route peut se trouver.
     */
    public enum Level {
        OVERGROUND, UNDERGROUND
    }

    /**
     * Construit une route avec l'identité <code>id</code>,
     * les gares <code>station1</code> et <code>station2</code>,
     * la longueur <code>length</code>,
     * le niveau <code>level</code> ainsi que
     * la couleur <code>color</code> donnés.
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
     *          le niveau auquel la route se trouve : à la surface ou bien dans un tunnel
     * @param color
     *          la couleur de la route, ou <code>null</code> si elle est de couleur neutre
     *
     * @throws IllegalArgumentException
     *          si les deux gares sont égales (au sens de la méthode <code>equals</code>)
     *          ou si la longueur n'est pas comrpise entre
     *          <code>Constants.MIN_ROUTE_LENGTH</code> et <code>Constants.MAX_ROUTE_LENGTH</code>
     *
     * @throws NullPointerException
     *          si l'identité, l'une des 2 gares ou bien le niveau sont <code>null</code>
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        final boolean isInBounds = (
                length >= Constants.MIN_ROUTE_LENGTH
                && length <= Constants.MAX_ROUTE_LENGTH
        );
        
        Preconditions.checkArgument(
                ! station1.equals(station2) && isInBounds
        );

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.length = length;
        this.level = Objects.requireNonNull(level);
        this.color = color;
    }

    /**
     * Retourne une chaîne de caractères représentant l'identité de la Route.
     *
     * @return
     *          l'identité de la Route
     */
    public String id() {
        return id;
    }

    /**
     * Retourne la première gare de la Route.
     *
     * @return
     *          la première gare de la Route
     */
    public Station station1() {
        return station1;
    }

    /**
     * Retourne la deuxième gare de la Route.
     *
     * @return
     *          la deuxième gare de la Route
     */
    public Station station2() {
        return station2;
    }

    /**
     * Retourne la longueur de la Route.
     *
     * @return
     *          la longueur de la Route
     */
    public int length() {
        return length;
    }

    /**
     * Retourne le niveau de la Route.
     *
     * @return
     *          le niveau de la Route
     */
    public Level level() {
        return level;
    }

    /**
     * Retourne la couleur de la Route.
     *
     * @return
     *          la couleur de la Route
     *          ou <code>null</code> si la couleur est neutre
     */
    public Color color() {
        return color;
    }

    /**
     * Retourne la liste immuable des deux gares du constructeur
     * dans l'ordre dans lequel elles apparaissent.
     *
     * @return
     *          la liste immuable composée de la première gare puis de la deuxième
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Retourne la gare opposée à celle passée en argument.
     * En d'autres termes, retourne <code>station1</code> si l'on passe <code>station2</code>,
     * et inversement.
     *
     * @param station
     *          la gare dont on veut l'opposée
     *
     * @throws IllegalArgumentException
     *          si la gare passée en argument (<code>station</code>)
     *          n'est pas une des deux gares de la Route
     *
     * @return
     *          la gare opposée à celle passée en argument
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(
                stations().contains(station)
        );

        return station.equals(station1) ? station2 : station1;
    }

    /**
     * Retourne la liste immuable de tous les ensembles triés de cartes
     * qui peuvent être jouées pour s'emparer d'une route.
     * La liste est triée par ordre croissant du nombre de cartes locomotives puis par couleur.
     *
     * @return
     *          la liste immuable de tous les ensembles triés de cartes
     *          qui peuvent être jouées pour s'emparer d'une route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleClaimCards = new ArrayList<>();
        
        if (level == Level.OVERGROUND) {
            // Route en surface
            
            if (color != null) {
                // Route colorée
                possibleClaimCards.add(
                        SortedBag.of(length, Card.of(color))
                );
            } else {
                // Route de couleur neutre
                for (Color c : Color.values()) {
                    possibleClaimCards.add(
                            SortedBag.of(length, Card.of(c))
                    );
                }
            }
            
        } else if (level == Level.UNDERGROUND) {
            // Route dans un tunnel
    
            for (int l = 0; l < length; ++l) {
                // l représente le nombre de locomotives
                
                if (color != null) {
                    // Tunnel coloré
                    possibleClaimCards.add(
                            SortedBag.of(
                                    length - l, Card.of(color),
                                    l, Card.LOCOMOTIVE
                            )
                    );
                } else {
                    // Tunnel de couleur neutre
                    for (Color c : Color.values()) {
                        possibleClaimCards.add(
                                SortedBag.of(
                                        length - l, Card.of(c),
                                        l, Card.LOCOMOTIVE
                                )
                        );
                    }
                }
                
            }
    
            // Ajout du nombre de cartes locomotive maximum
            possibleClaimCards.add(
                    SortedBag.of(length, Card.LOCOMOTIVE)
            );
        }
        
        return Collections.unmodifiableList(possibleClaimCards);
    }

    /**
     * Retourne le nombre de cartes additionnelles à jouer pour s'emparer de la route dans un tunnel.
     *
     * @param claimCards
     *          les cartes posées par le joueur
     * @param drawnCards
     *          les 3 cartes du sommet de la pioche
     *
     * @throws IllegalArgumentException
     *          si la route à laquelle on applique la méthode n'est pas un tunnel
     *          ou si <code>drawnCards</code> ne contient pas exactement
     *          <code>Constants.ADDITIONAL_TUNNEL_CARDS</code> cartes (3 cartes)
     *
     * @return
     *          le nombre de cartes additionnelles à jouer pour s'emparer de la route dans un tunnel
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(
                level == Level.UNDERGROUND && drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS
        );
        
        final SortedBag<Card> coloredDrawnCards = drawnCards.difference(
                SortedBag.of(drawnCards.countOf(Card.LOCOMOTIVE), Card.LOCOMOTIVE)
        );
        
        int commonCardsCount = 0;
        for (Card c : coloredDrawnCards) {
            if (claimCards.contains(c))
                ++commonCardsCount;
        }
        
        return commonCardsCount + drawnCards.countOf(Card.LOCOMOTIVE);
    }

    /**
     * Retourne le nombre de points de construction obtenus en s'emparant de la route.
     *
     * @return
     *          le nombre de points gagnés en s'emparant de la route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }
    
}
