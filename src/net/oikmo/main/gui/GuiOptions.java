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
import net.oikmo.engine.world.World;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.toolbox.properties.OptionsHandler;

public class GuiOptions extends GuiScreen {
	
	private boolean mainmenu;
	
	public GuiOptions(boolean mainmenu) {
		super("Options");
		this.mainmenu = mainmenu;
	}
	
	private boolean vsync;
	
	private GuiSlider volume;
	private GuiSlider sensitivity;
	private GuiButton vsyncButton;
	private GuiButton renderDistanceButton;
	private GuiSlider fov;
	private GuiButton backButton;
	
	private int renderDistance = 1;
	
	public void onInit() {
		vsync = Boolean.parseBoolean(OptionsHandler.getInstance().translateKey("graphics.vsync"));
		
		renderDistance = Integer.parseInt(OptionsHandler.getInstance().translateKey("graphics.distance"));
		
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
				y = (Display.getHeight()/2)-20;
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
				y = (Display.getHeight()/2)-20;
			}
		});
		volume.setSliderValue(GameSettings.globalVolume, 0, 1);
		
		sensitivity = new GuiSlider((Display.getWidth()/2)-125,(Display.getHeight()/2)+20, 200, 30, Main.lang.translateKey("options.volume")+": "+(int)(GameSettings.globalVolume*100f)+"%");
		sensitivity.setGuiCommand(new GuiCommand() {
			public void invoke(float value) {
				updateSensitivityText();
			}
			
			public void update() {
				x = (Display.getWidth()/2)-125;
				y = (Display.getHeight()/2)+20;
			}
		});
		sensitivity.setSliderValue(GameSettings.sensitivity, 0, 0.4f);
		updateSensitivityText();
		
		vsyncButton = new GuiButton((Display.getWidth()/2)+125,(Display.getHeight()/2)+20, 200, 30, Main.lang.translateKey("options.vsync")+": "+vsync);
		vsyncButton.setGuiCommand(new GuiCommand() {
			public void invoke() {
				vsync = !vsync;
				vsyncButton.setText(Main.lang.translateKey("options.vsync")+": " + vsync);
				Display.setVSyncEnabled(vsync);
			}
			
			public void update() {
				x = (Display.getWidth()/2)+125;
				y = (Display.getHeight()/2)+20;
			}
		});
		
		renderDistanceButton = new GuiButton((Display.getWidth()/2),(Display.getHeight()/2)+65, 225, 30, Main.lang.translateKey("options.distance")+": ");
		renderDistanceButton.setGuiCommand(new GuiCommand() {
			public void invoke() {
				updateRenderDistanceText();
			}
			
			public void update() {
				x = (Display.getWidth()/2);
				y = (Display.getHeight()/2)+65;
			}
		});
		updateRenderDistanceText();
		
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
		} else if(fovFull <= 40) {
			fov.setText(fovText + Main.lang.translateKey("options.fov.low"));
		} else if(fovFull >= 100) {
			fov.setText(fovText + Main.lang.translateKey("options.fov.high"));
		} else {
			fov.setText(fovText + fovFull);
		}
	}
	
	public void updateRenderDistanceText() {
		if(renderDistance < 4) {
			renderDistance++;
		} else if(renderDistance >= 4) {
			renderDistance = 1;
		}
		
		String text = Main.lang.translateKey("options.distance")+": ";
		
		World.updateRenderSize(renderDistance*2);
		
		switch(renderDistance) {
		case 1:
			renderDistanceButton.setText(text + Main.lang.translateKey("options.distance.tiny"));
			break;
		case 2:
			renderDistanceButton.setText(text + Main.lang.translateKey("options.distance.small"));
			break;
		case 3:
			renderDistanceButton.setText(text + Main.lang.translateKey("options.distance.normal"));
			break;
		case 4:
			renderDistanceButton.setText(text + Main.lang.translateKey("options.distance.far"));
			break;
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
		vsyncButton.tick();
		if(Main.theNetwork == null) {
			renderDistanceButton.tick();
		}
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
		OptionsHandler.getInstance().insertKey("graphics.distance", renderDistance+"");
		OptionsHandler.getInstance().insertKey("graphics.fov", MasterRenderer.getInstance().FOV+"");
		OptionsHandler.getInstance().insertKey("graphics.vsync", Boolean.toString(vsync));
		OptionsHandler.getInstance().insertKey("input.sensitivity", GameSettings.sensitivity+"");
		OptionsHandler.getInstance().insertKey("audio.volume", GameSettings.globalVolume+"");
		
		Gui.cleanUp();
		fov.onCleanUp();
		volume.onCleanUp();
		sensitivity.onCleanUp();
		vsyncButton.onCleanUp();
		renderDistanceButton.onCleanUp();
		backButton.onCleanUp();
	}
}
