package net.oikmo.engine.entity;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.textures.ModelTexture;
import net.oikmo.engine.world.blocks.Block;

public class ItemBlock extends ItemEntity {

	public ItemBlock(Block block, Vector3f position, boolean bool) {
		super(new TexturedModel(CubeModel.getRawModel(block), new ModelTexture(MasterRenderer.currentTexturePack.getTextureID())), position);
		this.setSize(this.getScale(),this.getScale()/6);
	}
	
}
