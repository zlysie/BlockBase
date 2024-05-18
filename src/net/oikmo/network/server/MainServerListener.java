package net.oikmo.network.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import net.oikmo.engine.save.PlayersPositionData;
import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.network.client.OtherPlayer;
import net.oikmo.network.shared.LoginRequest;
import net.oikmo.network.shared.LoginResponse;
import net.oikmo.network.shared.PacketAddPlayer;
import net.oikmo.network.shared.PacketChatMessage;
import net.oikmo.network.shared.PacketChunk;
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

		players.put(connection.getID(), player);
		
		for (OtherPlayer p : players.values()) {
			PacketAddPlayer addPacket2 = new PacketAddPlayer();
			addPacket2.id = p.c.getID();
			connection.sendTCP(addPacket2);
		}
	}

	public void disconnected(Connection connection) {
		String username = "";
		if(players.get(connection.getID()) != null) {
			username = players.get(connection.getID()).userName;
		}
		
		PacketRemovePlayer removePacket = new PacketRemovePlayer();
		
		removePacket.id = connection.getID();
		removePacket.kick = false;
		removePacket.message = "";
		
		players.remove(connection.getID());
		MainServer.refreshList();
		
		MainServer.server.sendToAllUDP(removePacket);
		if(username != null && !username.isEmpty()) {
			MainServer.logPanel.append(username + " (ID="+connection.getID()+") left the server\n");
		}
		
	}
	
	private long lastSentTime = System.currentTimeMillis();
	
	private Map<Long, PacketModifyChunk> toBeProcessed = new HashMap<>();
	
	public void received(Connection connection, Object object) {
		if(object.getClass().getName().contains("PacketModifyChunk")) {
			
			if(System.currentTimeMillis() - lastSentTime > 250) {
				System.out.println((System.currentTimeMillis() - lastSentTime) +" " + object);
				lastSentTime = System.currentTimeMillis();
				for(Long time : toBeProcessed.keySet()) {
					if(System.currentTimeMillis() - time > 250) {
						PacketModifyChunk packet = toBeProcessed.get(time);
						MainServer.theWorld.setBlock(new Vector3f(packet.x,packet.y,packet.z), packet.block);
						System.out.println(new Vector3f(packet.x,packet.y,packet.z) + " " + packet.block + " processed");
						MainServer.server.sendToAllUDP(packet);
						toBeProcessed.remove(time);
					}
				}
			} else {
				toBeProcessed.put(lastSentTime, (PacketModifyChunk)object);
			}
			
		}
		
		
		if(object instanceof LoginRequest) {
			LoginRequest request = (LoginRequest) object;
			LoginResponse response = new LoginResponse();
			
			if(request.PROTOCOL == MainServer.NETWORK_PROTOCOL) {
				response.PROTOCOL = MainServer.NETWORK_PROTOCOL;
				response.setResponseText("ok");
				connection.sendTCP(response);
				
				PacketUserName packetUserName = new PacketUserName();
				packetUserName.id = connection.getID();
				packetUserName.userName = request.getUserName();
				
				MainServer.server.sendToAllExceptUDP(connection.getID(), packetUserName);
				
				players.get(connection.getID()).userName = request.getUserName();
				
				MainServer.refreshList();
				
				if(request.getUserName() != null && !request.getUserName().isEmpty()) {
					MainServer.logPanel.append(request.getUserName() + " (ID="+connection.getID()+") joined the server\n");
				}
				
				
				PacketWorldJoin packetWorld = new PacketWorldJoin();
				packetWorld.seed = MainServer.theWorld.getSeed();
				
				String ip = connection.getRemoteAddressTCP().getHostString();
				PlayersPositionData data = SaveSystem.loadPlayerPositions();
				System.out.println(data + " data");
				
				if(request.getUserName().length() > 20) {
					response.PROTOCOL = -1;
					response.setResponseText("not ok!");
					connection.sendTCP(response);
				}
				
				if(data != null) {
					Vector3f playerPos = data.positions.get(ip);
					if(playerPos != null) {
						packetWorld.x = playerPos.x;
						packetWorld.y = playerPos.y;
						packetWorld.z = playerPos.z;
						System.out.println(data.positions.get(ip) + " putting data");
					} else {
						packetWorld.x = MainServer.xSpawn;
						Vector3f spawn = new Vector3f(MainServer.xSpawn,0,MainServer.zSpawn);
						Vector3f chunkPos = new Vector3f();
						Maths.calculateChunkPosition(spawn, chunkPos);
						MasterChunk spawnChunk = MainServer.theWorld.getChunkFromPosition(MainServer.theWorld.getPosition(chunkPos));
						packetWorld.y = spawnChunk.getChunk().getHeightFromPosition(chunkPos, spawn);
						packetWorld.z = MainServer.zSpawn;
					}
				} else {
					packetWorld.x = MainServer.xSpawn;
					Vector3f spawn = new Vector3f(MainServer.xSpawn,0,MainServer.zSpawn);
					Vector3f chunkPos = new Vector3f();
					Maths.calculateChunkPosition(spawn, chunkPos);
					MasterChunk spawnChunk = MainServer.theWorld.getChunkFromPosition(MainServer.theWorld.getPosition(chunkPos));
					packetWorld.y = spawnChunk.getChunk().getHeightFromPosition(chunkPos, spawn);
					packetWorld.z = MainServer.zSpawn;
				}
				
				
				connection.sendUDP(packetWorld);
				
				PacketTickPlayer packetDisable = new PacketTickPlayer();
				packetDisable.id = connection.getID();
				packetDisable.shouldTick = false;
				connection.sendUDP(packetDisable);
				
				for (OtherPlayer p : players.values()) {
					PacketUserName packetUserName2 = new PacketUserName();
					packetUserName2.id = p.c.getID();
					packetUserName2.userName = p.userName;
					System.out.println(p.userName + " " + p.c.getID());
					// connection.sendTCP(packetUserName2);
					connection.sendUDP(packetUserName2);
				}
			} else {
				response.PROTOCOL = -1;
				response.setResponseText("not ok!");
				connection.sendTCP(response);
				MainServer.logPanel.append("Player \" "+ request.getUserName() + " \" (ID=" + connection.getID() + ") could not join\nas they had a protocol version of " + request.PROTOCOL +" whilst server is " + MainServer.NETWORK_PROTOCOL + "\n");
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
		else if(object instanceof PacketChunk) {
			PacketChunk packet = (PacketChunk) object;
			
			packet.id = connection.getID();
			
			Vector3f chunkPos = new Vector3f(packet.x, 0, packet.z);
			
			MainServer.theWorld.addChunk(chunkPos);
		} else if(object instanceof PacketRequestChunk) {
			PacketRequestChunk packet = (PacketRequestChunk) object;
			Vector3f chunkPos = new Vector3f(packet.x,0,packet.z);
			MasterChunk master = MainServer.theWorld.getChunkFromPosition(MainServer.theWorld.getPosition(chunkPos));
			
			if(master == null) {
				master = MainServer.theWorld.createAndAddChunk(new Vector3f(packet.x, 0, packet.z));
				MainServer.logPanel.append("Creating new chunk at: [X=" + packet.x + ", Z="+packet.z+"]");
			}
			
			PacketChunk packetChunk = new PacketChunk();
			packetChunk.id = connection.getID();
			packetChunk.x = master.getOrigin().x;
			packetChunk.z = master.getOrigin().z;
			
			try {
				packetChunk.data = Maths.compressObject(master.getChunk().blocks);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			connection.sendUDP(packetChunk);
		} else if(object instanceof PacketUpdateWithheldBlock) {
			PacketUpdateWithheldBlock packet = (PacketUpdateWithheldBlock) object;
			if(players.get(connection.getID()) != null)
				players.get(connection.getID()).block = packet.block;

			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		} else if(object instanceof PacketModifyChunk) {
			
			PacketModifyChunk packet = (PacketModifyChunk) object;

			packet.id = connection.getID();
			
			MainServer.theWorld.setBlock(new Vector3f(packet.x,packet.y,packet.z), packet.block);
			System.out.println(new Vector3f(packet.x,packet.y,packet.z) + " " + packet.block);
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		} else if(object instanceof PacketSavePlayerPosition) {
			PacketSavePlayerPosition packet = (PacketSavePlayerPosition) object;
			String ip = connection.getRemoteAddressTCP().getHostString();
			PlayersPositionData data = SaveSystem.loadPlayerPositions();
			if(data == null) {
				data = new PlayersPositionData();
				
				data.positions.put(ip, new Vector3f(packet.x,packet.y,packet.z));
				SaveSystem.savePlayerPositions(data);
			} else {
				if(data.positions.get(ip) == null) {
					data.positions.put(ip, new Vector3f(packet.x,packet.y,packet.z));
				} else {
					data.positions.replace(ip, new Vector3f(packet.x,packet.y,packet.z));
					SaveSystem.savePlayerPositions(data);
				}
			}
			
		}
		else if(object instanceof PacketPlaySoundAt) {
			PacketPlaySoundAt packet = (PacketPlaySoundAt) object;
			packet.id = connection.getID();
			MainServer.server.sendToAllExceptUDP(connection.getID(), packet);
		}
		else if(object instanceof PacketChatMessage) {
			PacketChatMessage packet = (PacketChatMessage) object;
			packet.id = connection.getID();
			MainServer.logPanel.append(packet.message+"\n");
			MainServer.server.sendToAllExceptTCP(connection.getID(), packet);
		}
	}
}