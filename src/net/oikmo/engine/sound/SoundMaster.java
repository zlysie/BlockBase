package net.oikmo.engine.sound;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;

import net.oikmo.main.Main;
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

	private static File customMusic = new File(Main.getResources()+"/custom/music");

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

		if(!customMusic.exists()) {
			customMusic.mkdirs();
		}

		File readme = new File(customMusic+"/README.TXT");
		if(!readme.exists()) {
			try {
				readme.createNewFile();
			} catch (IOException e) {
				Logger.log(LogLevel.WARN, "Unable to create README at " + customMusic.getAbsolutePath());
			}
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(readme);
			fw.write("### --- Created at "+ Logger.getCurrentTime() +  " --- ###");
			fw.write("\r\nHello! This is the custom music folder! In here you can put in custom music that gets added to the game at start! (not during runtime.)");
			fw.write("\r\nPlease note that music files must be .ogg to be loaded (otherwise it is ignored)");
			fw.write("\r\n- Oikmo :D");
			fw.close();
		} catch (IOException e) {
			Logger.log(LogLevel.WARN, "Unable to write into README at " + customMusic.getAbsolutePath());
		}
		
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
		
		File[] customTracks = customMusic.listFiles();
		if(customTracks.length != 0) {
			for(File track : customTracks) {
				String name = track.getName();
				if(name.endsWith(".ogg")) {
					addCustomMusicByte("custom."+ name.replace(".ogg", ""), name);
					Logger.log(LogLevel.INFO, "Adding custom track: " + name + " ("+"custom."+ name.replace(".ogg", "")+")");
				}
			}
		}

		soundSystem.activate("music");
		doRandomMusic();
	}

	public static void doRandomMusic() {
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
						long randomLong = new Random().nextInt(24000);
						long sum = duration + randomLong;

						Logger.log(LogLevel.INFO, "Playing " + FilenameUtils.getName(musicByte.getFileLocation().getPath()) + " (" + musicByte.getID() + ") and sleeping for " + duration + "ms with (random) " + randomLong + "+ ms with the sum of: " + (sum) + "ms");

						try {
							Thread.sleep(sum);
						} catch (InterruptedException e) {}
					}
				}

				doRandomMusic();
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

	private static void addCustomMusicByte(String id, String fileName) {
		music.put(id, SoundByte.custom(id, fileName));
	}

	private static void addMusicByte(String id, String fileName) {
		music.put(id, new SoundByte(id, fileName));
	}

	public static void cleanUp() {
		if(soundSystem != null) {
			soundSystem.cleanup();
		}
	}
}
