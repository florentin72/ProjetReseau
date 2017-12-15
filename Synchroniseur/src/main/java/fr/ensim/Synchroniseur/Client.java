package fr.ensim.Synchroniseur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client extends Gestionnaire {

	public static void main(String[] args) {
		Socket socket;
		BufferedReader inputStream, inputUser;
		PrintStream outputStream;
		String line;
		
		try {
			socket = new Socket(InetAddress.getByName("127.0.0.1"), 8080);
			inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputStream = new PrintStream(socket.getOutputStream());
			inputUser = new BufferedReader(new InputStreamReader(System.in));
			
			System.out.println(inputStream.readLine());
			do {
				line = inputUser.readLine();
				outputStream.println(line);
				System.out.println(inputStream.readLine());
			}while(!line.equals("STOP"));
			
			socket.close();
			
		} catch (UnknownHostException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
}
