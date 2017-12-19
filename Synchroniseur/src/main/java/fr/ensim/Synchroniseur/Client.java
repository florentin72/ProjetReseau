package fr.ensim.Synchroniseur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Client extends Gestionnaire {
	
	
	Socket socket;
	BufferedReader inputStream, inputUser;
	PrintStream outputStream;
	String line;
	File metadata;
	Options options;
	String repertoireDist;
	String repertoireLocal;
	
	 Client (){
		 
		 options = parseOptions();
		 repertoireDist = "args[3]";
		 repertoireLocal = "args[2]";
		 
		 try {
		
		 	socket = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
			inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputStream = new PrintStream(socket.getOutputStream());
			inputUser = new BufferedReader(new InputStreamReader(System.in));
		} catch (UnknownHostException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
		
		
	}
	
	

	public static void main(String[] args) {
	
		
		
		Client c = new Client ();
		
		
			
			
			
			
			//do {
				
				/*
				try {
					c.line = c.inputUser.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				/*c.outputStream.println(c.line);
				try {
					System.out.println(c.inputStream.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				*/
				c.envoiMetadata("testClient");
			//}while(!c.line.equals("STOP"));
			
			
	
			
			c.closeConnection();
	}
	
	
	private void envoiMetadata (String path){
		
		 	try {
				transfert(
				        new FileInputStream(path),
				        socket.getOutputStream(),
				        true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		
		
		
		
		
	}
	private void closeConnection (){
		
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private Options parseOptions (){
		return options;
		
		
	}
	
}
