package net.oikmo.engine.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.oikmo.main.Main;

public class SaveSystem {
	
	public static void save(String name, SaveData data) {
	    try {
	    	File directory = new File(Main.getDir()+ "/saves/");
	    	if(!directory.exists()) {
	    		directory.mkdir();
	    	}
	    	
	    	File save = new File(directory + "/" + name + ".dat");
	    	save.delete();
	    	//save.createNewFile();
	    	FileOutputStream fos = new FileOutputStream(save);
	    	GZIPOutputStream gzip = new GZIPOutputStream(fos);
		    ObjectOutputStream oos = new ObjectOutputStream(gzip);
			oos.writeObject(data);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	public static SaveData load(String name) {
		File directory = new File(Main.getDir()+ "/saves/");
		
		if(directory.exists()) {
			File save = new File(directory + "/" + name + ".dat");
			if(save.exists()) {
				ObjectInputStream obj;
				try {
					FileInputStream fis = new FileInputStream(save);
					GZIPInputStream gzip = new GZIPInputStream(fis);
					obj = new ObjectInputStream(gzip);
					SaveData data = (SaveData) obj.readObject();
					obj.close();
					return data;
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				return null;
			}
			
		} else {
			directory.mkdir();
			return null;
		}
		return null;
	}
	
	
	public static void saveServers(ServerListData data) {
	    try {
	    	File directory = Main.getDir();
	    	
	    	File save = new File(directory + "/servers.dat");
	    	save.delete();
	    	//save.createNewFile();
	    	FileOutputStream fos = new FileOutputStream(save);
	    	GZIPOutputStream gzip = new GZIPOutputStream(fos);
		    ObjectOutputStream oos = new ObjectOutputStream(gzip);
			oos.writeObject(data);
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
	}
	
	public static ServerListData loadServers() {
		File directory = Main.getDir();
    	
    	File save = new File(directory + "/servers.dat");
		
		if(directory.exists()) {
			if(save.exists()) {
				ObjectInputStream obj;
				try {
					FileInputStream fis = new FileInputStream(save);
					GZIPInputStream gzip = new GZIPInputStream(fis);
					obj = new ObjectInputStream(gzip);
					ServerListData data = (ServerListData) obj.readObject();
					obj.close();
					return data;
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				return null;
			}
			
		} else {
			directory.mkdir();
			return null;
		}
		return null;
	}
	
}
