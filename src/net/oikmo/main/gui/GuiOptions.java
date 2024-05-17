package net.oikmo.main.gui;

import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.gui.component.slick.slider.GuiSlider;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.toolbox.properties.OptionsHandler;

public class GuiOptions extends GuiScreen {
	
	private boolean mainmenu;
	
	public GuiOptions(boolean mainmenu) {
		super("Options");
		this.mainmenu = mainmenu;
	}
	
	private GuiSlider volume;
	private GuiSlider sensitivity;
	private GuiSlider fov;
	private GuiButton backButton;
	
	public void onInit() {
		fov = new GuiSlider((Display.getWidth()/2)+125, (Display.getHeight()/2)-20, 200, 30, Main.lang.translateKey("options.fov")+": "+(int)(MasterRenderer.getInstance().FOV));
		fov.setGuiCommand(new GuiCommand() {
			public void invoke(float value) {
				
				int fovFull = (int) (30+(value*80));
				MasterRenderer.getInstance().FOV = fovFull;
				updateFOVText();
				
				MasterRenderer.getInstance().updateProjectionMatrix();
				
			}
			
			public void update() {
				x = (Display.getWidth()/2)+125;
				y = Display.getHeight()/2;
			}
		});
		this.updateFOVText();
		
		volume = new GuiSlider((Display.getWidth()/2)-125, (Display.getHeight()/2)-20, 200, 30, Main.lang.translateKey("options.volume")+": "+(int)(GameSettings.globalVolume*100f)+"%");
		volume.setGuiCommand(new GuiCommand() {
			public void invoke(float value) {
				int percievedValue = (int)(value*100f);
				float actualValue = percievedValue/100f;
				GameSettings.globalVolume = actualValue;
				SoundMaster.setVolume();
				
				volume.setText(Main.lang.translateKey("options.volume")+": " + percievedValue+"%");
			}
			
			public void update() {
				x = (Display.getWidth()/2)-125;
				y = Display.getHeight()/2;
			}
		});
		volume.setSliderValue(GameSettings.globalVolume, 0, 1);
		
		sensitivity = new GuiSlider((Display.getWidth()/2),(Display.getHeight()/2)+20, 200, 30, Main.lang.translateKey("options.volume")+": "+(int)(GameSettings.globalVolume*100f)+"%");
		sensitivity.setGuiCommand(new GuiCommand() {
			public void invoke(float value) {
				updateSensitivityText();
			}
			
			public void update() {
				x = (Display.getWidth()/2)-125;
				y = Display.getHeight()/2;
			}
		});
		sensitivity.setSliderValue(GameSettings.sensitivity, 0, 0.4f);
		updateSensitivityText();
		
		backButton = new GuiButton(Display.getWidth()/2, (Display.getHeight())-60, 200, 30, Main.lang.translateKey("gui.done"));
		backButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				if(mainmenu) {
					Main.currentScreen = new GuiMainMenu(Main.splashText);
				} else {
					Main.currentScreen = new GuiPauseMenu();
				}
				
			}
			
			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight())-60;
			}
		});
	}
	
	public void updateFOVText() {
		fov.setSliderValue(MasterRenderer.getInstance().FOV, 30, 110);
		int fovFull = (int) (30+(fov.getValue()*80));
		String fovText = Main.lang.translateKey("options.fov")+": ";
		if(fovFull == 70) {
			fov.setText(fovText + Main.lang.translateKey("options.fov.normal"));
		} else if(fovFull == 30) {
			fov.setText(fovText + Main.lang.translateKey("options.fov.low"));
		} else if(fovFull == 110) {
			fov.setText(fovText + Main.lang.translateKey("options.fov.high"));
		} else {
			fov.setText(fovText + fovFull);
		}
	}
	
	public void updateSensitivityText() {
		int sensitivityFull = (int)(sensitivity.getValue()*100f);
		float actualValue = (sensitivity.getValue())*0.4f;
		GameSettings.sensitivity = actualValue;
		
		String sensitivityText = Main.lang.translateKey("options.sensitivity")+": ";
		if(sensitivityFull <= 15) {
			sensitivity.setText(sensitivityText + Main.lang.translateKey("options.sensitivity.low"));
		} else if(sensitivityFull >= 90) {
			sensitivity.setText(sensitivityText + Main.lang.translateKey("options.sensitivity.high"));
		} else {
			sensitivity.setText(sensitivityText + sensitivityFull+"%");
		}
	}
	
	public void onUpdate() {
		if(!mainmenu) {
			drawBackground(ResourceLoader.loadUITexture("ui/ui_background3"));
		} else {
			drawTiledBackground(ResourceLoader.loadUITexture("dirtTex"), 48);
		}
		
		drawShadowStringCentered(Display.getWidth()/2,fontSize*2,Main.lang.translateKey("options.title"));
		
		fov.tick();
		volume.tick();
		sensitivity.tick();
		backButton.tick();
	}
	
	public void onClose() {
		if(Main.theNetwork != null) {
			if(Main.thePlayer != null) {
				Main.thePlayer.getCamera().setMouseLock(false);
			} else {
				Main.disconnect(false, Main.lang.translateKey("network.disconnect.u"));
			}
			
		} else {
			Main.shouldTick();
		}
		OptionsHandler.getInstance().insertKey("graphics.fov", MasterRenderer.getInstance().FOV+"");
		OptionsHandler.getInstance().insertKey("audio.volume", GameSettings.globalVolume+"");
		OptionsHandler.getInstance().insertKey("input.sensitivity", GameSettings.sensitivity+"");
		Gui.cleanUp();
		volume.onCleanUp();
		backButton.onCleanUp();
	}
}
