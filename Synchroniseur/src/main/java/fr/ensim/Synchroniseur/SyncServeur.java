package fr.ensim.Synchroniseur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.Test;

public class SyncServeur extends Gestionnaire implements Runnable {
	
	private static int listenPort = 8080;
	private Socket clientSocket;
	static private int compteur = 0;
	
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
				receptionMetadata();
				
			
				
			//}
			clientSocket.close();
			decrCompteur();
			System.out.println(getCompteur() + " clients connectés.");
		} catch(IOException e) {
			System.err.println(e);
		} 
		
	}
	
	
	public void receptionMetadata () {
		
		
		
		 try {
			
			transfert(
			            clientSocket.getInputStream(),
			            new FileOutputStream("test"),
			            true);
			
			File file = new File ("test");
			System.out.println(file.lastModified());
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
	
		
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