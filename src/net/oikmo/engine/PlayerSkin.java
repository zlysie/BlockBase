package net.oikmo.engine;

import net.oikmo.engine.models.PlayerModel;
import net.oikmo.engine.models.TexturedModel;
import net.oikmo.engine.textures.ModelTexture;

public class PlayerSkin {
	public static final PlayerSkin[] players = new PlayerSkin[3];
	public static final PlayerSkin none = new PlayerSkin(User.DEFAULT);
	public static final PlayerSkin oikmo = new PlayerSkin(User.OIKMO);
	public static final PlayerSkin dev = new PlayerSkin(User.DEV);
	
	public static enum User {
		DEFAULT,
		OIKMO,
		DEV
	};
	
	private User userType;
	private TexturedModel model;
	
	public PlayerSkin(User type) {
		players[type.ordinal()] = this;
		this.userType = type;
	}
	
	public void setModel(TexturedModel model) {
		this.model = model;
	}
	
	public TexturedModel getModel() { 
		return model; 
	}
	
	public static PlayerSkin getPlayerFromOrdinal(byte type) {
		return type != -1 ? players[type] : null;
	}
	
	public void setType(PlayerSkin.User type) {
		this.userType = type;
	}

	public byte getByteType() {
		return (byte)userType.ordinal();
	}
	
	public User getEnumType() {
		return userType;
	}
	
	public int getType() {
		return userType.ordinal();
	}
	
}