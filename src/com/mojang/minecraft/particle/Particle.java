package com.mojang.minecraft.particle;

import org.lwjgl.util.vector.Vector3f;

import com.mojang.minecraft.Tesselator;

import net.oikmo.engine.entity.Entity;
import net.oikmo.engine.world.blocks.Block;

public class Particle extends Entity {
	private float xd;
	private float yd;
	private float zd;
	public int tex;
	private float uo;
	private float vo;
	private int age = 0;
	private int lifetime = 0;
	private float size;
	private float gravity;

	public Particle(float x, float y, float z, float velX, float var6, float var7, Block block) {
		super(new Vector3f(), new Vector3f(), 0.2f);
		this.tex = block.getType();
		this.gravity = 0.5f;
		this.setSize(0.2F, 0.2F);
		this.heightOffset = this.bbHeight / 2.0F;
		this.setPos(x, y, z);
		this.xd = velX + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
		this.yd = var6 + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
		this.zd = var7 + (float)(Math.random() * 2.0D - 1.0D) * 0.4F;
		float var9 = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
		x = (float)Math.sqrt((double)(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd));
		this.xd = this.xd / x * var9 * 0.4F;
		this.yd = this.yd / x * var9 * 0.4F + 0.1F;
		this.zd = this.zd / x * var9 * 0.4F;
		this.uo = (float)Math.random() * 2F;
		this.vo = (float)Math.random() * 2F;
		this.size = (float)(Math.random() * 0.5D + 0.5D);
		this.lifetime = (int)(4.0D / (Math.random() * 0.9D + 0.1D));
		this.age = 0;
	}
	
	public boolean remove = false;
	
	public void tick() {
		if(this.age++ >= this.lifetime) {
			remove = true;
		}

		this.yd = (float)((double)this.yd - 0.04D * (double)this.gravity);
		this.move(this.xd, this.yd, this.zd, 1);
		this.xd *= 0.98F;
		this.yd *= 0.98F;
		this.zd *= 0.98F;
		if(this.onGround) {
			this.xd *= 0.7F;
			this.zd *= 0.7F;
		}

	}

	public void render(Tesselator t, float tickSpeed, float var3, float var4, float var5, float var6, float var7) {
		float var8 = ((float)(this.tex % 16) + this.uo / 4.0F) / 16.0F;
		float var9 = var8 + 0.999F / 32.0F;
		float var10 = ((float)(this.tex / 16) + this.vo / 4.0F) / 16.0F;
		float var11 = var10 + 0.999F / 32.0F;
		float var12 = 0.1F * this.size;
		float var13 = getPosition().x * tickSpeed;
		float var14 = getPosition().y * tickSpeed;
		float var15 = getPosition().z * tickSpeed;
		t.vertexUV(var13 - var3 * var12 - var6 * var12, var14 - var4 * var12, var15 - var5 * var12 - var7 * var12, var8, var11);
		t.vertexUV(var13 - var3 * var12 + var6 * var12, var14 + var4 * var12, var15 - var5 * var12 + var7 * var12, var8, var10);
		t.vertexUV(var13 + var3 * var12 + var6 * var12, var14 + var4 * var12, var15 + var5 * var12 + var7 * var12, var9, var10);
		t.vertexUV(var13 + var3 * var12 - var6 * var12, var14 - var4 * var12, var15 + var5 * var12 - var7 * var12, var9, var11);
	}
}
