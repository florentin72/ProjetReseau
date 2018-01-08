package fr.ensim.Synchroniseur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import junit.framework.Test;

public class SyncServeur extends Gestionnaire implements Runnable {
	
	private static int listenPort = 8080;
	private Socket clientSocket;
	static private int compteur = 0;
	File metadata;
	Options optionClient;
	File metadataClient = null;
	
	synchronized public int getCompteur() {
		return compteur;
	}
	synchronized public void incrCompteur() {
		compteur++;
		return;
	}
	synchronized public void decrCompteur() {
		compteur--;
		return;
	}
	
	SyncServeur(Socket clientSocket) {
		this.clientSocket = clientSocket;
		incrCompteur();
		System.out.println(getCompteur() + " clients connectés.");
	}
	
	public void run() {
		doService(clientSocket);
	}
	
	public void doService(Socket clientSocket) {
		try {
			/*BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintStream out = new PrintStream(clientSocket.getOutputStream());
			
			out.println("Envoyez 'STOP' pour arrêter la connexion.");
			String line = "";
			while(!line.equals("STOP")) {
				
				line = in.readLine();
				out.println(line);
				*/
				//receptionMetadata();
				receptMetaObject();
				
				
			//}
			clientSocket.close();
			decrCompteur();
			System.out.println(getCompteur() + " clients connectés.");
		} catch(IOException e) {
			System.err.println(e);
		} 
		
	}
	
	public void receptMetaObject() {
		//Ajouter recepetion option
		try {
			InputStream is = clientSocket.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			 metadata = (File) ois.readObject();
			if (metadata!=null) { //Si meta est pas null, on a bien un truc :D
			
				listFilesForFolder(metadata, 0);
				//TODO appeler fonction pour traiter les metadata en fonction de l'option
				//		créer les dossier dont on a besoin -> return metadata qu'on veut récuperer
				//TODO envoyer metadata qu'on veut récuperer
				//TODO reception fichier 1 par 1 (avec transfert)
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void receptionMetadata () {
		 try {
		
			transfert(
			            clientSocket.getInputStream(),
			            new FileOutputStream(metadataClient),
			            true);
			
			
	//		File file = new File ("test.txt");
			
			//File directory = new File ("testClient");
			//listFilesForFolder(directory, 0);
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * 
	 * 
	 * @param dest
	 * repertoire destination
	 */
	public ArrayList <File> traitementMetadata(File dest) {
		
		ArrayList<File> demandeFile = new ArrayList<File>(); 
		if (dest == null ) {
			demandeFile.add(metadata);
			return demandeFile;
			
		}
		else {
		switch (optionClient) {
		case es:
		
			break;
			
		case e:
			 for (final File fileEntry : metadata.listFiles() ) {
				 
			        if (fileEntry.isDirectory()) {
			        	for (File f : dest.listFiles()) {
			        		
			        		if (fileEntry.exists()) {
			        			
			        				
			        		}
			        		
			        	}
			           
			        	
			        	
			        	
			            listFilesForFolder(fileEntry,0);
			        } else {
			        	
			        	
			            System.out.println(fileEntry.getName());
			            
			            
			            
			            
			        }
			    }
			
			
			
			
			break;
		case w:
			
			break;
			
		case ws:
			
			break;
		case wes:
			
			break;
		case s:
			
			break;
			
		case we:
			
			break;
			
		default:
			break;
		}
		return demandeFile;
		}
		
		
		
		
		
		
		
		
	}
	
	public void listFilesForFolder(final File folder, int arbre) {
	    for (final File fileEntry : folder.listFiles()) {
        	printTab(arbre);
	        if (fileEntry.isDirectory()) {
	        	
	        	/* on creer un nouveau fichier en rajoutant la destinationau chemin*/
	        	File fileSend = new File ("Serveur/"+fileEntry.getPath());
	        	System.out.println(fileSend.getAbsolutePath());
	        	
	        	/* on creer le dossier au bonne endroit*/
	        	fileSend.mkdirs();
	            System.out.println(fileEntry.getName());
	            listFilesForFolder(fileEntry,arbre+1);
	        } else {
	            System.out.println(fileEntry.getName());
	        }
	    }
	}
	
	private void printTab(int arbre) {
		for(int i = 0; i < arbre; i++) {
			System.out.print("\t");
		}
		return;
	}
	public static void main(String[] args) {
		ServerSocket listenSocket;
		try {
			listenSocket = new ServerSocket(listenPort);
			while(true) {
				Socket clientSocket = listenSocket.accept();
				Thread serviceThread = new Thread(new SyncServeur(clientSocket));
				//System.err.println("Connexion from :" + clientSocket.getInetAddress());
				serviceThread.start();
			}
		} catch(Exception e) {
			System.err.println(e);
		}
	}
}