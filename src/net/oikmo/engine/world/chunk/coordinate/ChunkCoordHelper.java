package net.oikmo.engine.world.chunk.coordinate;

import java.util.ArrayList;
import java.util.List;

public class ChunkCoordHelper {
	private static List<ChunkCoordinates> coords = new ArrayList<>();
	
	public static ChunkCoordinates create(int x, int z) {
		ChunkCoordinates coord = new ChunkCoordinates(x, z);
		
		if(coords.contains(coord)) {
			return coords.get(coords.indexOf(coord));
		} else {
			//System.gc();
			coords.add(coord);
			return coord;
		}
	}
}
