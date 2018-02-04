package fr.ensim.Synchroniseur;
import java.net.ServerSocket;
import java.net.Socket;

public class SyncServeur extends Serveur implements Runnable {

	SyncServeur(Socket clientSocket) {
		super(clientSocket);
	}
	
	public static void main(String[] args) {
		ServerSocket listenSocket;
		Thread serviceThread;
		try {
			listenSocket = new ServerSocket(listenPort);
			System.out.println("En attente d'une connexion client ...");
			while(true) {
				Socket clientSocket = listenSocket.accept();
				serviceThread = new Thread(new SyncServeur(clientSocket));
				serviceThread.start();
			}
		} catch(Exception e) {
			System.err.println(e);
		}
	
		return;
	}

	public void run() {
		receptIsMaster();
		if(isMaster) {
			try {
				mutex.acquire(10);
			} catch (InterruptedException e) {
				System.err.println(e);
			}
			masterService();
			mutex.release(10);
		}
		else {
			try {
				mutex.acquire();
			} catch (InterruptedException e) {
				System.err.println(e);
			}
			slaveService();
			mutex.release();
		}
		return;
	}
}
