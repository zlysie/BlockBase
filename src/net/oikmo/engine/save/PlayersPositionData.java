package net.oikmo.engine.save;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PlayersPositionData implements Serializable {
	private static final long serialVersionUID = 1L;
	public Map<String, PlayerPositionData> positions;
	
	public PlayersPositionData() {
		positions = new HashMap<>();
	}
}
