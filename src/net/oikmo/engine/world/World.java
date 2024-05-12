package net.oikmo.engine.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.save.ChunkSaveData;
import net.oikmo.engine.save.SaveData;
import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.network.server.MainServer;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class World {

	public static final int WORLD_HEIGHT = 128;
	public static final int WORLD_SIZE = 4*8;

	public Map<Vector3f, MasterChunk> chunkMap = new HashMap<>();
	public List<Vector3f> usedPositions = new ArrayList<>();
	
	private long seed;
	private OpenSimplexNoise noise;
	
	private Thread chunkCreator;
	
	public World(String seed) {
		init(Maths.getSeedFromName(seed));
	}
	public World(long seed) {
		init(seed);
	}
	public World() {
		init(new Random().nextLong());
	}
	
	private void init(long seed) {
		this.seed = seed;
		this.noise = new OpenSimplexNoise(this.seed);
	}
	
	public void addChunk(Vector3f position) {
		MasterChunk m = new MasterChunk(noise, position);
		usedPositions.add(m.getOrigin());
		chunkMap.put(m.getOrigin(), m);
	}
	
	public void addChunk(MasterChunk m) {
		usedPositions.add(m.getOrigin());
		chunkMap.put(m.getOrigin(), m);
	}
	
	public MasterChunk createAndAddChunk(Vector3f chunkPos) {
		MasterChunk m = new MasterChunk(this.noise, chunkPos);
		usedPositions.add(m.getOrigin());
		chunkMap.put(m.getOrigin(), m);
		return m;
	}
	
	public boolean setBlock(Vector3f position, byte block) {
		Vector3f chunkPos = new Vector3f();
		Maths.calculateChunkPosition(position, chunkPos);
		MasterChunk m = getChunkFromPosition(chunkPos);
		if(m != null) {
			m.setBlock(position, block);
			return true;
		}
		return false;
	}
	
	public boolean isInValidRange(Vector3f origin) {
		return isInValidRange(1, origin);
	}
	public boolean isInValidRange(int size, Vector3f origin) {
		int distX = (int) FastMath.abs((1 * size) - origin.x);
		int distZ = (int) FastMath.abs((1 * size) - origin.z);

		if((distX <= WORLD_SIZE) && (distZ <= WORLD_SIZE)) {
			return true;
		}

		return false;
	}
	
	public MasterChunk getChunkFromPosition(Vector3f position) {
		return chunkMap.get(getPosition(position));
	}
	public boolean isPositionUsed(Vector3f pos) {
		boolean result = false;
		synchronized(usedPositions) {
			result = usedPositions.contains(new Vector3f(pos.x, 0, pos.z));
		}
		return result;
	}
	public Vector3f getPosition(Vector3f pos) {
		Vector3f result = null;
		synchronized(usedPositions) {
			for(int i = 0; i < usedPositions.size(); i++) {
				if(usedPositions.get(i) != null) {
					if((int)usedPositions.get(i).x == (int)pos.x && (int)usedPositions.get(i).z == (int)pos.z) {
						result = usedPositions.get(i);
					}
				}
				
			}
		}
		return result;
	}

	public void saveWorld(String world) {
		List<ChunkSaveData> chunks = new ArrayList<>();
		
		for(Map.Entry<Vector3f, MasterChunk> entry : chunkMap.entrySet()) {
			MasterChunk master = entry.getValue();
			
			chunks.add(new ChunkSaveData(master.getOrigin(), master.getChunk().blocks));
		}

		SaveSystem.saveWorld(world, new SaveData(seed, chunks));
		chunks.clear();
	}
	@SuppressWarnings("deprecation")
	public void saveWorldAndQuit(String world) {
		saveWorld(world);
		
		this.chunkCreator.interrupt();
		this.chunkCreator.stop();
		MainServer.logPanel.append("Saving world!\n");
		System.exit(0);
	}
	public static World loadWorld(String world) {
		SaveData data = SaveSystem.loadWorld(world);
		if(data != null) {
			Logger.log(LogLevel.WARN, "Loading world!");
			
			World w = new World(data.seed);
			
			for(ChunkSaveData s : data.chunks) {
				w.addChunk(new MasterChunk(new Vector3f(s.x,0,s.z), s.blocks));
			}
			
			return w;
		} else {
			Logger.log(LogLevel.WARN, "World couldn't be loaded!");
			World w = new World();
			w.createChunkRadius(8);
			return w;
		}
	}
	
	public void createChunkRadius(int radius) {
		radius *= 8;
		for (int x = (int) (-radius) / 16; x < (radius) / 16; x++) {
			for (int z = (int) (-radius) / 16; z < (radius) / 16; z++) {
				int chunkX = x * 16;
				int chunkZ = z * 16;
				
				Vector3f chunkPos = new Vector3f(chunkX, 0, chunkZ);
				synchronized(usedPositions) {
					if(!isPositionUsed(chunkPos) && getChunkFromPosition(chunkPos) == null) {
						MasterChunk m = new MasterChunk(noise, chunkPos);
						addChunk(m);
					}
				}
			}
		}
		saveWorld("server-level");
	}
	
	public long getSeed() {
		return seed;
	}
}
