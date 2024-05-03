package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Maths;

public class GuiSelectWorld extends GuiScreen {

	public GuiSelectWorld() {
		super("worlds");
	}
	
	private GuiButton world1Button;
	private GuiButton world2Button;
	private GuiButton world3Button;
	private GuiButton world4Button;
	private GuiButton world5Button;
	private GuiButton backButton;
	
	public void onInit() {
		float offsetY = -(6*30)/2;
		
		world1Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-150-5, 200, 30, "Create new world...");
		world1Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				
				Main.loadWorld("world1");
				Main.shouldTick();
				Gui.cleanUp();
				Main.currentScreen = null;
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-150;
			}
		});

		if(SaveSystem.load("world1") != null) {
			world1Button.setText("World 1 ("+ String.format("%.1f", Maths.getWorldSize("world1")/1024f) +"KB)");
		}
		
		world2Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-120, 200, 30, "Create new world...");
		world2Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				SoundMaster.stopMusic();
				SoundMaster.doMusic();
				Main.loadWorld("world2");
				Main.shouldTick();
				Gui.cleanUp();
				Main.currentScreen = null;
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-120;
			}
		});
		
		if(SaveSystem.load("world2") != null) {
			world2Button.setText("World 2 ("+ String.format("%.1f", Maths.getWorldSize("world2")/1024f) +"KB)");
		}
		
		world3Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-90+5, 200, 30, "Create new world...");
		world3Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				SoundMaster.stopMusic();
				SoundMaster.doMusic();
				Main.loadWorld("world3");
				Main.shouldTick();
				Gui.cleanUp();
				Main.currentScreen = null;
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-90;
			}
		});
		
		if(SaveSystem.load("world3") != null) {
			world3Button.setText("World 3 ("+ String.format("%.1f", Maths.getWorldSize("world3")/1024f) +"KB)");
		}
		
		world4Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-60+10, 200, 30, "Create new world...");
		world4Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				SoundMaster.stopMusic();
				SoundMaster.doMusic();
				Main.loadWorld("world4");
				Main.shouldTick();
				Gui.cleanUp();
				Main.currentScreen = null;
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-60;
			}
		});
		
		if(SaveSystem.load("world4") != null) {
			world4Button.setText("World 4 ("+ String.format("%.1f", Maths.getWorldSize("world4")/1024f) +"KB)");
		}
		
		
		world5Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-30+15, 200, 30, "Create new world...");
		world5Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				SoundMaster.stopMusic();
				SoundMaster.doMusic();
				Main.loadWorld("world5");
				Main.shouldTick();
				Gui.cleanUp();
				Main.currentScreen = null;
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-30;
			}
		});
		
		if(SaveSystem.load("world5") != null) {
			world5Button.setText("World 5 ("+ String.format("%.1f", Maths.getWorldSize("world5")/1024f) +"KB)");
		}
		
		backButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY+20, 200, 30, "Back");
		backButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Main.currentScreen = new GuiMainMenu(Main.splashText);
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY;
			}
		});
	}
	
	private int ticksToWait = 0;
	private int maxCoolDown = 60; //1s
	private boolean lockTick = false;
	public void onUpdate() {
		drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 48);
		if(ticksToWait < maxCoolDown) {
			ticksToWait++;
		}
		
		if(!lockTick && ticksToWait >= maxCoolDown) {
			lockTick = true;
		}
		world1Button.tick(lockTick);
		world2Button.tick(lockTick);
		world3Button.tick(lockTick);
		world4Button.tick(lockTick);
		world5Button.tick(lockTick);
		backButton.tick(lockTick);
		
	}

	public void onClose() {
		Gui.cleanUp();
		world1Button.onCleanUp();
		world2Button.onCleanUp();
		world3Button.onCleanUp();
		world4Button.onCleanUp();
		world5Button.onCleanUp();
		backButton.onCleanUp();
	}
}
