package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.main.Main;
import net.oikmo.toolbox.properties.LanguageHandler;

public class GuiConnecting extends GuiScreen {
	
	public GuiConnecting() {
		super("Connecting");
	}
	
	private GuiButton quitButton;
	
	public void onInit() {
		if(Main.theNetwork != null) {
			Main.thePlayer.getCamera().setMouseLock(false);
		} else {
			Main.shouldTick();
		}
		
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+40, 200, 30, LanguageHandler.getInstance().translateKey("gui.quit"));
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Main.theNetwork.disconnect();
				Main.disconnect(false, "");
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
	private String elipsis = "";
	public void onTick() {
		if(ticksToWait < maxCoolDown) {
			ticksToWait++;
		}
		if((ticksToWait % 60) >= 8) {
			elipsis += ".";
		}
		
		long count = elipsis.chars().filter(ch -> ch == '.').count();
		if(count >= 3) {
			elipsis = "";
		}
		
		if(!lockTick && ticksToWait >= maxCoolDown) {
			lockTick = true;
		}
	}
	
	public void onUpdate() {
		drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 48);
		drawShadowStringCentered(Display.getWidth()/2, (Display.getHeight()/2), Main.lang.translateKey("network.connecting") + elipsis);
		quitButton.tick(lockTick);
		
		if(Main.thePlayer != null) {
			if(Main.thePlayer.tick) {
				prepareCleanUp();
				Main.thePlayer.getCamera().setMouseLock(true);
				Main.currentScreen = null;
			}
		}
		
	}
	
	public void onClose() {
		Gui.cleanUp();
		quitButton.onCleanUp();
	}
}
