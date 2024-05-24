package net.oikmo.engine.world.chunk.coordinate;

public final class ChunkCoordinates {

	public final int x;
	public final int z;
	
	ChunkCoordinates(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public boolean equals(Object obj) {
		if(obj instanceof ChunkCoordinates) {
			ChunkCoordinates chunkcoordinates = (ChunkCoordinates)obj;
			return x == chunkcoordinates.x && z == chunkcoordinates.z;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return x << 16 ^ z;
	}
}
