package net.oikmo.network.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.network.client.OtherPlayer;
import net.oikmo.network.shared.LoginRequest;
import net.oikmo.network.shared.LoginResponse;
import net.oikmo.network.shared.PacketAddPlayer;
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
import net.oikmo.toolbox.Maths;

public class MainServerListener extends Listener {

	public static Map<Integer, OtherPlayer> players = new HashMap<Integer, OtherPlayer>();

	public void connected(Connection connection) {
		OtherPlayer player = new OtherPlayer();
		player.c = connection;

		// add packet or init packet
		PacketAddPlayer addPacket = new PacketAddPlayer();
		addPacket.id = connection.getID();
		MainServer.server.sendToAllExceptTCP(connection.getID(), addPacket);

		for (OtherPlayer p : players.values()) {
			PacketAddPlayer addPacket2 = new PacketAddPlayer();
			addPacket2.id = p.c.getID();
			connection.sendTCP(addPacket2);
		}

		players.put(connection.getID(), player);
		
	}

	public void disconnected(Connection connection) {
		String username = "";
		if(players.get(connection.getID()) != null) {
			username = players.get(connection.getID()).userName;
			MainServer.removePlayer(username +" ("+ connection.getID() +")");
		}
		
		players.remove(connection.getID());
		PacketRemovePlayer removePacket = new PacketRemovePlayer();
		
		removePacket.id = connection.getID();
		removePacket.kick = false;
		removePacket.message = "";
		
		MainServer.server.sendToAllTCP(removePacket);
		MainServer.logPanel.append(username + " (ID="+connection.getID()+") left the server");
		MainServer.logPanel.append("\n");
	}

	public void received(Connection connection, Object object) {
		if(object instanceof LoginRequest) {
			LoginRequest request = (LoginRequest) object;
			LoginResponse response = new LoginResponse();
			response.setResponseText("ok");
			connection.sendTCP(response);
			
			PacketUserName packetUserName = new PacketUserName();
			packetUserName.id = connection.getID();
			packetUserName.userName = request.getUserName();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packetUserName);
			
			players.get(connection.getID()).userName = request.getUserName();
			
			MainServer.addPlayer(request.getUserName() +" ("+ connection.getID() +")");
			
			MainServer.logPanel.append(request.getUserName() + " (ID="+connection.getID()+") joined the server\n");
			
			PacketWorldJoin packetWorld = new PacketWorldJoin();
			packetWorld.seed = MainServer.theWorld.getSeed();
			
			packetWorld.x = MainServer.xSpawn;
			Vector3f spawn = new Vector3f(MainServer.xSpawn,0,MainServer.zSpawn);
			Vector3f chunkPos = new Vector3f();
			Maths.calculateChunkPosition(spawn, chunkPos);
			MasterChunk spawnChunk = MainServer.theWorld.getChunkFromPosition(MainServer.theWorld.getPosition(chunkPos));
			packetWorld.y = spawnChunk.getChunk().getHeightFromPosition(chunkPos, spawn);
			packetWorld.z = MainServer.zSpawn;
			connection.sendUDP(packetWorld);
			
			PacketTickPlayer packetDisable = new PacketTickPlayer();
			packetDisable.id = connection.getID();
			packetDisable.shouldTick = false;
			connection.sendUDP(packetDisable);
			
			for(Map.Entry<Vector3f, MasterChunk> entry : MainServer.theWorld.chunkMap.entrySet()) {
				MasterChunk master = entry.getValue();
				PacketUpdateChunk packet = new PacketUpdateChunk();
				packet.id = connection.getID();
				packet.x = master.getOrigin().x;
				packet.z = master.getOrigin().z;
				packet.add = true;
				
				try {
					packet.data = Maths.compressObject(master.getChunk().blocks);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				connection.sendUDP(packet);
			}
			MainServer.logPanel.append("Sent chunks to " + request.getUserName() +"!\n");
			
			PacketTickPlayer packetEnable = new PacketTickPlayer();
			packetEnable.id = connection.getID();
			packetEnable.shouldTick = true;
			connection.sendUDP(packetEnable);
			
			for (OtherPlayer p : players.values()) {
				PacketUserName packetUserName2 = new PacketUserName();
				packetUserName2.id = p.c.getID();
				packetUserName2.userName = p.userName;
				// connection.sendTCP(packetUserName2);
				connection.sendUDP(packetUserName2);
			}
		}

		// random number packet, sync'd across the entire network
		RandomNumber packetRandom = new RandomNumber();
		packetRandom.randomFloat = 0.254f;
		MainServer.server.sendToTCP(connection.getID(),packetRandom);

		if(object instanceof PacketUpdateX) {
			PacketUpdateX packet = (PacketUpdateX) object;
			if(players.get(connection.getID()) != null)
				players.get(connection.getID()).x = packet.x;

			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
		else if(object instanceof PacketUpdateY) {
			PacketUpdateY packet = (PacketUpdateY) object;
			if(players.get(connection.getID()) != null)
				players.get(connection.getID()).y = packet.y;

			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
		else if(object instanceof PacketUpdateZ) {
			PacketUpdateZ packet = (PacketUpdateZ) object;
			if(players.get(connection.getID()) != null)
				players.get(connection.getID()).z = packet.z;

			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
		else if(object instanceof PacketUpdateRotX) {
			PacketUpdateRotX packet = (PacketUpdateRotX) object;
			if(players.get(connection.getID()) != null)
				players.get(connection.getID()).rotX = packet.x;

			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
		else if(object instanceof PacketUpdateRotY) {
			PacketUpdateRotY packet = (PacketUpdateRotY) object;
			if(players.get(connection.getID()) != null)
				players.get(connection.getID()).rotY = packet.y;

			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
		else if(object instanceof PacketUpdateRotZ) {
			PacketUpdateRotZ packet = (PacketUpdateRotZ) object;
			if(players.get(connection.getID()) != null)
				players.get(connection.getID()).rotZ = packet.z;

			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
		else if(object instanceof PacketUpdateChunk) {
			PacketUpdateChunk packet = (PacketUpdateChunk) object;
			
			packet.id = connection.getID();
			
			byte[][][] blocks = new byte[1][1][1];
			try {
				blocks = (byte[][][])Maths.uncompressStream(packet.data);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			Vector3f chunkPos = new Vector3f(packet.x, 0, packet.z);
			
			if(packet.add) {
				MainServer.theWorld.addChunk(new MasterChunk(chunkPos, blocks));
			} else {
				MainServer.theWorld.getChunkFromPosition(MainServer.theWorld.getPosition(chunkPos)).replaceBlocks(blocks);
			}
			
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
	}
}