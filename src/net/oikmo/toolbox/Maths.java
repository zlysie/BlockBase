package net.oikmo.toolbox;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.world.World;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordHelper;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;

public class Maths {	
	public static int roundFloat(float number) {
		int rounded;
		if (number - (int) number >= 0.0 && number - (int) number < 1.0) {
			rounded = Math.round(number - 0.1f);
		} else {
			rounded = Math.round(number);
		}
		return rounded;
	}

	public static Vector3f roundVectorTo(Vector3f vector) {
		Vector3f vec = new Vector3f(vector);
		vec.x = Maths.roundFloat(vector.x);
		vec.y = Maths.roundFloat(vector.y);
		vec.z = Maths.roundFloat(vector.z);
		return vec;
	}

	public static void roundVector(Vector3f vector) {
		vector.x = Maths.roundFloat(vector.x);
		vector.y = Maths.roundFloat(vector.y);
		vector.z = Maths.roundFloat(vector.z);
	}

	public static void roundVector(Vector3f input, Vector3f output) {
		output.x = Maths.roundFloat(input.x);
		output.y = Maths.roundFloat(input.y);
		output.z = Maths.roundFloat(input.z);
	}

	/**
	 * Converts string to long via taking each character of the string and converting it into a number. Then that number is added to string to be parsed to {@link Long#valueOf(String)}
	 * @param name - {@link String}
	 * @return {@link Long}
	 */
	public static long getSeedFromName(String name) {
		String finalString = "";
		for(int i = 0; i < name.length(); i++) {
			char ch = (char) name.getBytes()[i];
			int pos = Math.abs(ch - 'a' + 1);
			finalString += pos;
		}

		return Long.valueOf(finalString);
	}

	public static boolean isWithinChunk(int localX, int localY, int localZ) {
		return localX >= 0 && localX < Chunk.CHUNK_SIZE &&
				localY >= 0 && localY < World.WORLD_HEIGHT-1 &&
				localZ >= 0 && localZ < Chunk.CHUNK_SIZE;
	}

	public static boolean isWithinChunk(int localX, int localZ) {
		return localX >= 0 && localX < Chunk.CHUNK_SIZE &&
				localZ >= 0 && localZ < Chunk.CHUNK_SIZE;
	}

	public static ChunkCoordinates calculateChunkPosition(Vector3f input) {
		int outx, outz;
		if(input.x >= 0) {
			outx = (int) (input.x / Chunk.CHUNK_SIZE)*16;
		} else {
			if(input.x >= -16) {
				outx = (int)-1*16;
			} else {
				float x = FastMath.round(input.x+1);
				outx = (int) ((x / Chunk.CHUNK_SIZE)-1)*16;

			}
		}

		if(input.z >= 0) {
			outz = (int) (input.z / Chunk.CHUNK_SIZE) * 16;
		} else {
			if(input.z >= -16) {
				outz = (int)-1 * 16;
			} else {
				float z = FastMath.round(input.z+1);
				outz = (int) ((z / Chunk.CHUNK_SIZE)-1)*16;
			}
		}
		
		return ChunkCoordHelper.create(outx, outz);
	}

	public static String[] fileToArray(String fileLoc) {
		try {
			List<String> listOfStrings = new ArrayList<String>();
			BufferedReader bf = new BufferedReader(new InputStreamReader(Maths.class.getResourceAsStream("/assets/" + fileLoc)));
			String line = bf.readLine();

			while (line != null) {
				listOfStrings.add(line);
				line = bf.readLine();
			}
			
			bf.close();
			return listOfStrings.toArray(new String[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static byte[] compressObject(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzipOut = new GZIPOutputStream(baos);;
		ObjectOutputStream oos = new ObjectOutputStream(gzipOut);
		if(object instanceof BufferedImage) {
			//oos.defaultWriteObject();
			ImageIO.write((BufferedImage)object, "png", oos); // png is lossless
		} else {
			oos.writeObject(object);
		}
		
		oos.close();
		
		//System.out.println(humanReadableByteCountBin(baos.size()));
		return baos.toByteArray();
	}
	
	public static Object uncompressStream(byte[] data) throws ClassNotFoundException, IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		GZIPInputStream gzipIn = new GZIPInputStream(bais);
		ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
		Object obj = objectIn.readObject();
		objectIn.close();
		
		return obj;
	}
}
