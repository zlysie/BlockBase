package net.oikmo.engine.save;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

public class PlayersPositionData implements Serializable {
	private static final long serialVersionUID = 1L;
	public Map<String, Vector3f> positions;
	
	public PlayersPositionData() {
		positions = new HashMap<>();
	}
}
