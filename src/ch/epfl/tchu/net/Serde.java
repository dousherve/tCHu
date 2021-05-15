package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Interface générique publique représentant un objet capable de sérialiser
 * et désérialiser des valeurs d'un type donné.
 * 
 * @param <T> le type d'objet à sérialiser/désérialiser
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public interface Serde<T> {
    
    // MARK:- Séparateurs
    
    /**
     * Une chaîne de caractères contenant uniquement
     * une virgule, utilisée comme séparateur.
     */
    String COMMA = ",";
    /**
     * Une chaîne contenant uniquement un point-virgule,
     * utilisé comme séparateur.
     */
    String SEMI_COLON = ";";
    /**
     * Une chaîne contenant uniquement le caractère "deux points" (:),
     * utilisé comme séparateur.
     */
    String COLON = ":";
    
    // MARK:- Méthodes statiques de découpage de String
    
    /**
     * Découpe la chaîne de caractères <code>toSplit</code> en plusieurs sous-chaînes,
     * séparés dans la chaîne initiale par le séparateur <code>separator</code>.
     * 
     * @param toSplit
     *          la chaîne contenant les sous-chaînes à séparer
     * @param separator
     *          le séparateur utilisé dans la chaîne à séparer
     * @return
     *          un tableau de <code>{@link String}</code> contenant
     *          toutes les sous-chaînes de <code>toSplit</code>,
     *          séparées par <code>separator</code>
     */
    static String[] split(String toSplit, String separator) {
        return toSplit.split(Pattern.quote(separator), -1);
    }
    
    /**
     * Découpe la chaîne de caractères <code>toSplit</code> en plusieurs sous-chaînes,
     * séparés dans la chaîne initiale par un point-virgule.
     *
     * @param toSplit
     *          la chaîne contenant les sous-chaînes à séparer
     * @return
     *          un tableau de <code>{@link String}</code> contenant
     *          toutes les sous-chaînes de <code>toSplit</code>,
     *          séparées par un point-virgule
     */
    static String[] split(String toSplit) {
        return split(toSplit, SEMI_COLON);
    }
    
    // MARK:- Méthodes statiques de construction de serdes
    
    /**
     * Méthode générique prenant en arguments une fonction de sérialisation
     * ainsi qu'une fonction de désérialisation,
     * et retournant le serde correspondant.
     * 
     * @param serializer
     *          la fonction à utiliser pour sérialiser les objets de type donné
     * @param deserializer
     *          la fonction à utiliser pour désérialiser les objets de type donné
     * @param <T>
     *          le type d'objets que le serde retourné sera en mesure de sérialiser/désérialiser
     * @return
     *          un serde capable de sérialiser/désérialiser les objets
     *          de type donné à l'aide des fonctions données.
     */
    static <T> Serde<T> of(Function<T, String> serializer, Function<String, T> deserializer) {
        return new Serde<>() {
            @Override
            public String serialize(T raw) {
                return raw != null
                        ? serializer.apply(raw)
                        : "";
            }

            @Override
            public T deserialize(String serialized) {
                return deserializer.apply(serialized);
            }
        };
    }
    
    /**
     * Méthode générique prenant en argument la liste de toutes
     * les valeurs d'un ensemble de valeurs énuméré
     * et retournant le serde correspondant.
     * 
     * @param elements
     *          la liste de toutes les valeurs possibles
     *          de l'ensemble énuméré en question
     * @param <T>
     *          le type des objets composant l'ensemble énuméré
     * @return
     *          un serde capable de sérialiser/désérialiser les objets
     *          qui composent la liste passée en argument
     */
    static <T> Serde<T> oneOf(List<T> elements) {
        return Serde.of(
                raw -> String.valueOf(elements.indexOf(raw)),
                serialized -> serialized.isEmpty()
                        ? null
                        : elements.get(Integer.parseInt(serialized))
        );
    }
    
    /**
     * Méthode générique prenant en argument un serde et un caractère de séparation
     * et retournant un serde capable de sérialiser/désérialiser des listes de valeurs,
     * sérialisées et désérialisées par le serde donné.
     * 
     * @param serde
     *          le serde à utiliser pour sérialiser/désérialiser
     *          les valeurs de la liste à traiter
     * @param separator
     *          le séparateur à utiliser pour séparer les différentes
     *          chaînes de caractères correspondant aux sérialisations
     *          des valeurs de la liste à traiter
     * @param <T>
     *          le type des objets contenus dans les listes à sérialiser/désérialiser
     * @return
     *          un serde capable de sérialiser des listes d'objets du type
     *          que le serde passé en argument est en mesure de sérialiser/désérialiser
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return Serde.of(
                raw -> raw.stream()
                        .map(serde::serialize)
                        .collect(Collectors.joining(separator)),
        
                serialized -> serialized.isEmpty()
                        ? List.of()
                        : Arrays.stream(split(serialized, separator))
                        .map(serde::deserialize)
                        .collect(Collectors.toUnmodifiableList())
        );
    }
    
    /**
     * Méthode générique prenant en argument un serde
     * et retournant un serde capable de sérialiser/désérialiser des listes de valeurs
     * en utilisant la virgule comme caractère de séparation,  
     * et sérialisées et désérialisées par le serde donné.
     *
     * @param serde
     *          le serde à utiliser pour sérialiser/désérialiser
     *          les valeurs de la liste à traiter
     * @param <T>
     *          le type des valeurs contenues dans les listes à sérialiser/désérialiser
     * @return
     *          un serde capable de sérialiser des listes d'objets du type
     *          que le serde passé en argument est en mesure de sérialiser/désérialiser
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde) {
        return listOf(serde, COMMA);
    }
    
    /**
     * Méthode générique prenant en argument un serde
     * et retournant un serde capable de sérialiser/désérialiser des multiensembles triés
     * de valeurs, sérialisées et désérialisées par le serde donné.
     * 
     * @param serde
     *          le serde à utiliser pour sérialiser/désérialiser
     *          les valeurs du multiensemble trié à traiter
     * @param separator
     *          le séparateur à utiliser pour séparer les différentes
     *          chaînes de caractères correspondant aux sérialisations
     *          des valeurs du multiensemble trié à traiter
     * @param <T>
     *          le type des valeurs contenues dans les multiensembles triés
     *          à sérialiser/désérialiser
     * @return
     *          un serde capable de sérialiser des listes d'objets du type
     *          que le serde passé en argument est en mesure de sérialiser/désérialiser
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        Serde<List<T>> listSerde = listOf(serde, separator);
        return Serde.of(
                bag -> listSerde.serialize(bag.toList()),
                serialized -> SortedBag.of(listSerde.deserialize(serialized))
        );
    }
    
    /**
     * Méthode générique prenant en argument un serde
     * et retournant un serde capable de sérialiser/désérialiser des multiensembles triés
     * de valeurs en utilisant la virgule comme caractère de séparation,
     * sérialisées et désérialisées par le serde donné.
     *
     * @param serde
     *          le serde à utiliser pour sérialiser/désérialiser
     *          les valeurs du multiensemble trié à traiter
     * @param <T>
     *          le type des valeurs contenues dans les multiensembles triés
     *          à sérialiser/désérialiser
     * @return
     *          un serde capable de sérialiser des listes d'objets du type
     *          que le serde passé en argument est en mesure de sérialiser/désérialiser
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde) {
        return bagOf(serde, COMMA);
    }
    
    // MARK:- Méthodes de Serde
    
    /**
     * Sérialise l'objet passé en argument et retourne
     * la chaîne de caractères correspondante.
     * 
     * @param raw
     *          l'objet à sérialiser
     * @return
     *          la chaîne de caractères correspondant à la sérialisation
     *          de l'objet passé en argument
     */
    String serialize(T raw);
    
    /**
     * Retourne l'objet correspondant à sa forme textuelle
     * sérialisée <code>serialized</code> passée en argument.
     * 
     * @param serialized
     *          la forme sérialisée de l'objet à désérialiser
     * @return
     *          l'objet désérialisé
     */
    T deserialize(String serialized);

}
