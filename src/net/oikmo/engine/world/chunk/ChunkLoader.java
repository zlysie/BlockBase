package net.oikmo.engine.world.chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.oikmo.engine.nbt.NBTTagCompound;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordHelper;
import net.oikmo.toolbox.CompressedStreamTools;

public class ChunkLoader {

	public ChunkLoader(File saveDir, boolean createIfNecessary) {
		this.saveDir = saveDir;
		this.createIfNecessary = createIfNecessary;
	}

	private File chunkFileForXZ(int x, int z) {
		String s = "c."+Integer.toString(x, 36)+"."+Integer.toString(z, 36)+".dat";
		String s1 = Integer.toString(x & 0x3f, 36);
		String s2 = Integer.toString(z & 0x3f, 36);
		File file = new File(saveDir, s1);
		if(!file.exists()) {
			if(createIfNecessary) {
				file.mkdir();
			} else {
				return null;
			}
		}
		file = new File(file, s2);
		if(!file.exists()) {
			if(createIfNecessary) {
				file.mkdir();
			} else {
				return null;
			}
		}
		file = new File(file, s);
		if(!file.exists() && !createIfNecessary) {
			return null;
		} else {
			return file;
		}
	}

	public MasterChunk loadChunk(int x, int z) {
		File file = chunkFileForXZ(x, z);
		if(file != null && file.exists()) {
			try {
				FileInputStream fileinputstream = new FileInputStream(file);
				NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
				if(!nbttagcompound.hasKey("Level")) {
					System.out.println((new StringBuilder()).append("Chunk file at ").append(x).append(",").append(z).append(" is missing level data, skipping").toString());
					return null;
				}
				if(!nbttagcompound.getCompoundTag("Level").hasKey("Blocks")) {
					System.out.println((new StringBuilder()).append("Chunk file at ").append(x).append(",").append(z).append(" is missing block data, skipping").toString());
					return null;
				}
				MasterChunk chunk = loadChunkIntoWorldFromCompound(nbttagcompound.getCompoundTag("Level"));
				if(!((int)chunk.getOrigin().x == x && (int)chunk.getOrigin().z == z)) {
					System.out.println("Chunk file at "+x+","+z+" is in the wrong location; relocating. (Expected "+x+", "+z+", got "+chunk.getOrigin()+")");
					nbttagcompound.setInteger("xPos", x);
					nbttagcompound.setInteger("zPos", z);
					chunk = loadChunkIntoWorldFromCompound(nbttagcompound.getCompoundTag("Level"));
				}
				return chunk;
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		}
		return null;
	}

	public void saveChunk(MasterChunk chunk) {
		//world.checkSessionLock();
		File file = chunkFileForXZ((int)chunk.getOrigin().x, (int)chunk.getOrigin().z);
		try {
			File file1 = new File(saveDir, "tmp_chunk.dat");
			FileOutputStream fileoutputstream = new FileOutputStream(file1);
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			nbttagcompound.setTag("Level", nbttagcompound1);
			storeChunkInCompound(chunk, nbttagcompound1);
			CompressedStreamTools.writeGzippedCompoundToOutputStream(nbttagcompound, fileoutputstream);
			fileoutputstream.close();
			if(file.exists()) {
				file.delete();
			}
			file1.renameTo(file);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void storeChunkInCompound(MasterChunk chunk, NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("xPos", (int)chunk.getOrigin().x);
		nbttagcompound.setInteger("zPos", (int)chunk.getOrigin().z);

		nbttagcompound.setByteArray("Blocks", chunk.getChunk().getByteArray());

		/*
        nbttagcompound.setLong("LastUpdate", world.worldTime);
        nbttagcompound.setByteArray("Data", chunk.data.data);
        nbttagcompound.setByteArray("SkyLight", chunk.skylightMap.data);
        nbttagcompound.setByteArray("BlockLight", chunk.blocklightMap.data);
        nbttagcompound.setByteArray("HeightMap", chunk.heightMap);
        nbttagcompound.setBoolean("TerrainPopulated", chunk.isTerrainPopulated);
        chunk.hasEntities = false;
        NBTTagList nbttaglist = new NBTTagList();
        for(int i = 0; i < chunk.entities.length; i++)
        {
            Iterator<?> iterator = chunk.entities[i].iterator();
            do
            {
                if(!iterator.hasNext())
                {
                    continue label0;
                }
                Entity entity = (Entity)iterator.next();
                chunk.hasEntities = true;
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                if(entity.func_358_c(nbttagcompound1))
                {
                    nbttaglist.setTag(nbttagcompound1);
                }
            } while(true);
        }

        nbttagcompound.setTag("Entities", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        NBTTagCompound nbttagcompound2;
        for(Iterator<?> iterator1 = chunk.chunkTileEntityMap.values().iterator(); iterator1.hasNext(); nbttaglist1.setTag(nbttagcompound2))
        {
            TileEntity tileentity = (TileEntity)iterator1.next();
            nbttagcompound2 = new NBTTagCompound();
            tileentity.writeToNBT(nbttagcompound2);
        }

        nbttagcompound.setTag("TileEntities", nbttaglist1);*/
	}

	public static MasterChunk loadChunkIntoWorldFromCompound(NBTTagCompound nbttagcompound) {
		int x = nbttagcompound.getInteger("xPos");
		int z = nbttagcompound.getInteger("zPos");
		
		MasterChunk chunk = new MasterChunk(ChunkCoordHelper.create(x,z), nbttagcompound.getByteArray("Blocks"));

		/*chunk.data = new NibbleArray(nbttagcompound.getByteArray("Data"));
        chunk.skylightMap = new NibbleArray(nbttagcompound.getByteArray("SkyLight"));
        chunk.blocklightMap = new NibbleArray(nbttagcompound.getByteArray("BlockLight"));
        chunk.heightMap = nbttagcompound.getByteArray("HeightMap");
        chunk.isTerrainPopulated = nbttagcompound.getBoolean("TerrainPopulated");
        if(!chunk.data.isValid())
        {
            chunk.data = new NibbleArray(chunk.blocks.length);
        }
        if(chunk.heightMap == null || !chunk.skylightMap.isValid())
        {
            chunk.heightMap = new byte[256];
            chunk.skylightMap = new NibbleArray(chunk.blocks.length);
            chunk.func_1024_c();
        }
        if(!chunk.blocklightMap.isValid())
        {
            chunk.blocklightMap = new NibbleArray(chunk.blocks.length);
            chunk.func_1014_a();
        }
        NBTTagList nbttaglist = nbttagcompound.getTagList("Entities");
        if(nbttaglist != null)
        {
            for(int k = 0; k < nbttaglist.tagCount(); k++)
            {
                NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(k);
                Entity entity = EntityList.createEntityFromNBT(nbttagcompound1, world);
                chunk.hasEntities = true;
                if(entity != null)
                {
                    chunk.addEntity(entity);
                }
            }

        }
        NBTTagList nbttaglist1 = nbttagcompound.getTagList("TileEntities");
        if(nbttaglist1 != null)
        {
            for(int l = 0; l < nbttaglist1.tagCount(); l++)
            {
                NBTTagCompound nbttagcompound2 = (NBTTagCompound)nbttaglist1.tagAt(l);
                TileEntity tileentity = TileEntity.createAndLoadEntity(nbttagcompound2);
                if(tileentity != null)
                {
                    chunk.func_1001_a(tileentity);
                }
            }

        }*/
		return chunk;
	}

	private File saveDir;
	private boolean createIfNecessary;
}
