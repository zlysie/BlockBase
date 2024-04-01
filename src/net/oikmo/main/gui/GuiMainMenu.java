package net.oikmo.main.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.sound.SoundByte;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;

public class GuiMainMenu extends GuiScreen {

	public GuiMainMenu() {
		super("Main Menu");
	}

	private Thread musicThread;	
	
	private GuiButton playButton;
	private GuiButton quitButton;

	private String[] menuIDS;
	
	private String[] splashes;
	
	private String splashText;
	
	public void onInit() {
		String[] menuIDS = {
				"music.moogcity",
				"music.mutation",
				"music.beginning",
				"music.floatingtrees"
		};
		this.menuIDS = menuIDS;
		this.splashes = Maths.fileToArray("splashes.txt");
		this.splashText = splashes[new Random().nextInt(splashes.length)];
		
		float offsetY = 20f;
		
		playButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY, 200, 30, "Play game");
		playButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY;
			}
		});

		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+offsetY, 200, 30, "Quit game");
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				Main.close();
				System.exit(0);
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+offsetY;
			}
		});
		
		doRandomMusic();
	}
	
	private void doRandomMusic() {
		List<SoundByte> bytes = new ArrayList<>();
		for(String entry : menuIDS) {
			SoundByte b = SoundMaster.getMusicByte(entry);
			bytes.add(b);
			
		}
		Collections.shuffle(bytes);

		musicThread = new Thread(new Runnable(){
			public void run() {
				
				try {
					long thing = new Random().nextInt(12000);
					System.out.println("waiting for " + thing + "ms");
					Thread.sleep(thing);
					
					
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

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
	
	public void onUpdate() {
		if(Main.isPaused()) {
			drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 48);
			float x = (Display.getWidth()/2);
			float y = (Display.getHeight()/2)-100;
			float width = 256;
			float height = 64;
			
			
			drawImage(ResourceLoader.loadUITexture("ui/title"), x, y, width, height);
			
			float yOffset = splashText.length() > 16 ? splashText.length()/3 : 0;
			
			drawShadowStringCentered(fontSize, -10, Color.yellow, x+width/2,((y+height/2)+10)+yOffset, splashText);
			playButton.tick();
			quitButton.tick();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void onClose() {
		musicThread.interrupt();
		musicThread.stop();
	}
}
