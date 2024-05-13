package com.mojang.minecraft.particle;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.Tesselator;

import net.oikmo.engine.entity.Player;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.toolbox.FastMath;

public final class ParticleEngine {
	public List<Particle> particles = new ArrayList<>();
	private int texID;
	public ParticleEngine() {
		texID = MasterRenderer.particleTexture;
		//System.out.println(texID);
	}

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
	
	public final void render(Player player, float tickSpeed) {
		if(this.particles.size() != 0) {
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float lookCosY = -(FastMath.cos(player.getCamera().getRotation().y * (float)Math.PI / 180.0F));
			float lookSinY = -(FastMath.sin(player.getCamera().getRotation().y * (float)Math.PI / 180.0F));
			float lookSinX = -lookSinY * FastMath.sin(player.getCamera().getRotation().x * (float)Math.PI / 180.0F);
			float var6 = lookCosY * FastMath.sin(player.getCamera().getRotation().x * (float)Math.PI / 180.0F);
			float var11 = FastMath.cos(player.getCamera().getRotation().x * (float)Math.PI / 180.0F);
			Tesselator tess = Tesselator.instance;
			tess.begin();

			for(int i = 0; i < this.particles.size(); ++i) {
				Particle particle = (Particle)this.particles.get(i);
				float brightness = 1F * particle.getBrightness();
				tess.color(brightness, brightness, brightness);
				particle.render(tess, tickSpeed, lookCosY, var11, lookSinY, lookSinX, var6);
			}

			tess.end();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
}
