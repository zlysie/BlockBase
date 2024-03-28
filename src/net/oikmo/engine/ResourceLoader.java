package net.oikmo.engine;

import java.util.HashMap;
import java.util.Map;

public class ResourceLoader {
	
	private static Map<Integer, String> textures = new HashMap<>();
	
	public static int loadTexture(String name) {
		int textureID = Loader.getInstance().loadTexture(name);
		String textureName = textures.get(textureID);
		
		if(textureName == null) {
			textures.put(textureID, name);
		}
		
		return textureID;
	}
}
