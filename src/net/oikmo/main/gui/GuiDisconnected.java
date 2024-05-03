package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.main.Main;

public class GuiDisconnected extends GuiScreen {
	
	private String message;
	private boolean kick;
	
	public GuiDisconnected(boolean kick, String message) {
		super("Disconnected");
		this.message = message;
		this.kick = kick;
	}
	
	private GuiButton quitButton;
	
	
	public void onInit() {
		if(Main.network != null) {
			Main.thePlayer.getCamera().setMouseLock(false);
		} else {
			Main.shouldTick();
		}
		
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+40, 200, 30, "Back to main menu");
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Main.currentScreen = new GuiMainMenu(Main.getRandomSplash());
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+40;
			}
		});
	}
	
	private int ticksToWait = 0;
	private int maxCoolDown = 60;
	private boolean lockTick = false;
	public void onTick() {
		if(ticksToWait < maxCoolDown) {
			ticksToWait++;
		}
		
		if(!lockTick && ticksToWait >= maxCoolDown) {
			lockTick = true;
		}
	}
	
	public void onUpdate() {
		drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 48);
		drawShadowStringCentered(Display.getWidth()/2, (Display.getHeight()/2), (!kick ? "Disconnected: " : "Kicked: ") + message);
		quitButton.tick(lockTick);
	}
	
	public void onClose() {
		Gui.cleanUp();
		quitButton.onCleanUp();
	}
}
