package net.oikmo.engine.sound;

import java.util.ArrayList;
import java.util.List;

public class SoundEffect {
	private List<SoundByte> sounds;
	
	public SoundEffect(String id, String name) {
		this.sounds = new ArrayList<>();
		int max = 5;
		if(name.contains("glass")) { max = 4; }
		for(int i = 1; i < max; i++) {
			this.sounds.add(new SoundByte(id, "/sfx/"+name+i+".ogg"));
			System.out.println(this.sounds.get(i-1).getFileName() +" " + this.sounds.get(i-1).getFileLocation());
		}
	}
	
	public SoundByte getByteFromIndex(int index) {
		return sounds.get(index);
	}
	
	public int getAmountOfURLS() {
		return sounds.size();
	}
}
