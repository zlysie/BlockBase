package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.gui.component.slick.textfield.GuiTextField;
import net.oikmo.main.Main;

public class GuiCreateWorld extends GuiScreen {

	private String world;

	public GuiCreateWorld(String world) {
		super("Disconnected");
		this.world = world;
	}

	private boolean superflat = false;
	
	private GuiTextField worldSeed;
	private GuiButton superflatButton;
	private GuiButton createButton;
	private GuiButton quitButton;

	public void onInit() {
		worldSeed = new GuiTextField(Main.lang.translateKey("world.create.seed"),  Display.getWidth()/2, (Display.getHeight()/2)-20, 200, 30);
		worldSeed.setGuiCommand(new GuiCommand() {
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-20;
			}
		});
		
		superflatButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+20, 200, 30, Main.lang.translateKey("world.create.superflat")+": OFF");
		superflatButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				superflat = !superflat;
				String bool = superflat ? "ON" : "OFF";
				superflatButton.setText(Main.lang.translateKey("world.create.superflat")+": "+bool);
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+20;
			}
		});
		
		createButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+80, 200, 30, Main.lang.translateKey("world.create"));
		createButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				
				String seed = worldSeed.getInputText().trim();
				if(seed.isEmpty()) {
					seed = null;
				}
				
				Main.loadWorld(world, seed, superflat);
				Main.shouldTick();
				Gui.cleanUp();
				Main.currentScreen = null;
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+40;
			}
		});
		
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+120, 200, 30, Main.lang.translateKey("gui.quit"));
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Main.currentScreen = new GuiSelectWorld();
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)+90;
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
		drawShadowStringCentered(Display.getWidth()/2, (fontSize), "Create world...");
		worldSeed.tick();
		superflatButton.tick(lockTick);
		createButton.tick(lockTick);
		quitButton.tick(lockTick);
	}

	public void onClose() {
		Gui.cleanUp();
		worldSeed.tick();
		superflatButton.onCleanUp();
		createButton.onCleanUp();
		quitButton.onCleanUp();
	}
}
