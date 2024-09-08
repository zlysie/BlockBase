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

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryJavaSound;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundMaster {

	private static Map<String, SoundByte> music = new HashMap<>();
	private static Map<String, SoundByte> sfx = new HashMap<>();
	private static Map<String, SoundEffect> sfxCollection = new HashMap<>();
	public static SoundSystem soundSystem = null;

	private static File customMusic = new File(Main.getResources()+"/custom/music");

	private static Thread musicThread;
	private static int ticksBeforeMusic;

	/**
	 * Initalises soundsystem along
	 */
	public static void init() {
		ticksBeforeMusic = 12000 + new Random().nextInt(12000);
		
		try {
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.addLibrary(LibraryJavaSound.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
		} catch( SoundSystemException e ) {
			System.err.println("error linking with the plug-ins");
		}
		soundSystem = new SoundSystem();
		soundSystem.setVolume("music", 0.5f);
		soundSystem.setVolume("sfx", 0.25f);

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

		registerMusic();
		registerSFX();
	}
	
	
	/**
	 * Reads audio files from {@link Main#getResources()}/music/
	 */
	private static void registerMusic() {
		registerMusicByte("music.jackblack", "jackblock.ogg");

		registerMusicByte("music.minecraft", "calm1.ogg");
		registerMusicByte("music.clark", "calm2.ogg");
		registerMusicByte("music.sweden", "calm3.ogg");

		registerMusicByte("music.subwooferlullaby", "hal1.ogg");
		registerMusicByte("music.livingmice", "hal2.ogg");
		registerMusicByte("music.haggstrom", "hal3.ogg");
		registerMusicByte("music.danny", "hal4.ogg");

		registerMusicByte("music.key", "nuance1.ogg");
		registerMusicByte("music.oxygene", "nuance2.ogg");

		registerMusicByte("music.dryhands", "piano1.ogg");
		registerMusicByte("music.wethands", "piano2.ogg");
		registerMusicByte("music.miceonvenus", "piano3.ogg");

		registerMusicByte("music.mutation", "menu/menu1.ogg");
		registerMusicByte("music.moogcity", "menu/menu2.ogg");
		registerMusicByte("music.beginning", "menu/menu3.ogg");
		registerMusicByte("music.floatingtrees", "menu/menu4.ogg");

		File[] customTracks = customMusic.listFiles();
		if(customTracks.length != 0) {
			for(File track : customTracks) {
				String name = track.getName();
				if(name.endsWith(".ogg")) {
					registerCustomMusicByte("custom."+ name.replace(".ogg", ""), name);
					Logger.log(LogLevel.INFO, "Adding custom track: " + name + " ("+"custom."+ name.replace(".ogg", "")+")");
				}
			}
		}

	}
	private static void registerSFX() {
		registerSFXByte("entity.tnt.primed", "random/fuse.ogg");
		registerSFXByte("entity.generic.explode", "random/explode.ogg");
		registerSFXByte("ui.button.click","random/click.ogg");
		registerSFX("block.grass.break", "dig/grass");
		registerSFX("block.cloth.break", "dig/cloth");
		registerSFX("block.gravel.break", "dig/gravel");
		registerSFX("block.sand.break", "dig/sand");
		registerSFX("block.stone.break", "dig/stone");
		registerSFX("block.wood.break", "dig/wood");
		registerSFX("block.glass.break", "dig/glass");
	}
	
	public static void playRandomMusicIfReady() {
		if(GameSettings.globalVolume == 0.0F) {
			return;
		}
		if(!soundSystem.playing("music")) {
			if(ticksBeforeMusic > 0) {
				ticksBeforeMusic--;
			} else {
				List<SoundByte> bytes = new ArrayList<>();
				for(Map.Entry<String, SoundByte> entry : music.entrySet()) {
					SoundByte b = entry.getValue();
					if(!b.getFileName().contains("menu")) {
						bytes.add(entry.getValue());
					}
				}
				Collections.shuffle(bytes);
				
				SoundByte music = bytes.get(0);
				ticksBeforeMusic = new Random().nextInt(12000) + 12000;
				soundSystem.backgroundMusic("music", music.getFileLocation(), music.getFileName(), false);
				soundSystem.setVolume("music", GameSettings.globalVolume);
				soundSystem.play("music");
				Logger.log(LogLevel.INFO, (String.format("Playing {0} ({1})", music.getFileName(),music.getID())));
			}	
		}
	}
	

	private static float lastVolume = -1;
	public static void setVolume() {
		if(lastVolume != GameSettings.globalVolume) {
			soundSystem.stop("music");

			soundSystem.setVolume("music", GameSettings.globalVolume);
			lastVolume = GameSettings.globalVolume;
		}	
	}

	private static void registerMusicByte(String id, String fileName) {
		music.put(id, new SoundByte(id, "/music/"+fileName));
	}
	private static void registerCustomMusicByte(String id, String fileName) {
		music.put(id, SoundByte.custom(id, fileName));
	}

	@SuppressWarnings("unused")
	private static void registerCustomSFXByte(String id, String fileName) {
		sfx.put(id, SoundByte.custom(id, fileName));
	}
	private static void registerSFXByte(String id, String fileName) {
		sfx.put(id, new SoundByte(id, "/sfx/"+fileName));
	}
	private static void registerSFX(String id, String name) {
		sfxCollection.put(id, new SoundEffect(id, name));
	}

	public static void playSFX(String id) {
		SoundByte sbyte = sfx.get(id);
		if(sbyte != null) {
			soundSystem.newSource(false, id, sbyte.getFileLocation(), sbyte.getFileName(), false, 0, 0, 0, 0, 0);
			soundSystem.setVolume(sbyte.getID(), 0.25f * GameSettings.globalVolume);
			soundSystem.play(sbyte.getID());
		} else {
			SoundEffect sfx = sfxCollection.get(id);
			if(sfx != null) {
				SoundByte bytes = sfx.getByteFromIndex(new Random().nextInt(4));
				soundSystem.newSource(false, id, bytes.getFileLocation(), bytes.getFileName(), false, 0, 0, 0, 0, 0);
				soundSystem.setVolume(id, 0.25f * GameSettings.globalVolume);
				soundSystem.play(id);
			}
		}
	}

	public static SoundByte getMusicByte(String id) {
		return music.get(id);
	}

	public static void playMusic(String id) {
		SoundByte musicByte = music.get(id);
		if(musicByte != null) {
			soundSystem.backgroundMusic("music",musicByte.getFileLocation(), musicByte.getFileName(), false);
			soundSystem.setVolume("music", GameSettings.globalVolume);
			soundSystem.play("music");
		}
	}

	public static void playBlockBreakSFX(Block block, int x, int y, int z) {
		Block.Type enumType = block.getEnumType();
		String enumm = enumType.name().toLowerCase();
		if(enumType == Block.Type.DIRT || enumType == Block.Type.LEAVES || enumType == Block.Type.TNT) {
			enumm = "grass";
		} else if(enumType == Block.Type.BEDROCK || enumType == Block.Type.COBBLE 
				|| enumType == Block.Type.MOSSYCOBBLE || enumType == Block.Type.OBSIDIAN
				|| enumType == Block.Type.SMOOTHSTONE || enumType == Block.Type.BRICK
				|| enumType == Block.Type.IRONBLOCK || enumType == Block.Type.GOLDBLOCK
				|| enumType == Block.Type.DIAMONDBLOCK) {
			enumm = "stone";
		} else if(enumType == Block.Type.PLANKS) {
			enumm = "wood";
		}
		String id = "block."+enumm+".break";
		SoundEffect sfx = sfxCollection.get(id);
		if(sfx != null) {
			SoundByte bytes = sfx.getByteFromIndex(new Random().nextInt(4));
			soundSystem.newSource(false, id, bytes.getFileLocation(), bytes.getFileName(), false, x, y, z, 2, 16);
			soundSystem.setVolume(id, 0.25f * GameSettings.globalVolume);
			soundSystem.play(id);
		}
	}

	public static void setListener(Camera camera) {
		Vector3f pos = camera.getPosition();
		float x = pos.x;
		float y = pos.y;
		float z = pos.z;
		soundSystem.setListenerPosition(x, y, z);
		soundSystem.setListenerOrientation(camera.roll, camera.pitch, camera.yaw, 0, 90, 0);
	}

	public static void setListener(Camera camera, float f) {
		float yaw = camera.prevYaw + (camera.yaw - camera.yaw) * f;
		if(camera.getPosition() == null) { return; }
		double x = camera.prevPosition.x + (camera.getPosition().x -  camera.prevPosition.x) * (double)f;
		double y = camera.prevPosition.y + (camera.getPosition().y -  camera.prevPosition.y) * (double)f;
		double z = camera.prevPosition.z + (camera.getPosition().z -  camera.prevPosition.z) * (double)f;
		float f2 = FastMath.cos((float) (-yaw * 0.01745329F - Math.PI));
		float f3 = FastMath.sin((float) (-yaw * 0.01745329F - Math.PI));
		float lookX = -f3;
		float lookY = 0.0F;
		float lookZ = -f2;
		float upX = 0.0F;
		float upY = 1.0F;
		float upZ = 0.0F;
		soundSystem.setListenerPosition((float)x, (float)y, (float)z);
		soundSystem.setListenerOrientation(lookX, lookY, lookZ, upX, upY, upZ);
	}
	
	public static void cleanUp() {
		if(soundSystem != null) {
			soundSystem.cleanup();
		}
		if(musicThread != null) {
			musicThread.interrupt();
		}

	}
	
	public static void stopMusic() {
		if(musicThread != null) {
			musicThread.interrupt();
		}
		soundSystem.stop("music");
	}

	public static void playBlockPlaceSFX(Block block, int x, int y, int z) {
		Block.Type enumType = block.getEnumType();
		String enumm = enumType.name().toLowerCase();
		if(enumType == Block.Type.DIRT || enumType == Block.Type.LEAVES || enumType == Block.Type.TNT) {
			enumm = "grass";
		} else if(enumType == Block.Type.BEDROCK || enumType == Block.Type.COBBLE 
				|| enumType == Block.Type.MOSSYCOBBLE || enumType == Block.Type.OBSIDIAN
				|| enumType == Block.Type.SMOOTHSTONE || enumType == Block.Type.BRICK
				|| enumType == Block.Type.IRONBLOCK || enumType == Block.Type.GOLDBLOCK
				|| enumType == Block.Type.DIAMONDBLOCK || enumType == Block.Type.GLASS) {
			enumm = "stone";
		} else if(enumType == Block.Type.PLANKS) {
			enumm = "wood";
		} 
		String id = "block."+enumm+".break";
		SoundEffect sfx = sfxCollection.get(id);
		if(sfx != null) {
			SoundByte bytes = sfx.getByteFromIndex(new Random().nextInt(4));
			soundSystem.newSource(false, id, bytes.getFileLocation(), bytes.getFileName(), false, x, y, z, 2, 16);
			soundSystem.setVolume(id, 0.25f * GameSettings.globalVolume);

			soundSystem.play(id);
		}
	}
}
