package fr.ensim.Synchroniseur;
import java.io.IOException;

public class SyncMaitre extends Client {
//Le maitre push sur le serveur
	
	SyncMaitre(String [] args) {
		super(args);
	}
	//java SyncMaitre adresse:port source cible
	//java SyncMaitre localhost:8080 C:\Users\Quent\Documents\TestReseau\Client C:\Users\Quent\Documents\TestReseau\Serveur -w
	public static void main(String[] args) {
		SyncMaitre c = new SyncMaitre(args);
		c.envoiIsMaster();
		if(c.envoiOK(c.getMetaDataSource())) { //Si le fichier source � push existe bien
			c.envoiOptions(); //Envoyer options
			c.envoieRepertoires(c.getRepertoireSource(), c.getRepertoireCible()); //Envoyer r�pertoires
			c.envoiMetaDataSource(c.getMetaDataSource()); //Envoyer metaDataSource du client
			c.envoiFichiers(); //Envoie les fichiers demand�s par le serveur
		} else {
			System.out.println("Le repertoire source n'existe pas. R�essayez.");
		}
		c.closeConnection();
		return;
	}
	
	private void envoiIsMaster() {
		try {
			oos.writeBoolean(true);
		} catch (IOException e) {
			System.err.println(e);
		}
		return;
	}
}

