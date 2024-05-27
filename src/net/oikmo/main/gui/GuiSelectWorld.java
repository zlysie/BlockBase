package net.oikmo.main.gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.Display;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.gui.component.slick.GuiCommand;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Maths;

public class GuiSelectWorld extends GuiScreen {

	public GuiSelectWorld() {
		super("worlds");
	}
	
	private boolean delete;
	
	private GuiButton world1Button;
	private GuiButton world2Button;
	private GuiButton world3Button;
	private GuiButton world4Button;
	private GuiButton world5Button;
	private GuiButton deleteButton;
	private GuiButton backButton;
	
	public void onInit() {
		float offsetY = -(6*30)/2;
		
		world1Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-150-5, 200, 30, Main.lang.translateKey("world.select.create"));
		world1Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				if(!delete) {
					String world = "world1";
					prepareCleanUp();
					Gui.cleanUp();
					if(world1Button.getText().contentEquals(Main.lang.translateKey("world.select.create"))) {
						Main.currentScreen = new GuiCreateWorld(world);
					} else {
						Main.loadWorld(world);
						Main.shouldTick();
						Main.currentScreen = null;
					}
				} else {
					try {
						FileUtils.deleteDirectory( new File(Main.getWorkingDirectory()+"/saves/world1/"));
					} catch (IOException e) {}
					updateButtons();
				}
				
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-150-5;
			}
		});
		
		world2Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-120, 200, 30, Main.lang.translateKey("world.select.create"));
		world2Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				if(!delete) {
					String world = "world2";
					prepareCleanUp();
					Gui.cleanUp();
					if(world2Button.getText().contentEquals(Main.lang.translateKey("world.select.create"))) {
						Main.currentScreen = new GuiCreateWorld(world);
					} else {
						Main.loadWorld(world);
						Main.shouldTick();
						Main.currentScreen = null;
					}
				} else {
					try {
						FileUtils.deleteDirectory( new File(Main.getWorkingDirectory()+"/saves/world2/"));
					} catch (IOException e) {}
					updateButtons();
				}
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-120;
			}
		});
		
		world3Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-90+5, 200, 30, Main.lang.translateKey("world.select.create"));
		world3Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				if(!delete) {
					String world = "world3";
					prepareCleanUp();
					Gui.cleanUp();
					if(world3Button.getText().contentEquals(Main.lang.translateKey("world.select.create"))) {
						Main.currentScreen = new GuiCreateWorld(world);
					} else {
						Main.loadWorld(world);
						Main.shouldTick();
						Main.currentScreen = null;
					}
				} else {
					try {
						FileUtils.deleteDirectory( new File(Main.getWorkingDirectory()+"/saves/world3/"));
					} catch (IOException e) {}
					updateButtons();
				}
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-90+5;
			}
		});
		
		world4Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-60+10, 200, 30, Main.lang.translateKey("world.select.create"));
		world4Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				if(!delete) {
					String world = "world4";
					prepareCleanUp();
					Gui.cleanUp();
					if(world4Button.getText().contentEquals(Main.lang.translateKey("world.select.create"))) {
						Main.currentScreen = new GuiCreateWorld(world);
					} else {
						Main.loadWorld(world);
						Main.shouldTick();
						Main.currentScreen = null;
					}
				} else {
					try {
						FileUtils.deleteDirectory( new File(Main.getWorkingDirectory()+"/saves/world4/"));
					} catch (IOException e) {}
					updateButtons();
				}
			}


			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-60+10;
			}
		});
		
		world5Button = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY-30+15, 200, 30, Main.lang.translateKey("world.select.create"));
		world5Button.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				if(!delete) {
					String world = "world5";
					prepareCleanUp();
					Gui.cleanUp();
					if(world5Button.getText().contentEquals(Main.lang.translateKey("world.select.create"))) {
						Main.currentScreen = new GuiCreateWorld(world);
					} else {
						Main.loadWorld(world);
						Main.shouldTick();
						Main.currentScreen = null;
					}
				} else {
					try {
						FileUtils.deleteDirectory( new File(Main.getWorkingDirectory()+"/saves/world5/"));
					} catch (IOException e) {}
					updateButtons();
				}
			}


			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY-30+15;
			}
		});
		
		deleteButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY+20, 200, 30, Main.lang.translateKey("gui.delete"));
		deleteButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				delete = !delete;
				if(delete) {
					deleteButton.setText(Main.lang.translateKey("gui.cancel"));
				} else {
					deleteButton.setText(Main.lang.translateKey("gui.delete"));
				}
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY+20;
			}
		});
		
		backButton = new GuiButton(Display.getWidth()/2, (Display.getHeight()/2)-offsetY+60, 200, 30, Main.lang.translateKey("gui.quit"));
		backButton.setGuiCommand(new GuiCommand() {
			@Override
			public void invoke() {
				prepareCleanUp();
				Main.currentScreen = new GuiMainMenu(Main.splashText);
			}

			@Override
			public void update() {
				x = Display.getWidth()/2;
				y = (Display.getHeight()/2)-offsetY+60;
			}
		});
		
		updateButtons();
	}
	
	public void updateButtons() {
		delete = false;
		if(delete) {
			deleteButton.setText("Cancel");
		} else {
			deleteButton.setText("Delete");
		}
		
		try {
			world1Button.setText("World 1 ("+ String.format("%.1f", Maths.getWorldSize("world1")/1024f) +"KB)");
		} catch (FileNotFoundException e) {
			world1Button.setText(Main.lang.translateKey("world.select.create"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			world2Button.setText("World 2 ("+ String.format("%.1f", Maths.getWorldSize("world2")/1024f) +"KB)");
		} catch (FileNotFoundException e) {
			world2Button.setText(Main.lang.translateKey("world.select.create"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			world3Button.setText("World 3 ("+ String.format("%.1f", Maths.getWorldSize("world3")/1024f) +"KB)");
		} catch (FileNotFoundException e) {
			world3Button.setText(Main.lang.translateKey("world.select.create"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			world4Button.setText("World 4 ("+ String.format("%.1f", Maths.getWorldSize("world4")/1024f) +"KB)");
		} catch (FileNotFoundException e) {
			world4Button.setText(Main.lang.translateKey("world.select.create"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			world5Button.setText("World 5 ("+ String.format("%.1f", Maths.getWorldSize("world5")/1024f) +"KB)");
		} catch (FileNotFoundException e) {
			world5Button.setText(Main.lang.translateKey("world.select.create"));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		deleteButton.tick(lockTick);
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
