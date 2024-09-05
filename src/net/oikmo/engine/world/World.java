package net.oikmo.engine.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.nbt.NBTTagCompound;
import net.oikmo.engine.world.chunk.ChunkLoader;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordHelper;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;
import net.oikmo.network.server.MainServer;
import net.oikmo.toolbox.CompressedStreamTools;
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
	
	private File worldDir;
	private ChunkLoader provider;
	
	private long lockTimestamp;
	
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
	
	public void initLevelLoader() {
		String world = "server-level";
		lockTimestamp = System.currentTimeMillis();
		worldDir = new File(MainServer.getWorkingDirectory()+"/saves/"+world+"/");
		System.out.println(worldDir);
		worldDir.mkdirs();
		try {
			File file1 = new File(worldDir, "session.lock");
			DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));
			try {
				dataoutputstream.writeLong(lockTimestamp);
			} finally {
				dataoutputstream.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to check session lock, aborting");
		}
		File file2 = new File(worldDir, "level.dat");
		boolean isNewWorld = !file2.exists();
		System.out.println(isNewWorld);
		if(file2.exists()) {
			try {
				NBTTagCompound baseTag = CompressedStreamTools.readCompressed(new FileInputStream(file2));
				NBTTagCompound nbttagcompound1 = baseTag.getCompoundTag("Data");
				this.seed = nbttagcompound1.getLong("Seed");
				this.noise = new OpenSimplexNoise(this.seed);
				
				//worldTime = nbttagcompound1.getLong("Time");
			} catch(Exception exception1) {
				exception1.printStackTrace();
			}
		}
		provider = new ChunkLoader(worldDir, !isNewWorld);
	}
	
	private void saveLevel() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setLong("Seed", seed);
		
		//nbttagcompound.setLong("Time", worldTime);*/
		nbttagcompound.setLong("LastPlayed", System.currentTimeMillis());
		/*EntityPlayer entityplayer = null;
		if(playerEntities.size() > 0)
		{
			entityplayer = (EntityPlayer)playerEntities.get(0);
		}*/
		NBTTagCompound dataTag = new NBTTagCompound();
		dataTag.setTag("Data", nbttagcompound);
		try {
			File newLevel = new File(worldDir, "level.dat_new");
			File oldLevel = new File(worldDir, "level.dat_old");
			File currentLevel = new File(worldDir, "level.dat");
			CompressedStreamTools.writeGzippedCompoundToOutputStream(dataTag, new FileOutputStream(newLevel));
			if(oldLevel.exists()) {
				oldLevel.delete();
			}
			currentLevel.renameTo(oldLevel);
			if(currentLevel.exists()) {
				currentLevel.delete();
			}
			newLevel.renameTo(currentLevel);
			if(newLevel.exists()) {
				newLevel.delete();
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
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
	
	public void saveWorld() {
		MainServer.append("Saving world!\n");
		try {
			for(Map.Entry<ChunkCoordinates, MasterChunk> entry : chunkMap.entrySet()) {
				MasterChunk master = entry.getValue();

				provider.saveChunk(master);
			}
		} catch(Exception e) {}
		
		saveLevel();
	}
	
	public void saveWorldAndQuit() {
		saveWorld();
		quitWorld();
	}
	
	public void quitWorld() {
		this.chunkCreator.interrupt();
	}
	public static World loadWorld() {
		Logger.log(LogLevel.WARN, "Loading world!");

		World w = null;
		w = new World();
		w.initLevelLoader();
		
		return w;
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
		saveWorld();
	}
	
	public long getSeed() {
		return seed;
	}
}
