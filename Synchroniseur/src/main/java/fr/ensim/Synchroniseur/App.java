package fr.ensim.Synchroniseur;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}


/***

un mode écrasement où un fichier existant déjà dans le répertoire destination est écrasé par le fichier du répertoire source.
un mode suppression où un fichier existant dans le répertoire destination mais pas dans le répertoire source est supprimé dans le répertoire destination.
un mode watchdog où un fichier existant déjà dans le répertoire destination est écrasé uniquement par une version plus récente du fichier du répertoire source.


*/