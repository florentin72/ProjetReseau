package fr.ensim.Synchroniseur;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Gestionnaire {
	int port;
	String serverName;
	
	Client(String [] args) {
		options = parseOptions(args);
		metaDataSource = new MyFile(repertoireSource);
		try {
			socket = new Socket(serverName, port);
			inputStream = socket.getInputStream();
			outputStream = socket.getOutputStream();
			oos = new ObjectOutputStream(outputStream);
			ois = new ObjectInputStream(inputStream);
		} catch (UnknownHostException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
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
	
	protected Options parseOptions (String[] args) {
		int nbArgs = args.length;
		options = Options.defaut;
		if(nbArgs < 3) {
			System.out.println("Il manque des arguments !");
		} else if(nbArgs >= 3) {

			serverName = args[0].substring(0, args[0].indexOf(':'));
			port = Integer.parseInt(args[0].substring(args[0].indexOf(':')+1, args[0].length()));
			repertoireSource = args[1];
			repertoireCible = args[2];
			
			for(int i = 3; i < nbArgs; i++) {
				if(args[i].indexOf('-') == 0) { //Si le paramètre à une option
					switch(args[i]) {//	defaut, w, e, s, we, ws, es, wes;
					case "-e" :
						options = options.ajouter(Options.e);
						break;
					case "-w" :
						options = options.ajouter(Options.w);
						break;
					case "-s":
						options = options.ajouter(Options.s);
						break;
					case "-we":
						options = options.ajouter(Options.we);
						break;
					case "-ws":
						options = options.ajouter(Options.ws);
						break;
					case "-es":
						options = options.ajouter(Options.es);
						break;
					case "-wes":
						options = options.ajouter(Options.wes);
						break;
					default :
						options = options.ajouter(Options.defaut);
						break;
					}
				}// Si il n'y a pas le marqueur d'option, on en tient pas compte
			}
		}
		return options;
	}
}