package fr.ensim.Synchroniseur;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Gestionnaire {

	
	
	   public static void transfert(InputStream in, OutputStream out, boolean closeOnExit) throws IOException
	    {
	        byte buf[] = new byte[4096];
	        
	        int n;
	        while((n=in.read(buf)) !=-1)
	            out.write(buf,0,n);
	        
	        /*if (closeOnExit)
	        {
	            in.close();
	            out.close();
	        }*/
	    }
	
	
	
}
