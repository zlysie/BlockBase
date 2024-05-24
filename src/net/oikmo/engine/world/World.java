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
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordHelper;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;
import net.oikmo.network.server.MainServer;
import net.oikmo.toolbox.FastMath;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.noise.OpenSimplexNoise;

public class World {

	public static final int WORLD_HEIGHT = 128;
	public static final int WORLD_SIZE = 4*8;

	public Map<ChunkCoordinates, MasterChunk> chunkMap = new HashMap<>();
	
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
	
	public void addChunk(ChunkCoordinates position) {
		MasterChunk m = new MasterChunk(noise, position);
		chunkMap.put(m.getOrigin(), m);
	}
	
	public void addChunk(MasterChunk m) {
		chunkMap.put(m.getOrigin(), m);
	}
	
	public MasterChunk createAndAddChunk(ChunkCoordinates chunkPos) {
		MasterChunk m = new MasterChunk(this.noise, chunkPos);
		chunkMap.put(m.getOrigin(), m);
		return m;
	}
	
	public boolean setBlock(Vector3f position, byte block) {
		ChunkCoordinates chunkPos = Maths.calculateChunkPosition(position);
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
	
	public MasterChunk getChunkFromPosition(ChunkCoordinates position) {
		return chunkMap.get(position);
	}
	
	public void saveWorld(String world) {
		List<ChunkSaveData> chunks = new ArrayList<>();
		
		for(Map.Entry<ChunkCoordinates, MasterChunk> entry : chunkMap.entrySet()) {
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
				w.addChunk(new MasterChunk(ChunkCoordHelper.create(s.x,s.z), s.blocks));
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
				
				ChunkCoordinates chunkPos = ChunkCoordHelper.create(chunkX, chunkZ);
				if(getChunkFromPosition(chunkPos) == null) {
					MasterChunk m = new MasterChunk(noise, chunkPos);
					addChunk(m);
				}
			}
		}
		saveWorld("server-level");
	}
	
	public long getSeed() {
		return seed;
	}
}
