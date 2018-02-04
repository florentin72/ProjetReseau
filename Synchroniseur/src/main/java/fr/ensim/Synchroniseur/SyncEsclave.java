package fr.ensim.Synchroniseur;
import java.io.IOException;

public class SyncEsclave extends Client {
	
	SyncEsclave(String[] args) {
		super(args);
	}
	//java SyncEsclave adresse:port source cible
	//java SyncEsclave localhost:8080 C:\Users\Quent\Documents\TestReseau\Serveur C:\Users\Quent\Documents\TestReseau\Client -w
	public static void main(String[] args) {
		SyncEsclave c = new SyncEsclave(args);
		c.envoiIsSlave();
		c.envoieRepertoires(c.getRepertoireSource(), c.getRepertoireCible());
		
		if(c.receptOK()) {
			c.setMetaDataCible(new MyFile(c.getRepertoireCible()));
			c.getMetaDataCible().mkdirs();
			System.out.println("Contenu du repertoire cible : "); c.afficherFile(c.getMetaDataCible(), 0);
			c.receptMetaDataSource();
			switch (c.getOptions()) {//On privil�gie toujours l'option qui fait le moins de perte de donn�es au niveau de la cible
			case w: /*Watchdog : overwrite seulement par un fichier plus r�cent*/
			case we:
			case wes:
				System.out.println("\n\nD�but Watchdog !");
				c.watchdog(c.getMetaDataSource());
				System.out.println("Fin Watchdog !");
				break;
			case e: /*Ecrasement : overwrite*/
			case es:
				System.out.println("\n\nD�but Ecrasement !");
				c.ecrasement(c.getMetaDataSource());
				System.out.println("Fin Ecrasement !");
				break;
			case s: /*Suppression : vider le r�pertoire cible*/
				System.out.println("\n\nD�but Suppression !");
				c.setMetaDataCible(c.suppr(c.getMetaDataCible()));
				System.out.println("Contenu apr�s suppr:");
				c.afficherFile(c.getMetaDataCible(), 0);
				c.suppression(c.getMetaDataSource());
				System.out.println("Fin Suppression !");
				break;
			case ws: /*Watchdog et suppression : on remplace que les fichiers les plus vieux, on supprime ceux qui n'existent pas sur le serveur*/
			case defaut:
			default:
				System.err.println("Pas de copie : option incorrecte.");
				break;
			}
			
			try {
				c.setStopFile(new MyFile("STOP"));
				c.getOOS().writeObject(c.getStopFile());
			} catch (IOException e) {
				System.err.println(e);
			}
			
		} else {
			System.out.println("Le repertoire source n'existe pas. R�essayez.");
		}
		
		c.closeConnection();
		return;
	}
	
	private void envoiIsSlave() {
		try {
			oos.writeBoolean(false);
		} catch (IOException e) {
			System.err.println(e);
		}
		return;
	}
}