package net.oikmo.engine;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import net.oikmo.main.Main;

public class ResourceLoader {
	private static Map<String, Integer> textures = new HashMap<>();
	private static Map<String, URL> audioFiles = new HashMap<>();

	/**
	 * Loads file into program, if the file hasn't been loaded before then it will be added to a static list. If it has then it retrieves a current instance of the file from the static list.
	 * @param name - {@link String}
	 * @return {@link Integer}
	 */
	public static int loadTexture(String name) {
		if(textures.get(name) == null) {
			textures.put(name, Loader.getInstance().loadTexture(name));
		}

		return textures.get(name);
	}

	/**
	 * Loads file into program, if the file hasn't been loaded before then it will be added to a static list. If it has then it retrieves a current instance of the file from the static list.
	 * @param file - {@link String}
	 * @return {@link URL}
	 */
	public static URL loadAudioFile(String file) {

		if(audioFiles.get(file) == null) {
			try {
				String path = Paths.get(Main.getResources() + "/music/"+ file).toString();
                URL url = new File(path).toURI().toURL();
                audioFiles.put(file, url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return audioFiles.get(file);
	}

	/**
	 * Uses the custom resources at .blockcraft
	 * @param file - {@link String}
	 * @return {@link URL}
	 */
	public static URL loadCustomAudioFile(String file) {
		if(audioFiles.get(file) == null) {
			audioFiles.put(file, Main.class.getResource(Main.getResources()+"/custom/music/" + file));
		}

		return audioFiles.get(file);
	}


}
