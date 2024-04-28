package net.oikmo.toolbox;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.chunk.Chunk;
import net.oikmo.main.Main;

public class Maths {

	private static Vector3f rxTable = new Vector3f(1,0,0);
	private static Vector3f ryTable = new Vector3f(0,1,0);
	private static Vector3f rzTable = new Vector3f(0,0,1);

	private static List<Vector3f> scaleTable = new ArrayList<>();

	private static Vector3f getScaleFromPool(float scale) {
		Vector3f result = null;
		if(scaleTable.size() != 0) {
			for(Vector3f vec : scaleTable) {
				if(vec.x == scale && vec.x == scale && vec.x == scale) {
					result = vec;
				}
			}
			if(result == null) {
				Vector3f scaleVec = new Vector3f(scale,scale,scale);
				scaleTable.add(scaleVec);
				return scaleVec;
			}
		} else {
			scaleTable.add(new Vector3f(scale, scale, scale));
			result =  scaleTable.get(0);
		}
		return result;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {	
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.x), rxTable, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), ryTable, matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), rzTable, matrix, matrix);
		Matrix4f.scale(getScaleFromPool(scale), matrix, matrix);
		return matrix;

	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();

		Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1,0,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0,1,0), matrix, matrix);
		Matrix4f.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0,0,1), matrix, matrix);
		Matrix4f.translate(new Vector3f(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z), matrix, matrix);

		return matrix;
	}
	
	public static void matrixToBuffer(Matrix4f m, FloatBuffer dest)
    {
        matrixToBuffer(m, 0, dest);
    }
    public static void matrixToBuffer(Matrix4f m, int offset, FloatBuffer dest)
    {
        dest.put(offset, m.m00);
        dest.put(offset + 1, m.m01);
        dest.put(offset + 2, m.m02);
        dest.put(offset + 3, m.m03);
        dest.put(offset + 4, m.m10);
        dest.put(offset + 5, m.m11);
        dest.put(offset + 6, m.m12);
        dest.put(offset + 7, m.m13);
        dest.put(offset + 8, m.m20);
        dest.put(offset + 9, m.m21);
        dest.put(offset + 10, m.m22);
        dest.put(offset + 11, m.m23);
        dest.put(offset + 12, m.m30);
        dest.put(offset + 13, m.m31);
        dest.put(offset + 14, m.m32);
        dest.put(offset + 15, m.m33);
    }

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

	public static void calculateChunkPosition(Vector3f input, Vector3f output) {
		if(input.x >= 0) {
			output.x = (int) (input.x / Chunk.CHUNK_SIZE)*16;
		} else {
			if(input.x >= -16) {
				output.x = (int)-1*16;
			} else {
				float x = Maths.roundFloat(input.x+1);
				output.x = (int) ((x / Chunk.CHUNK_SIZE)-1)*16;

			}
		}

		if(input.z >= 0) {
			output.z = (int) (input.z / Chunk.CHUNK_SIZE) * 16;
		} else {
			if(input.z >= -16) {
				output.z = (int)-1 * 16;
			} else {
				float z = Maths.roundFloat(input.z+1);
				output.z = (int) ((z / Chunk.CHUNK_SIZE)-1)*16;
			}
		}
	}

	public static boolean isVectorEqualTo(Vector3f one, Vector3f two) {
		return one.x == two.x && one.y == two.y && one.z == two.z;
	}

	/**
	 * Lerp, allows you to transition numbers<br><br>
	 * 
	 * {@code public static float lerp(float start, float end, float amount)} 
	 * 
	 * @param start - starting number to interpolate from <i>[float]</i>
	 * @param end - end number to interpolate to <i>[float]</i>
	 * @param amount - amount to interpolate to and from <i>[float]</i>
	 * 
	 * @return <b>result</b> <i>[float]</i>
	 * 
	 * @author <i>Oikmo</i>
	 */
	public static float lerp(float start, float end, float amount) {
		return start + (amount)* (end - start);
	}

	public static long getDurationOfOGG(URL file) {
		long length = -1;
		try {
			AudioFile audioFile = AudioFileIO.read(new File(file.toURI()));

			// Extract the length of the OGG file in milliseconds
			length = audioFile.getAudioHeader().getTrackLength()*1000;
		} catch (IOException | CannotReadException | TagException | InvalidAudioFrameException | ReadOnlyFileException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return length;
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
	
	public static float getWorldSize(String fileDir) {
		return new File(Main.getDir()+"/saves/"+fileDir+".dat").length();
	}
	
	public static String humanReadableByteCountBin(long bytes) {
	    long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
	    if (absB < 1024) {
	        return bytes + " B";
	    }
	    long value = absB;
	    CharacterIterator ci = new StringCharacterIterator("KMGTPE");
	    for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
	        value >>= 10;
	        ci.next();
	    }
	    value *= Long.signum(bytes);
	    return String.format("%.1f %ciB", value / 1024.0, ci.current());
	}
	
	public static byte[] compressObject(Object object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzipOut = new GZIPOutputStream(baos);;
		ObjectOutputStream oos = new ObjectOutputStream(gzipOut);
		oos.writeObject(object);
		oos.close();
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
