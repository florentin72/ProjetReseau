package fr.ensim.Synchroniseur;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Serveur extends Gestionnaire {
	static Semaphore mutex = new Semaphore(10);
	
	static int listenPort = 8080;
	
	static private int compteur = 0;
	synchronized protected int getCompteur() {
		return compteur;
	}
	synchronized protected void incrCompteur() {
		compteur++;
		return;
	}
	synchronized protected void decrCompteur() {
		compteur--;
		return;
	}

	static private int compteurMaitre = 0;
	synchronized protected static int getCompteurMaitre() {
		return compteurMaitre;
	}
	synchronized protected static void incrCompteurMaitre() {
		compteurMaitre++;
	}
	synchronized protected static void decrCompteurMaitre() {
		compteurMaitre--;
	}
	
	Serveur(Socket clientSocket) {
		this.socket = clientSocket;
		try {
			inputStream = this.socket.getInputStream();
			outputStream = this.socket.getOutputStream();
			ois = new ObjectInputStream(inputStream);
			oos = new ObjectOutputStream(outputStream);
		} catch (IOException e) {
			System.err.println(e);
		}
		stopFile = new MyFile("STOP");
		incrCompteur();
		System.out.println(getCompteur() + " clients connect�s.");
	}
	
	protected void receptIsMaster() {
		try {
			isMaster = ois.readBoolean();
		} catch (IOException e) {
			System.err.println(e);
		}
		return;
	}
	
	synchronized protected void masterService() { //envoie fichier sur serveur
		System.out.println("Connexion Maitre recue.");
		if(receptOK()) {
			receptOption();
			receptRepertoire();
			metaDataCible.mkdirs();
			System.out.println("Contenu du repertoire cible : "); afficherFile(metaDataCible, 0);
			receptMetaDataSource();
			switch (options) { //On privil�gie toujours l'option qui fait le moins de perte de donn�es au niveau de la cible
			case w: /*Watchdog : overwrite seulement par un fichier plus r�cent*/
			case we:
			case ws:
			case wes:
			case defaut:
				System.out.println("\n\nD�but Watchdog !");
				watchdog(metaDataSource);
				System.out.println("Fin Watchdog !");
				break;
			case e: /*Ecrasement : overwrite*/
			case es:
				System.out.println("\n\nD�but Ecrasement !");
				ecrasement(metaDataSource);
				System.out.println("Fin Ecrasement !");
				break;
			case s: /*Suppression : vider le r�pertoire cible*/
				System.out.println("\n\nD�but Suppression !");
				metaDataCible = suppr(metaDataCible);
				suppression(metaDataSource);
				System.out.println("Fin Suppression !");
				break;
			default:
				System.err.println("Pas de copie : option incorrecte.");
				break;
			}
			
			try {
				oos.writeObject(stopFile);
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		closeConnection();
		decrCompteur();
		decrCompteurMaitre();
		System.out.println(getCompteur() + " clients connect�s.");
		return;
	}

	protected void slaveService() { //envoie fichier sur client
		System.out.println("Connexion Esclave recue.");
		receptRepertoire();
		if(envoiOK(metaDataSource)) {
			envoiMetaDataSource(metaDataSource);
			envoiFichiers();
		} else {//System.out.println("Le repertoire source n'existe pas. R�essayez.");
			System.out.println("Rien n'est envoy�, le repertoire demande est introuvable.");
		}
		closeConnection();
		decrCompteur();
		System.out.println(getCompteur() + " clients connect�s.");
		return;
	}
}
