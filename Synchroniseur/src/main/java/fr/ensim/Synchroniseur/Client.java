package fr.ensim.Synchroniseur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	static String repertoireLocal;
	String serverName;
	int port;
	
	 Client (String [ ] args) {
		 options = parseOptions(args);
		
		 try {
		 	socket = new Socket(serverName, port);
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
		Client c = new Client (args);
		
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
				c.envoimetadata(repertoireLocal);
				File repLoc = new File ( repertoireLocal);
				  for (final File fileEntry : repLoc.listFiles()) {
			        
				        if (fileEntry.isDirectory()) {
			
				        	
				        	
				        	
				       
				        } else {
				        	c.envoiFichier(fileEntry.getPath());
				            System.out.println(fileEntry.getName());
				        }
					
					
					
					
				}
				//c.envoiFichier(repertoireLocal);
				//File folder = new File (repertoireLocal);	
				//c.sendListFilesForFolder(folder);	
				c.closeConnection();
			//}while(!c.line.equals("STOP"));
			
			/*
			} catch(IOException e) {
				e.printStackTrace();
			}*/
}
	
	
	private void envoimetadata(String repertoireLocal) {
		try {
			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			File metadata = new File (repertoireLocal); //metadata = repertoire local + tout ses enfants
			oos.writeObject(metadata);
			oos.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
		
	}



	private void envoiFichier (String path){
		
		 	try {
				transfert(
				        new FileInputStream(path),
				        socket.getOutputStream(),
				        true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// 
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
	
	
	public void sendListFilesForFolder(final File folder) {
		if (folder.isDirectory()) {
		    for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		        	
		            System.out.println(fileEntry.getName());
		            sendListFilesForFolder(fileEntry);
		        } else {
		        	
		            System.out.println(fileEntry.getName());
		            this.envoiFichier(fileEntry.getAbsolutePath());
		        }
	    	}
	    } else {
	    	envoiFichier(folder.getAbsolutePath());
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
