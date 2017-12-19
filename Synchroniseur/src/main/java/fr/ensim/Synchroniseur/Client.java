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
import java.security.cert.PKIXRevocationChecker.Option;
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
	String serverName;
	int port;
	
	 Client (String [ ] args) {
		 options = parseOptions(args);
		 System.out.println(options);
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
			} catch(IOException e) {
				e.printStackTrace();
			}
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
	
	
	private void closeConnection () {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private Options parseOptions (String[] args) {
		int nbArgs = args.length;
		options = Options.defaut;
		if(nbArgs < 3) {
			System.out.println("Il manque des arguments !");
		} else if(nbArgs >= 3) {

			serverName = args[0].substring(0, args[0].indexOf(':'));
			port = Integer.parseInt(args[0].substring(args[0].indexOf(':')+1, args[0].length()));
			repertoireLocal = args[1];
			repertoireDist = args[2];
			
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
