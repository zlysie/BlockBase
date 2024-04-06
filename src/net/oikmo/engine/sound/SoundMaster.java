package net.oikmo.engine.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;

import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundMaster {

	private static Map<String, SoundByte> music = new HashMap<>();
	private static SoundSystem soundSystem = null;

	public static void init() {
		//Initalises soundsystem
		try {
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.addLibrary(LibraryJavaSound.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
		} catch( SoundSystemException e ) {
			System.err.println("error linking with the plug-ins");
		}
		soundSystem = new SoundSystem();
		soundSystem.setVolume("music", 0.5f);

		addMusicByte("music.minecraft", "calm1.ogg");
		addMusicByte("music.clark", "calm2.ogg");
		addMusicByte("music.sweden", "calm3.ogg");

		addMusicByte("music.subwooferlullaby", "hal1.ogg");
		addMusicByte("music.livingmice", "hal2.ogg");
		addMusicByte("music.haggstrom", "hal3.ogg");
		addMusicByte("music.danny", "hal4.ogg");

		addMusicByte("music.key", "nuance1.ogg");
		addMusicByte("music.oxygene", "nuance2.ogg");

		addMusicByte("music.dryhands", "piano1.ogg");
		addMusicByte("music.wethands", "piano2.ogg");
		addMusicByte("music.miceonvenus", "piano3.ogg");

		addRandomMusic();

		soundSystem.activate("music");
	}

	public static void addRandomMusic() {
		List<SoundByte> bytes = new ArrayList<>();
		for(Map.Entry<String, SoundByte> entry : music.entrySet()) {
			bytes.add(entry.getValue());
		}
		Collections.shuffle(bytes);

		Thread musicThread = new Thread(new Runnable(){
			public void run() {
				synchronized(bytes) {
					for(SoundByte musicByte : bytes) {
						soundSystem.backgroundMusic("music", musicByte.getFileLocation(), musicByte.getFileName(), false);
						
						long duration = Maths.getDurationOfOGG(musicByte.getFileLocation());
						long randomLong = new Random().nextInt(12000);
						long sum = duration + randomLong;
						
						Logger.log(LogLevel.INFO, "Playing " + FilenameUtils.getName(musicByte.getFileLocation().getPath()) + " (" + musicByte.getID() + ") and sleeping for " + duration + "ms with  " + randomLong + " more random ms with the sum of: " + (sum) + "ms");
						
						try {
							Thread.sleep(sum);
						} catch (InterruptedException e) {}
					}
				}
				
				addRandomMusic();
			}
		});
		musicThread.setName("Music Player (BG)");
		musicThread.start();
		
		
	}

	public static void playMusic(SoundByte musicByte) {
		soundSystem.queueSound("music", musicByte.getFileLocation(), musicByte.getFileName());
	}

	public static void playMusic(String id) {
		SoundByte musicByte = music.get(id);
		soundSystem.queueSound("music", musicByte.getFileLocation(), musicByte.getFileName());
	}

	private static void addMusicByte(String id, String fileName) {
		music.put(id, new SoundByte(id, fileName));
	}

}
