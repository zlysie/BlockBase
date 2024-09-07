package net.oikmo.engine.entity;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.network.packet.PacketPlaySoundAt;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.Main;

public class PrimedTNT extends Entity {

	private int timer = 0;
	private boolean actuallyExplode;
	
	public PrimedTNT(Vector3f position, float xa, float ya, float za, boolean actuallyExplode) {
		super(new TexturedModel(CubeModel.getRawModel(Block.tnt),MasterRenderer.currentTexturePack), new Vector3f(position), new Vector3f(), 1);
		int x = (int) (position.x);
		int y = (int) (position.y);
		int z = (int) (position.z);
		this.setSize(1f, 1f);
		//this.setRotation(0.0f, new Random().nextInt(4)*90, 0.0f);
		motion.y = ya;
		
		//this.moveRelative(xa, za, this.isOnGround() ? 0.015F : 0.005F);
		if(actuallyExplode) {
			SoundMaster.playSFX("entity.tnt.primed");
			if(Main.theNetwork != null) {
				PacketPlaySoundAt packet = new PacketPlaySoundAt();
				packet.blockID = -1;
				packet.sfxID = "entity.tnt.primed";
				packet.x = x;
				packet.y = y;
				packet.z = z;
				Main.theNetwork.client.sendTCP(packet);
			}
		}
		
		this.actuallyExplode = actuallyExplode;
	}
	
	public void tick() {
		timer++;
		
		this.motion.y = (float)((double)this.motion.y - 0.008D);
		this.moveWithoutSound(this.motion.x, this.motion.y, this.motion.z,1);
		
		int x = (int) getPosition().x;
		int y = (int) getPosition().y;
		int z = (int) getPosition().z;
		
		if((timer % 45f)/45f <= 0.5f) {
			this.setWhiteOffset(15);
		} else {
			this.setWhiteOffset(0);
		}
		
		if(getPosition().y < 0) {
			remove = true;
		}
		
		if(timer >= 60*5 && !remove) {
			if(actuallyExplode) {
				Main.theWorld.createRadiusFromBlock(5, null, x, y, z);
				SoundMaster.playSFX("entity.generic.explode");
				if(Main.theNetwork != null) {
					PacketPlaySoundAt packet = new PacketPlaySoundAt();
					packet.blockID = -1;
					packet.sfxID = "entity.generic.explode";
					packet.x = x;
					packet.y = y;
					packet.z = z;
					Main.theNetwork.client.sendTCP(packet);
				}
			}
			
			remove = true;
		}
	}

}
