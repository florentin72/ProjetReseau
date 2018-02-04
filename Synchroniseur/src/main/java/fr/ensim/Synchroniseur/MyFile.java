package fr.ensim.Synchroniseur;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class MyFile implements Serializable {
	private static final long serialVersionUID = -4130870535766559481L;
	
	File file;
	boolean isDirectory, isFile, exists;
	long length, lastModified;
	ArrayList<MyFile> child;
	
	public MyFile(String chemin) {
		file = new File(chemin);
		isDirectory = file.isDirectory(); isFile = file.isFile(); exists = file.exists();
		length = file.length(); lastModified = file.lastModified();
		child = new ArrayList<MyFile>();
		if(file.listFiles() != null)
			for(File enfant : file.listFiles())
				child.add(new MyFile(enfant.getAbsolutePath()));
	}
	
	public String getName() {
		return file.getName();
	}
	public String getPath() {
		return file.getPath();
	}
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	
	public ArrayList<MyFile> listFiles() {
		return child;
	}
	public boolean isDirectory() {
		return isDirectory;
	}
	public boolean isFile() {
		return isFile;
	}
	public boolean exists() {
		return exists;
	}
	
	public long length() {
		return length;
	}
	public long lastModified() {
		return lastModified;
	}
	
	public boolean delete() {
		return file.delete();
	}
	public void mkdir() {
		file.mkdir();
	}
	public void mkdirs() {
		file.mkdirs();
	}
}
