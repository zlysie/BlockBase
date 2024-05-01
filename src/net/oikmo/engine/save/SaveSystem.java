package net.oikmo.engine.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.oikmo.network.server.MainServer;

public class SaveSystem {
	
	public static void saveWorld(String name, SaveData data) {
	    try {
	    	File directory = new File(MainServer.getDir()+ "/saves/");
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
	public static SaveData loadWorld(String name) {
		File directory = new File(MainServer.getDir()+ "/saves/");
		
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
	
	public static void saveWorldPosition(String name, WorldPositionData data) {
		try {
	    	File directory = new File(MainServer.getDir()+ "/saves/");
	    	if(!directory.exists()) {
	    		directory.mkdir();
	    	}
	    	
	    	File save = new File(directory + "/" + name + ".position");
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
	public static WorldPositionData loadWorldPosition(String name) {
		File directory = new File(MainServer.getDir()+ "/saves/");
		
		if(directory.exists()) {
			File save = new File(directory + "/" + name + ".position");
			if(save.exists()) {
				ObjectInputStream obj;
				try {
					FileInputStream fis = new FileInputStream(save);
					GZIPInputStream gzip = new GZIPInputStream(fis);
					obj = new ObjectInputStream(gzip);
					WorldPositionData data = (WorldPositionData) obj.readObject();
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

	public static void savePlayerPositions(PlayersPositionData data) {
		try {
	    	File directory = new File(MainServer.getDir()+ "/saves/");
	    	if(!directory.exists()) {
	    		directory.mkdir();
	    	}
	    	
	    	File save = new File(directory + "/players.position");
	    	save.delete();
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
	public static PlayersPositionData loadPlayerPositions() {
		File directory = new File(MainServer.getDir()+ "/saves/");
		
		if(directory.exists()) {
			File save = new File(directory + "/players.position");
			if(save.exists()) {
				ObjectInputStream obj;
				try {
					FileInputStream fis = new FileInputStream(save);
					GZIPInputStream gzip = new GZIPInputStream(fis);
					obj = new ObjectInputStream(gzip);
					PlayersPositionData data = (PlayersPositionData) obj.readObject();
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
