package ch.epfl.tchu;

/**
 * Classe utilitaire servant à vérifier les préconditions
 * de fonctions.
 * 
 * @author Louis Hervé (312937)
 */
public final class Preconditions {
    
    private Preconditions() {}
    
    /**
     * Vérifie que l'argument passé en paramètre est vrai,
     * retourne une {@link IllegalArgumentException} sinon.
     * 
     * @param shouldBeTrue
     *          booléen vérifié par la fonction (doit être vrai)
     * @throws IllegalArgumentException
     *          si le booléen shouldBeTrue est faux
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (! shouldBeTrue)
            throw new IllegalArgumentException();
    }
    
}
