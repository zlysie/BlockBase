package net.oikmo.main.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.sound.SoundByte;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.properties.OptionsHandler;

public class GuiMainMenu extends GuiScreen {

	public GuiMainMenu(String splashText) {
		super("Main Menu");
		this.splashText = splashText;
	}

	private static Thread musicThread;	
	
	private GuiButton playButton;
	private GuiButton multiplayerButton;
	private GuiButton optionsButton;
	private GuiButton quitButton;

	private String[] menuIDS;
	
	private String splashText;
	
	private Camera mainMenuCamera;
	
	public void onInit() {
		ticksToWait = 0;
		if(Main.thePlayer != null) {
			Main.thePlayer.getCamera().setMouseLock(false);
		}
		Main.thePlayer = null;
		Main.shouldTick = false;
		Main.theWorld = null;
		String[] menuIDS = {
				"music.moogcity",
				"music.mutation",
				"music.beginning",
				"music.floatingtrees"
		};
		this.menuIDS = menuIDS;
		
		MasterRenderer.getInstance().FOV = 90f;
		MasterRenderer.getInstance().updateProjectionMatrix();
		
		Camera mainMenuCamera = new Camera();
		this.mainMenuCamera = mainMenuCamera;
		
		float offsetY = 20f;
		
		playButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY*2f, 200, 30, Main.lang.translateKey("title.singleplayer"));
		playButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Gui.cleanUp();
				Main.currentScreen = new GuiSelectWorld();
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY*2f;
			}
		});
		
		multiplayerButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2), 200, 30, Main.lang.translateKey("title.multiplayer"));
		multiplayerButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Gui.cleanUp();
				Main.currentScreen = new GuiServerList();
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY;
			}
		});
		
		optionsButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+offsetY*2, 200, 30, Main.lang.translateKey("options.title"));
		optionsButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Gui.cleanUp();
				Main.currentScreen = new GuiOptions(true);
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2);
			}
		});

		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+offsetY*4f, 200, 30, Main.lang.translateKey("title.quit"));
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				Main.close();
				System.exit(0);
			}

			@Override
			public void update() {
					x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+offsetY*2f;
			}
		});
		
		if(Main.theNetwork != null) {
			if(Main.theNetwork.players != null) {
				Main.theNetwork.players.clear();
				Main.theNetwork.disconnect();
			}
		}
		if(musicThread == null) {
			this.doRandomMusic();
		} else {
			if(!musicThread.isAlive()) {
				this.doRandomMusic();
			}
		}
	}
	
	public void doRandomMusic() {
		
		
		List<SoundByte> bytes = new ArrayList<>();
		for(String entry : menuIDS) {
			SoundByte b = SoundMaster.getMusicByte(entry);
			bytes.add(b);
			
		}
		Collections.shuffle(bytes);

		musicThread = new Thread(new Runnable(){
			public void run() {

				for(SoundByte musicByte : bytes) {
					SoundMaster.playMusic(musicByte.getID());
					long duration = Maths.getDurationOfOGG(musicByte.getFileLocation());
					long randomLong = new Random().nextInt(24000);
					long sum = duration + randomLong;

					Logger.log(LogLevel.INFO, "Playing " + FilenameUtils.getName(musicByte.getFileLocation().getPath()) + " (" + musicByte.getID() + ") and sleeping for " + duration + "ms with (random) " + randomLong + "+ ms with the sum of: " + (sum) + "ms");

					try {
						Thread.sleep(sum);
					} catch (InterruptedException e) {}
				}

				doRandomMusic();
			}
		});
		musicThread.setName("Music Player (Main Menu)");
		musicThread.start();
	}
	
	//private boolean isNegative = false;
	
	private int ticksToWait = 0;
	private int maxCoolDown = 30; //0.5s
	private boolean lockTick = false;
	public void onUpdate() {
		if(Main.isPaused()) {
			/*if(mainMenuCamera.pitch < -45) {
				isNegative = true;
			} else if(mainMenuCamera.pitch > 45) {
				isNegative = false;
			}
			
			if(!isNegative) {
				mainMenuCamera.pitch -= 0.025f;
			} else {
				mainMenuCamera.pitch += 0.025f;
			}*/
			
			float x = (Display.getWidth()/2);
			float y = (Display.getHeight()/2)-140;
			float width = (256+128)+64+64;
			float height = 64+32;
			
			MasterRenderer.getInstance().render(mainMenuCamera);
			drawTexture(Main.jmode ? ResourceLoader.loadUITexture("ui/title_j") : ResourceLoader.loadUITexture("ui/title"), x, y, width, height);
			drawShadowStringCentered(Color.yellow, x,((y+height/2)+20), splashText);
			drawShadowStringCentered(font.getWidth("Created by Oikmo")/2,Display.getHeight()-fontSize/2, "Created by Oikmo");
			playButton.tick(lockTick);
			multiplayerButton.tick(lockTick);
			optionsButton.tick(lockTick);
			quitButton.tick(lockTick);
		}
	}
	
	public void onTick() {
		mainMenuCamera.yaw += 0.1f;
		
		if(ticksToWait < maxCoolDown) {
			ticksToWait++;
		}
		
		if(!lockTick && ticksToWait >= maxCoolDown) {
			lockTick = true;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void stopMusic() {
		if(musicThread != null) {
			musicThread.interrupt();
			musicThread.stop();
		}
	}

	public void onClose() {
		try {
			MasterRenderer.getInstance().FOV = Float.parseFloat(OptionsHandler.getInstance().translateKey("graphics.fov"));	
		} catch(NumberFormatException e) {
			OptionsHandler.getInstance().insertKey("graphics.fov", 70+"");
		}
		MasterRenderer.getInstance().updateProjectionMatrix();
		
		playButton.onCleanUp();
		multiplayerButton.onCleanUp();
		quitButton.onCleanUp();
	}
}
