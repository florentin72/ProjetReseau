package fr.ensim.Synchroniseur;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Gestionnaire {

	boolean isMaster;

	InputStream inputStream;
	OutputStream outputStream;
	ObjectInputStream ois;
	ObjectOutputStream oos;

	Socket socket;
	
	MyFile metaDataSource, metaDataCible, stopFile, metaFileToSend;

	String repertoireSource, repertoireCible;
	Options options;

	public void setStopFile(MyFile stopFile) {
		this.stopFile = stopFile;
	}
	public MyFile getStopFile() {
		return stopFile;
	}
	public ObjectOutputStream getOOS() {
		return oos;
	}	
	public Options getOptions() {
		return options;
	}
	public MyFile getMetaDataSource() {
		return metaDataSource;
	}
	public String getRepertoireSource() {
		return repertoireSource;
	}
	public String getRepertoireCible() {
		return repertoireCible;
	}
	public void setMetaDataCible(MyFile metaDataCible) {
		this.metaDataCible = metaDataCible;
	}
	public MyFile getMetaDataCible() {
		return metaDataCible;
	}

	
	
	public void transfert(InputStream in, OutputStream out, long fileSize) throws IOException
	{
		byte buf[] = new byte[1024];
		int n;
		System.out.println("Taille du fichier : " + fileSize);
		while(fileSize > 0 && (n = in.read(buf)) != -1) {
			out.write(buf, 0, n);
			out.flush();
			fileSize -= n;
		}
		return;
	}
	
	public void afficherFile(final MyFile folder, int arbre) {
		for(final MyFile fileCourant : folder.listFiles()) {
			printTab(arbre);
			if(fileCourant.isDirectory()) {
				if(arbre > 0)
					System.out.println(fileCourant.getName());
				else
					System.out.println(fileCourant.getPath());
				afficherFile(fileCourant, arbre+1);
			} else {
				if(arbre > 0)
					System.out.println(fileCourant.getName());
				else
					System.out.println(fileCourant.getPath());
			}
		}
		return;
	}
	
	private void printTab(int arbre) {
		for(int i = 0; i < arbre; i++) {
			System.out.print("\t");
		}
		return;
	}
	
	protected boolean envoiOK(MyFile metaData) {
		try {
			oos.writeObject(metaData.exists());
		} catch (IOException e) {
			System.err.println(e);
		}
		return metaDataSource.exists();
	}
	protected void envoieRepertoires(String repertoireSrc, String repertoireDest) {
		try {
			System.out.println("Envoi des repertoires ...");
			oos.writeObject(repertoireSrc);
			System.out.println("Repertoire source envoye !");
			oos.writeObject(repertoireDest);
			System.out.println("Repertoire dest envoye !");
		} catch (IOException e) {
			System.err.println(e);
		}
		return;
	}
	protected void envoiMetaDataSource(MyFile metaDataSource) {
		try {
			System.out.println("Envoi des metaDataSource ...");
			oos.writeObject(metaDataSource);
			System.out.println("MetaData envoyees !");
		} catch (IOException e) {
			System.err.println(e);
		}
		return;
	}
	protected void envoiOptions() {
		try {
			System.out.println("Envoi des options ...");
			oos.writeObject(options);
			System.out.println("Options envoyees !");
		} catch (IOException e) {
			System.err.println(e);
		}
		return;
	}
	
	protected void envoiFichiers() {
		receptMetaFileToSend();
		while(metaFileToSend.exists()) { //Tant qu'on reçoit des pathName à envoyer
			System.out.println("Fichier à envoyer : " + metaFileToSend.getAbsolutePath());
			//envoyer le fichier correspondant
			try {
				FileInputStream FileIS = new FileInputStream(metaFileToSend.getAbsolutePath());
				transfert(FileIS, outputStream, metaFileToSend.length());
				FileIS.close();
				System.out.println("Fichier envoyé !");
			} catch (IOException e) {
				System.err.println(e);
			}
			receptMetaFileToSend();
		}
		System.out.println("Fin de la transmition.");
	}
	protected void receptMetaFileToSend() {
		try {
			metaFileToSend = (MyFile) ois.readObject();
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
		return;
	}
	
	protected void closeConnection () {
		try {
			oos.close();
			ois.close();
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException e) {
			System.err.println(e);
		}
		return;
	}
		
	
	
	protected MyFile suppr(MyFile src) {
		for(final MyFile fileCourant : src.listFiles()) {
			if(fileCourant.isDirectory()) {
				suppr(fileCourant);
			}
			if(!fileCourant.delete())
				System.err.println("Suppr failed! (" + fileCourant.getName() + ")");
		}
		return new MyFile(src.getAbsolutePath());
	}
	
	protected void askAndCreateFile(MyFile fileCourant, MyFile metaFile) {
		//envoyer metadata du fichier dont on a besoin (fileCourant)
		envoiMetaFileToRecept(fileCourant);
		//recevoir le fichier, l'enregistrer à metaFile.getAbsolutePath()
		try {
			FileOutputStream fileOS = new FileOutputStream(metaFile.getAbsolutePath());
			transfert(inputStream, fileOS, fileCourant.length());
			System.out.println("Fichier recu !");
			fileOS.close();
		} catch (IOException e) {
			System.err.println(e);
		}
		return;
	}

	protected String diffFile(final MyFile abstractFile, String repertoire) {
		String strAbstractFile, strRepertoire;
		strAbstractFile = toParse(abstractFile.getAbsolutePath());
		strRepertoire = toParse(repertoire);
		String[] diff = strAbstractFile.split(strRepertoire);
		//System.out.println(abstractFile.getAbsolutePath() + " - " + repertoire + " = " + diff[1].substring(1));
		return diff[1].substring(1);
	}
	protected String toParse(String str) {
		String toReturn = new String();
		//System.out.print("ToParse :\n'" + str + "' -> '");
		toReturn = str.replace('\\', '/');
		//System.out.println(toReturn + "'");
		return toReturn;
	}
	

	protected boolean receptOK() {
		try {
			return (boolean) ois.readObject();
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
		return false;
	}
	protected void receptRepertoire() {
		try {
			System.out.println("Envoi des repertoires ...");
			repertoireSource = (String) ois.readObject();
			System.out.println("Repertoire source recu : " + repertoireSource);
			repertoireCible = (String) ois.readObject();
			System.out.println("Repertoire cible recu : " + repertoireCible);
			if(isMaster)
				metaDataCible = new MyFile(repertoireCible);
			else
				metaDataSource = new MyFile(repertoireSource);
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
		return;
	}
	protected void receptOption() {
		try {
			options = (Options) ois.readObject();
			System.out.println("Option recu : '" + options + "'");
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
		return;		
	}
	protected void receptMetaDataSource() {
		try {
			metaDataSource = (MyFile) ois.readObject();
			System.out.println("metaDataSource recues (contenu du repertoire source):"); afficherFile(metaDataSource, 0);
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
		return;
	}

	protected void envoiMetaFileToRecept(MyFile metaFile) {
		try {
			oos.writeObject(metaFile);
		} catch (IOException e) {
			System.err.println(e);
		}
		return;
	}
	
	
	protected void watchdog(MyFile src) {
		for(final MyFile fileCourant : src.listFiles()) {
			System.out.println("\nLecture de " + src.getName());
			if(fileCourant.isDirectory()) {
				System.out.println(fileCourant.getName() + " est un dossier.");
				String abstractPathFolder = diffFile(fileCourant, repertoireSource);
				MyFile metaFolder = new MyFile(repertoireCible + '\\' + abstractPathFolder);
				if(!metaFolder.exists()) {//Si le dossier meta n'existe pas
					System.out.println("Le dossier n'existe pas. Création.");
					metaFolder.mkdir();
				} else {
					System.out.println("Le dossier existe.");
				}
				watchdog(fileCourant);
			} else { //fileCourant est un fichier
				System.out.println(fileCourant.getName() + " est un fichier.");
				String abstractPathFile = diffFile(fileCourant, repertoireSource);
				MyFile metaFile = new MyFile(repertoireCible + '\\' + abstractPathFile);
				if(!metaFile.exists()) {//Si le fichier meta n'existe pas
					System.out.println("Le fichier n'existe pas. Ask and Create.");
					askAndCreateFile(fileCourant, metaFile);
				} else if(fileCourant.lastModified() > metaFile.lastModified()) {//Si le fichier existe mais fichierSrc est plus récent
					System.out.println("Le fichier existe mais est moins recent. Delete. Ask and Create.");
					metaFile.delete();
					askAndCreateFile(fileCourant, metaFile);
				} else {
					System.out.println("Le fichier existe et est plus recent.");
				}
			}
		}
		return;
	}
	protected void ecrasement(MyFile src) {
		for(final MyFile fileCourant : src.listFiles()) {
			System.out.println("\nLecture de " + src.getName());
			if(fileCourant.isDirectory()) {
				System.out.println(fileCourant.getName() + " est un dossier.");
				String abstractPathFolder = diffFile(fileCourant, repertoireSource);
				MyFile metaFolder = new MyFile(repertoireCible + '\\' + abstractPathFolder);
				if(!metaFolder.exists()) {//Si le dossier meta n'existe pas
					System.out.println("Le dossier n'existe pas. Création.");
					metaFolder.mkdir();
				} else {
					System.out.println("Le dossier existe.");
				}
				ecrasement(fileCourant);
			} else { //fileCourant est un fichier
				System.out.println(fileCourant.getName() + " est un fichier.");
				String abstractPathFile = diffFile(fileCourant, repertoireSource);
				MyFile metaFile = new MyFile(repertoireCible + '\\' + abstractPathFile);
				if(!metaFile.exists()) {//Si le fichier meta n'existe pas
					System.out.println("Le fichier n'existe pas. Ask and Create.");
					askAndCreateFile(fileCourant, metaFile);
				} else {//Si le fichier existe
					System.out.println("Le fichier existe. Delete. Ask and Create.");
					metaFile.delete();
					askAndCreateFile(fileCourant, metaFile);
				}
			}
		}
		return;
	}
	protected void suppression(MyFile src) {
		for(final MyFile fileCourant : src.listFiles()) {
			System.out.println("\nLecture de " + src.getName());
			if(fileCourant.isDirectory()) {
				System.out.println(fileCourant.getName() + " est un dossier.");
				String abstractPathFolder = diffFile(fileCourant, repertoireSource);
				MyFile metaFolder = new MyFile(repertoireCible + '\\' + abstractPathFolder);
				if(!metaFolder.exists()) {//Si le dossier meta n'existe pas
					System.out.println("Le dossier n'existe pas. Création.");
					metaFolder.mkdir();
				} else {
					System.err.println("Le dossier existe.");
				}
				suppression(fileCourant);
			} else { //fileCourant est un fichier
				System.out.println(fileCourant.getName() + " est un fichier.");
				String abstractPathFile = diffFile(fileCourant, repertoireSource);
				MyFile metaFile = new MyFile(repertoireCible + '\\' + abstractPathFile);
				if(!metaFile.exists()) {//Si le fichier meta n'existe pas
					System.out.println("Le fichier n'existe pas. Ask and Create.");
					askAndCreateFile(fileCourant, metaFile);
				} else {//Si le fichier existe
					System.err.println("Le fichier existe.");
				}
			}
		}
		return;		
	}
}