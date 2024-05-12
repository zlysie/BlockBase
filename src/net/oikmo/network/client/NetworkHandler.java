package net.oikmo.network.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;

import org.lwjgl.util.vector.Vector3f;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

import net.oikmo.engine.InputManager;
import net.oikmo.engine.gui.ChatMessage;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.main.Main;
import net.oikmo.network.shared.LoginRequest;
import net.oikmo.network.shared.LoginResponse;
import net.oikmo.network.shared.Message;
import net.oikmo.network.shared.PacketAddPlayer;
import net.oikmo.network.shared.PacketChatMessage;
import net.oikmo.network.shared.PacketChunk;
import net.oikmo.network.shared.PacketGameOver;
import net.oikmo.network.shared.PacketModifyChunk;
import net.oikmo.network.shared.PacketPlaySoundAt;
import net.oikmo.network.shared.PacketRemovePlayer;
import net.oikmo.network.shared.PacketRequestChunk;
import net.oikmo.network.shared.PacketSavePlayerPosition;
import net.oikmo.network.shared.PacketTickPlayer;
import net.oikmo.network.shared.PacketUpdateRotX;
import net.oikmo.network.shared.PacketUpdateRotY;
import net.oikmo.network.shared.PacketUpdateRotZ;
import net.oikmo.network.shared.PacketUpdateWithheldBlock;
import net.oikmo.network.shared.PacketUpdateX;
import net.oikmo.network.shared.PacketUpdateY;
import net.oikmo.network.shared.PacketUpdateZ;
import net.oikmo.network.shared.PacketUserName;
import net.oikmo.network.shared.PacketWorldJoin;
import net.oikmo.network.shared.RandomNumber;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.Logger.LogLevel;

public class NetworkHandler {
	
	public static float rand;

	public static JFrame frame;
	public Random random = new Random();
	
	private int tcpPort;
	private int udpPort;
	private int timeout;
	private String ip;
	
	public Client client;
	private static Kryo kryo;
	
	public OtherPlayer player;
	public Map<Integer, OtherPlayer> players = new HashMap<Integer, OtherPlayer>();
	public List<ChatMessage> rawMessages = new ArrayList<>();
	public List<ChatMessage> currentlyShownMessages = new ArrayList<ChatMessage>();
	
	private int tickTimer = 0;
	public static final int NETWORK_PROTOCOL = 4;
	
	private static void registerKryoClasses() {
		kryo.register(LoginRequest.class);
		kryo.register(LoginResponse.class);
		kryo.register(Message.class);
		kryo.register(OtherPlayer.class);
		kryo.register(float[].class);
		kryo.register(byte[].class);
		kryo.register(PacketChunk.class);
		kryo.register(PacketRequestChunk.class);
		kryo.register(PacketModifyChunk.class);
		kryo.register(PacketUpdateX.class);
		kryo.register(PacketUpdateY.class);
		kryo.register(PacketUpdateZ.class);
		kryo.register(PacketUpdateRotX.class);
		kryo.register(PacketUpdateRotY.class);
		kryo.register(PacketUpdateRotZ.class);
		kryo.register(PacketWorldJoin.class);
		kryo.register(PacketAddPlayer.class);
		kryo.register(PacketRemovePlayer.class);
		kryo.register(PacketUserName.class);
		kryo.register(RandomNumber.class);
		kryo.register(PacketGameOver.class);
		kryo.register(PacketTickPlayer.class);
		kryo.register(PacketUpdateWithheldBlock.class);
		kryo.register(PacketSavePlayerPosition.class);
		kryo.register(PacketPlaySoundAt.class);
		kryo.register(PacketChatMessage.class);
	}
	
	public NetworkHandler(String ipAddress) throws Exception {
		this.ip = ipAddress;
		this.udpPort = 25565;
		this.tcpPort = 25565;
		this.timeout = 500000;
		players = new HashMap<Integer, OtherPlayer>();
		player = new OtherPlayer();
		String name = "Player"+new Random().nextInt(256);
		if(Main.playerName == null ) {
			Main.playerName = name;
		}
		player.userName = Main.playerName;
		currentlyShownMessages = new ArrayList<>();
		client = new Client();
		kryo = client.getKryo();
		registerKryoClasses();
		connect(ip);
	}
	
	public static void testNetwork(String ipAddress) throws Exception {
		String ip = ipAddress;
		int udpPort = 25565;
		int tcpPort = 25565;
		int timeout = 500000;
		Client client = new Client();
		kryo = client.getKryo();
		registerKryoClasses();
		
		Logger.log(LogLevel.INFO, "Test connecting...");
		client.start();
		client.connect(timeout, ip, tcpPort, udpPort);
		Logger.log(LogLevel.INFO, "Test connected!");
		Logger.log(LogLevel.INFO, "Test disconnecting...");
		client.stop();
		Logger.log(LogLevel.INFO, "Test disconnected.");
	}
	
	public void tick() {
		synchronized(currentlyShownMessages) {
			for(int i = 0; i < currentlyShownMessages.size(); i++) {
				currentlyShownMessages.get(i).tick();
			}
		}
			
		
		if(tickTimer <= 60) {
			tickTimer++;
		} else {
			tickTimer = 0;
			if(!InputManager.hasMouseMoved()) {
				Vector3f rot = Main.thePlayer.getCamera().getRotation();
				player.updateRotation(rot.x, rot.y-degreesOffsetX, rot.z);
				
				PacketUpdateRotX packetRotX = new PacketUpdateRotX();
				packetRotX.x = player.rotX;
				client.sendUDP(packetRotX);
				PacketUpdateRotY packetRotY = new PacketUpdateRotY();
				packetRotY.y = player.rotY;
				client.sendUDP(packetRotY);
				PacketUpdateRotZ packetRotZ = new PacketUpdateRotZ();
				packetRotZ.z = player.rotZ;
				client.sendUDP(packetRotZ);
			}
			if(!InputManager.isMoving()) {
				
				Vector3f pos = Main.thePlayer.getCamera().getPosition();
				player.updatePosition(pos.x, pos.y, pos.z);
				PacketUpdateX packetX = new PacketUpdateX();
				packetX.x = player.x;
				client.sendUDP(packetX);
				PacketUpdateY packetY = new PacketUpdateY();
				packetY.y = player.y;
				client.sendUDP(packetY);
				PacketUpdateZ packetZ = new PacketUpdateZ();
				packetZ.z = player.z;
				client.sendUDP(packetZ);
			}
			
		}
	}
	
	private float degreesOffsetX = -90;
	public void update() {
		if(!client.isConnected()) {
			System.out.println("Yeahhh");
		}
		if(!client.isConnected()) {
			this.disconnect();
			Main.disconnect(false, "Unknown (Wrong protocol?)");
			return;
		}
		if(Main.theWorld != null) {
			if(!Main.theWorld.isChunkThreadRunning()) {
				Main.theWorld.startChunkRetriever();
			}
		}
		
		if(player.c != null) {
			if(players.containsKey(player.c.getID())) {
				players.remove(player.c.getID());
			}
		}
		
		
		float x = player.x;
		float y = player.y;
		float z = player.z;
		float rotX = player.rotX;
		float rotY = player.rotY-degreesOffsetX;
		float rotZ = player.rotZ;
		Vector3f pos = Main.thePlayer.getCamera().getPosition();
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
	}

	
	Vector3f lastRecordedPosition = new Vector3f();
	byte lastBlock;
	public void updateChunk(Vector3f position, Block block, boolean refresh, String yeah) {
		System.out.println("really? " + yeah);
		
		byte b = block != null ? block.getByteType() : -1;
		
		if(!Maths.isVectorEqualTo(lastRecordedPosition, position) && b != lastBlock) {
			PacketModifyChunk packet = new PacketModifyChunk();
			packet.refresh = refresh;
			packet.x = (int) position.x;
			packet.y = (int) position.y;
			packet.z = (int) position.z;
			packet.block = b;
			client.sendTCP(packet);
		}
		
	}

	public void connect(String ip) throws Exception {
		Main.thePlayer.tick = false;
		Logger.log(LogLevel.INFO, "Connecting...");
		client.start();
		client.connect(timeout, ip, tcpPort, udpPort);
		client.addListener(new PlayerClientListener());
		players = new HashMap<Integer, OtherPlayer>();
		LoginRequest request = new LoginRequest();
		request.setUserName(player.userName);
		request.PROTOCOL = NetworkHandler.NETWORK_PROTOCOL;
		client.sendTCP(request);
		
		Logger.log(LogLevel.INFO, "Connected.");
	}
	
	public void disconnect()  {
		if(Main.thePlayer != null) {
			PacketSavePlayerPosition data = new PacketSavePlayerPosition();
			data.x = (int) Main.thePlayer.getPosition().x;
			data.y = (int) Main.thePlayer.getPosition().y+1;
			data.z = (int) Main.thePlayer.getPosition().z;
			client.sendTCP(data);
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.log(LogLevel.INFO, "Disconnecting...");
		client.stop();
		Logger.log(LogLevel.INFO, "Disconnected.");
	}
}
