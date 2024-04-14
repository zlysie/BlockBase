package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.gui.component.slick.slider.GuiSlider;
import net.oikmo.engine.gui.component.slick.textfield.GuiTextField;
import net.oikmo.main.Main;

public class GuiPauseMenu extends GuiScreen {
	
	public GuiPauseMenu() {
		super("Pause Menu");
	}
	
	
	private GuiSlider testSlider;
	private GuiTextField worldTextField;
	private GuiButton saveButton;
	private GuiButton loadButton;
	private GuiButton quitButton;
	
	public void onInit() {
		Main.shouldTick();
		
		worldTextField = new GuiTextField(Display.getWidth()/2, Display.getHeight()-60, 200, 30);
		worldTextField.setGuiCommand(new GuiCommand() {
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = Display.getHeight() - 60;
			}
		});
		
		saveButton = new GuiButton((Display.getWidth()/2) - 50, Display.getHeight()-20, 90, 30, "Save...");
		saveButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				if(worldTextField.hasContent()) {
					Main.theWorld.saveWorld(worldTextField.getInputText().replace(" ", "-"));
					System.out.println(worldTextField.getInputText().replace(" ", "-"));
				}
				
			}
			
			@Override
			public void update() {
				x = (Display.getWidth()/2) - 50;
				y = Display.getHeight() - 20;
			}
		});
		
		loadButton = new GuiButton((Display.getWidth()/2) + 50, Display.getHeight()-20, 90, 30, "Load...");
		loadButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				if(worldTextField.hasContent()) {
					Main.theWorld.loadWorld(worldTextField.getInputText().replace(" ", "-"));
					System.out.println(worldTextField.getInputText().replace(" ", "-"));
				}
			}
			
			@Override
			public void update() {
				x = (Display.getWidth()/2) + 50;
				y = Display.getHeight() - 20;
			}
		});
		
		testSlider = new GuiSlider(Display.getWidth()/2, (Display.getHeight()/2)-30, 200, 30, "Test slider");
		testSlider.setGuiCommand(new GuiCommand() {
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = Display.getHeight()/2 - 30;
			}
		});
		
		quitButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)+30, 200, 30, "Quit game");
		quitButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				Main.close();
				System.exit(0);
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = Display.getHeight()/2 + 30;
			}
		});
		
	}
	
	public void onUpdate() {
		if(Main.isPaused()) {
			//drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 32);
			drawBackground(ResourceLoader.loadUITexture("ui/ui_background3"));
			saveButton.tick();
			loadButton.tick();
			quitButton.tick();
			testSlider.tick();
			worldTextField.tick();
		}
	}
	
	public void onClose() {
		Main.shouldTick();
	}
}
