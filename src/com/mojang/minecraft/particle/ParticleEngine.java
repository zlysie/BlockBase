package com.mojang.minecraft.particle;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Tesselator;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.entity.Player;
import net.oikmo.toolbox.FastMath;

public final class ParticleEngine {
	public List<Particle> particles = new ArrayList<>();
	public ParticleEngine() {}

	public final void tick() {
		for(int var1 = 0; var1 < this.particles.size(); ++var1) {
			Particle var2 = (Particle)this.particles.get(var1);
			var2.tick();
			//System.out.println(particles.size() + " " + var2.remove);
			if(var2.remove) {
				this.particles.remove(var1--);
			}
		}

	}
	int texID = ResourceLoader.loadTexture("textures/particles/blocks");
	public final void render(Player player, float tickSpeed) {
		if(this.particles.size() != 0) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
			float var12 = -(FastMath.cos(player.getCamera().getRotation().y * (float)Math.PI / 180.0F));
			float var4 = -(FastMath.sin(player.getCamera().getRotation().y * (float)Math.PI / 180.0F));
			float var5 = -var4 * FastMath.sin(player.getCamera().getRotation().x * (float)Math.PI / 180.0F);
			float var6 = var12 * FastMath.sin(player.getCamera().getRotation().x * (float)Math.PI / 180.0F);
			float var11 = FastMath.cos(player.getCamera().getRotation().x * (float)Math.PI / 180.0F);
			Tesselator tess = Tesselator.instance;
			tess.begin();

			for(int var8 = 0; var8 < this.particles.size(); ++var8) {
				Particle particle = (Particle)this.particles.get(var8);
				float brightness = 1F * particle.getBrightness();
				tess.color(brightness, brightness, brightness);
				particle.render(tess, tickSpeed, var12, var11, var4, var5, var6);
			}

			tess.end();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
	}
}
