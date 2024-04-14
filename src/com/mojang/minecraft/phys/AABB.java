package com.mojang.minecraft.phys;

public class AABB {
	private float epsilon = 0.0F;
	public float minX;
	public float minY;
	public float minZ;
	public float maxX;
	public float maxY;
	public float maxZ;

	public AABB(float x0, float y0, float z0, float x1, float y1, float z1) {
		this.minX = x0;
		this.minY = y0;
		this.minZ = z0;
		this.maxX = x1;
		this.maxY = y1;
		this.maxZ = z1;
	}

	public AABB expand(float xa, float ya, float za) {
		float _x0 = this.minX;
		float _y0 = this.minY;
		float _z0 = this.minZ;
		float _x1 = this.maxX;
		float _y1 = this.maxY;
		float _z1 = this.maxZ;
		if(xa < 0.0F) {
			_x0 += xa;
		}

		if(xa > 0.0F) {
			_x1 += xa;
		}

		if(ya < 0.0F) {
			_y0 += ya;
		}

		if(ya > 0.0F) {
			_y1 += ya;
		}

		if(za < 0.0F) {
			_z0 += za;
		}

		if(za > 0.0F) {
			_z1 += za;
		}

		return new AABB(_x0, _y0, _z0, _x1, _y1, _z1);
	}

	public AABB grow(float xa, float ya, float za) {
		float _x0 = this.minX - xa;
		float _y0 = this.minY - ya;
		float _z0 = this.minZ - za;
		float _x1 = this.maxX + xa;
		float _y1 = this.maxY + ya;
		float _z1 = this.maxZ + za;
		return new AABB(_x0, _y0, _z0, _x1, _y1, _z1);
	}

	public float clipXCollide(AABB c, float xa) {
		if(c.maxY > this.minY && c.minY < this.maxY) {
			if(c.maxZ > this.minZ && c.minZ < this.maxZ) {
				float max;
				if(xa > 0.0F && c.maxX <= this.minX) {
					max = this.minX - c.maxX - this.epsilon;
					if(max < xa) {
						xa = max;
					}
				}

				if(xa < 0.0F && c.minX >= this.maxX) {
					max = this.maxX - c.minX + this.epsilon;
					if(max > xa) {
						xa = max;
					}
				}

				return xa;
			} else {
				return xa;
			}
		} else {
			return xa;
		}
	}

	public float clipYCollide(AABB c, float ya) {
		if(c.maxX > this.minX && c.minX < this.maxX) {
			if(c.maxZ > this.minZ && c.minZ < this.maxZ) {
				float max;
				if(ya > 0.0F && c.maxY <= this.minY) {
					max = this.minY - c.maxY - this.epsilon;
					if(max < ya) {
						ya = max;
					}
				}

				if(ya < 0.0F && c.minY >= this.maxY) {
					max = this.maxY - c.minY + this.epsilon;
					if(max > ya) {
						ya = max;
					}
				}

				return ya;
			} else {
				return ya;
			}
		} else {
			return ya;
		}
	}

	public float clipZCollide(AABB c, float za) {
		if(c.maxX > this.minX && c.minX < this.maxX) {
			if(c.maxY > this.minY && c.minY < this.maxY) {
				float max;
				if(za > 0.0F && c.maxZ <= this.minZ) {
					max = this.minZ - c.maxZ - this.epsilon;
					if(max < za) {
						za = max;
					}
				}

				if(za < 0.0F && c.minZ >= this.maxZ) {
					max = this.maxZ - c.minZ + this.epsilon;
					if(max > za) {
						za = max;
					}
				}

				return za;
			} else {
				return za;
			}
		} else {
			return za;
		}
	}

	public boolean intersects(AABB c) {
		return c.maxX > this.minX && c.minX < this.maxX ? (c.maxY > this.minY && c.minY < this.maxY ? c.maxZ > this.minZ && c.minZ < this.maxZ : false) : false;
	}

	public void move(float xa, float ya, float za) {
		this.minX += xa;
		this.minY += ya;
		this.minZ += za;
		this.maxX += xa;
		this.maxY += ya;
		this.maxZ += za;
	}
}
