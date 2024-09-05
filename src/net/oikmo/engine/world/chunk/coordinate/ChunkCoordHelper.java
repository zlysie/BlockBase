package net.oikmo.engine.world.chunk.coordinate;

import java.util.ArrayList;
import java.util.List;

public class ChunkCoordHelper {
	private static List<ChunkCoordinates> coords = new ArrayList<>();
	
	public static ChunkCoordinates create(int x, int z) {
		ChunkCoordinates coord = new ChunkCoordinates(x, z);
		
		if(coords.contains(coord)) {
			if(coords.indexOf(coord) == -1) {
				coords.add(coord);
				return coord;
			}
			return coords.get(coords.indexOf(coord));
		} else {
			//System.gc();
			coords.add(coord);
			return coord;
		}
	}
	
	public static void cleanUp() {
		System.out.println("clean");
		coords.clear();
		System.gc();
	}
}
