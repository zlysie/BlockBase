package net.oikmo.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.ItemBlock;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.Main;

public class InputManager {

	private final int toggleUIKey = Keyboard.KEY_F1;
	private final int screenShotKey = Keyboard.KEY_F2;
	private final int texturePackKey = Keyboard.KEY_F3;
	private final int refreshKey = Keyboard.KEY_F;
	private final int saveKey = Keyboard.KEY_R;
	private final int loadKey = Keyboard.KEY_T;
	private final int itemKey = Keyboard.KEY_Y;
	private final int pauseKey = Keyboard.KEY_ESCAPE;

	private boolean lockInScreenshot = false;
	private boolean lockInChangeTexture = false;
	private boolean lockInUI = false;
	private boolean lockInRefresh = false;
	private boolean lockInWorldSave = false;
	private boolean lockInWorldLoad = false;
	private boolean lockInItem = false;
	private boolean lockInPause = false;

	public void handleInput() {
		if(!Main.isPaused()) {
			if(Keyboard.isKeyDown(refreshKey)) {
				if(!lockInRefresh) {
					Main.theWorld.refreshChunks();
				}

				lockInRefresh = true;
			} else {
				lockInRefresh = false;
			}

			if(Keyboard.isKeyDown(saveKey)) {
				if(!lockInWorldSave) {
					Main.theWorld.saveWorld();
				}

				lockInWorldSave = true;
			} else {
				lockInWorldSave = false;
			}

			if(Keyboard.isKeyDown(loadKey)) {
				if(!lockInWorldLoad) {
					Main.theWorld.loadWorld();
				}

				lockInWorldLoad = true;
			} else {
				lockInWorldLoad = false;
			}

			if(Keyboard.isKeyDown(toggleUIKey)) {
				if(!lockInUI) {
					Main.currentScreen.onClose();
				}
				lockInUI = true;
			} else {
				lockInUI = false;
			}

			if(Keyboard.isKeyDown(screenShotKey)) {
				if(!lockInScreenshot) {
					DisplayManager.saveScreenshot();
				}
				lockInScreenshot = true;
			} else {
				lockInScreenshot = false;
			}

			if(Keyboard.isKeyDown(texturePackKey)) {
				if(!lockInChangeTexture) {
					int texture = MasterRenderer.currentTexturePack.getTextureID();
					if(texture == MasterRenderer.defaultTexturePack) {
						MasterRenderer.getInstance().setTexturePack(MasterRenderer.customTexturePack);
					} else {
						MasterRenderer.getInstance().setTexturePack(MasterRenderer.defaultTexturePack);
					}
				}
				lockInChangeTexture = true;
			} else {
				lockInChangeTexture = false;
			}

			if(Keyboard.isKeyDown(itemKey)) {
				if(!lockInItem) {
					System.out.println("creating");
					ItemBlock block = new ItemBlock(Block.bedrock, new Vector3f(Main.thePlayer.getCamera().getPosition()), true);
					block.setRotation(0.0f, Main.thePlayer.getCamera().getYaw()-90, 0.0f);
					//block.moveRelative(1, 0, 0.1f);
					//block.setPosition(block.getRoundedPosition());
					Main.theWorld.entities.add(block);
				}
				lockInItem = true;
			} else {
				lockInItem = false;
			}
		}
		
		if(!lockInPause) {
			if(Keyboard.isKeyDown(pauseKey)) {
				Main.shouldTick();
				lockInPause = true;
			}
		} else {
			if(!Keyboard.isKeyDown(pauseKey)) {
				lockInPause = false;
			}
		}
		
		
	}

}
