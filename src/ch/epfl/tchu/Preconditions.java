package ch.epfl.tchu;

/**
 * Classe utilitaire servant à vérifier les arguments passés
 * à certaines fonctions.
 * 
 * @author Mallory Henriet (311258)
 * @author Louis Hervé (312937)
 */
public final class Preconditions {
    
    private Preconditions() {}
    
    /**
     * Vérifie que l'argument passé en paramètre est vrai,
     * lance une {@link IllegalArgumentException} sinon.
     * 
     * @param shouldBeTrue
     *          booléen vérifié par la fonction qui doit être vrai
     * @throws IllegalArgumentException
     *          si le booléen <code>shouldBeTrue</code> est faux
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (! shouldBeTrue)
            throw new IllegalArgumentException();
    }
    
}
