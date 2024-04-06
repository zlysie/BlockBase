package net.oikmo.engine.sound;

import java.net.URL;

import net.oikmo.engine.ResourceLoader;

public class SoundByte {
	private String id;
	private String name;
	private URL location;
	
	public SoundByte(String id, String name) {
		this.id = id;
		this.name = name;
		this.location = ResourceLoader.loadAudioFile(name);
	}
	
	public String getID() {
		return id;
	}

	public String getFileName() {
		return name;
	}

	public URL getFileLocation() {
		return location;
	}
}
