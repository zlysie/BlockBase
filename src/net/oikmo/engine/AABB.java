package net.oikmo.engine;

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

	public AABB grow(float xa, float ya, float za) {
		float _x0 = this.minX - xa;
		float _y0 = this.minY - ya;
		float _z0 = this.minZ - za;
		float _x1 = this.maxX + xa;
		float _y1 = this.maxY + ya;
		float _z1 = this.maxZ + za;
		return new AABB(_x0, _y0, _z0, _x1, _y1, _z1);
	}

	public float clipXCollide(AABB other, float xa) {
		if(other.maxY > this.minY && other.minY < this.maxY) {
			if(other.maxZ > this.minZ && other.minZ < this.maxZ) {
				float max;
				if(xa > 0.0F && other.maxX <= this.minX) {
					max = this.minX - other.maxX - this.epsilon;
					if(max < xa) {
						xa = max;
					}
				}

				if(xa < 0.0F && other.minX >= this.maxX) {
					max = this.maxX - other.minX + this.epsilon;
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

	public float clipYCollide(AABB other, float ya) {
		if(other.maxX > this.minX && other.minX < this.maxX) {
			if(other.maxZ > this.minZ && other.minZ < this.maxZ) {
				float max;
				if(ya > 0.0F && other.maxY <= this.minY) {
					max = this.minY - other.maxY - this.epsilon;
					if(max < ya) {
						ya = max;
					}
				}

				if(ya < 0.0F && other.minY >= this.maxY) {
					max = this.maxY - other.minY + this.epsilon;
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

	public float clipZCollide(AABB other, float za) {
		if(other.maxX > this.minX && other.minX < this.maxX) {
			if(other.maxY > this.minY && other.minY < this.maxY) {
				float max;
				if(za > 0.0F && other.maxZ <= this.minZ) {
					max = this.minZ - other.maxZ - this.epsilon;
					if(max < za) {
						za = max;
					}
				}

				if(za < 0.0F && other.minZ >= this.maxZ) {
					max = this.maxZ - other.minZ + this.epsilon;
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
	
	@Override
	public String toString() {
		return "AABB["+minX +", " + minY + ", " + minZ + ", " + maxX + ", " + maxY + ", " + maxZ + "]"; 
	}
}
