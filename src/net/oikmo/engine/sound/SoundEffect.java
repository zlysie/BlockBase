package net.oikmo.engine.sound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoundEffect {
	private String name;
	private List<SoundByte> sounds;
	
	public SoundEffect(String id, String name) {
		this.sounds = new ArrayList<>();
		this.name = name;
		int max = 5;
		if(name.contains("glass")) { max = 4; }
		for(int i = 1; i < max; i++) {
			this.sounds.add(new SoundByte(id, "/sfx/"+name+i+".ogg"));
		}
	}
	
	public SoundByte getByteFromIndex(int index) {
		if(name.contains("glass") && index > sounds.size()-1) {
			return sounds.get(new Random().nextInt(2));
		} else {
			return sounds.get(index);
		}
		
	}
	
	public int getAmountOfURLS() {
		return sounds.size();
	}
}
