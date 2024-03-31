package net.oikmo.main.save;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.oikmo.main.Main;

public class SaveSystem {
	
	public static void save(String name, SaveData data) {
	    try {
	    	File directory = new File(Main.getDir()+ "/saves/");
	    	if(!directory.exists()) {
	    		directory.mkdir();
	    	}
	    	
	    	FileOutputStream fos = new FileOutputStream(directory + "/" + name + ".world");
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
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
			File save = new File(directory + "/" + name + ".world");
			if(save.exists()) {
				ObjectInputStream obj;
				try {
					FileInputStream fis = new FileInputStream(save);
					obj = new ObjectInputStream(fis);
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
	
}
