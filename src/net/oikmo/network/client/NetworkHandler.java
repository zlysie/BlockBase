package net.oikmo.network.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.lwjgl.util.vector.Vector3f;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.Main;
import net.oikmo.network.shared.LoginRequest;
import net.oikmo.network.shared.LoginResponse;
import net.oikmo.network.shared.Message;
import net.oikmo.network.shared.PacketAddPlayer;
import net.oikmo.network.shared.PacketGameOver;
import net.oikmo.network.shared.PacketRemovePlayer;
import net.oikmo.network.shared.PacketTickPlayer;
import net.oikmo.network.shared.PacketUpdateChunk;
import net.oikmo.network.shared.PacketUpdateRotX;
import net.oikmo.network.shared.PacketUpdateRotY;
import net.oikmo.network.shared.PacketUpdateRotZ;
import net.oikmo.network.shared.PacketUpdateX;
import net.oikmo.network.shared.PacketUpdateY;
import net.oikmo.network.shared.PacketUpdateZ;
import net.oikmo.network.shared.PacketUserName;
import net.oikmo.network.shared.PacketWorldJoin;
import net.oikmo.network.shared.RandomNumber;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;

public class NetworkHandler {
	
	public static float rand;

	public static JLabel label_login;
	public static JTextField login;
	public static JLabel label_password;
	public static JPasswordField password;

	public static JFrame frame;
	public Random random = new Random();
	
	private int tcpPort;
	private int udpPort;
	private int timeout;
	private String ip;
	
	public static Client client;
	private Kryo kryo;
	
	private OtherPlayer player;
	public static Map<Integer, OtherPlayer> players = new HashMap<Integer, OtherPlayer>();
	
	private void registerKryoClasses() {		
		kryo.register(LoginRequest.class);
		kryo.register(LoginResponse.class);
		kryo.register(Message.class);
		kryo.register(OtherPlayer.class);
		kryo.register(float[].class);
		kryo.register(byte[].class);
		kryo.register(PacketUpdateX.class);
		kryo.register(PacketUpdateY.class);
		kryo.register(PacketUpdateZ.class);
		kryo.register(PacketUpdateRotX.class);
		kryo.register(PacketUpdateRotY.class);
		kryo.register(PacketUpdateRotZ.class);
		kryo.register(PacketWorldJoin.class);
		kryo.register(PacketUpdateChunk.class);
		kryo.register(PacketAddPlayer.class);
		kryo.register(PacketRemovePlayer.class);
		kryo.register(PacketUserName.class);
		kryo.register(RandomNumber.class);
		kryo.register(PacketGameOver.class);
		kryo.register(PacketTickPlayer.class);
	}
	
	public NetworkHandler(String ipAddress) throws Exception {
		this.ip = ipAddress;
		this.udpPort = 25565;
		this.tcpPort = 25565;
		this.timeout = 500000;
		player = new OtherPlayer();
		player.userName = "Player"+new Random().nextInt(256);

		client = new Client();
		kryo = client.getKryo();
		registerKryoClasses();
		connect(ip);
	}
	
	
	float degreesOffsetX = -90;
	public void update() {
		
		float x = player.x;
		float y = player.y;
		float z = player.z;
		float rotX = player.rotX;
		float rotY = player.rotY-degreesOffsetX;
		float rotZ = player.rotZ;
		Vector3f pos = Main.thePlayer.getPosition();
		Vector3f rot = Main.thePlayer.getCamera().getRotation();
		player.updatePosition(pos.x, pos.y, pos.z);
		player.updateRotation(rot.x, rot.y-degreesOffsetX, rot.z);
		
		if(x != player.x) {
			PacketUpdateX packetX = new PacketUpdateX();
			packetX.x = player.x;
			client.sendUDP(packetX);
		}
		
		if(y != player.y) {	
			PacketUpdateY packetY = new PacketUpdateY();
			packetY.y = player.y;
			client.sendUDP(packetY);
		}
		
		if(z != player.z) {
			PacketUpdateZ packetZ = new PacketUpdateZ();
			packetZ.z = player.z;
			client.sendUDP(packetZ);
		}
		
		if(rotX != player.rotX) {
			PacketUpdateRotX packetX = new PacketUpdateRotX();
			packetX.x = player.rotX;
			client.sendUDP(packetX);
		}
		if(rotY != player.rotY) {
			PacketUpdateRotY packetY = new PacketUpdateRotY();
			packetY.y = player.rotY;
			client.sendUDP(packetY);
		}
		if(rotZ != player.rotZ) {
			PacketUpdateRotZ packetZ = new PacketUpdateRotZ();
			packetZ.z = player.rotZ;
			client.sendUDP(packetZ);
		}
		
		
	} // end update

	public void updateChunk(MasterChunk master) {
		PacketUpdateChunk packet = new PacketUpdateChunk();
		
		packet.add = false;
		try {
			packet.data = Maths.compressObject(master.getChunk().blocks);
		} catch (IOException e) {}
		packet.x = master.getOrigin().x;
		packet.z = master.getOrigin().z;
		client.sendUDP(packet);
	}
	

	public void connect(String ip) throws Exception {
		Main.thePlayer.tick = false;
		Log.info("connecting...");
		client.start();
		client.connect(timeout, ip, tcpPort, udpPort);
		client.addListener(new PlayerClientListener());

		LoginRequest request = new LoginRequest();
		request.setUserName("Player" + new Random().nextInt(256));
		client.sendTCP(request);
		Log.info("Connected.");
	}
	
	public void disconnect() {
		Logger.log(LogLevel.INFO, "disconnecting...");
		client.stop();
	}

	
}
