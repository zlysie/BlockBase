package net.oikmo.network.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import net.oikmo.engine.gui.ChatMessage;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiChat;
import net.oikmo.network.shared.LoginResponse;
import net.oikmo.network.shared.PacketAddPlayer;
import net.oikmo.network.shared.PacketChatMessage;
import net.oikmo.network.shared.PacketPlaySoundAt;
import net.oikmo.network.shared.PacketRemovePlayer;
import net.oikmo.network.shared.PacketTickPlayer;
import net.oikmo.network.shared.PacketUpdateChunk;
import net.oikmo.network.shared.PacketUpdateRotX;
import net.oikmo.network.shared.PacketUpdateRotY;
import net.oikmo.network.shared.PacketUpdateRotZ;
import net.oikmo.network.shared.PacketUpdateWithheldBlock;
import net.oikmo.network.shared.PacketUpdateX;
import net.oikmo.network.shared.PacketUpdateY;
import net.oikmo.network.shared.PacketUpdateZ;
import net.oikmo.network.shared.PacketUserName;
import net.oikmo.network.shared.PacketWorldJoin;
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;

public class PlayerClientListener extends Listener {

	public void received(Connection connection, Object object){
		if(object instanceof LoginResponse){
			LoginResponse response = (LoginResponse) object;
			if(response.getResponseText().equalsIgnoreCase("ok")){
				Logger.log(LogLevel.INFO,"Login Ok");
			} else {
				Main.network.disconnect();
				System.out.println(response.PROTOCOL + " " + NetworkHandler.NETWORK_PROTOCOL);
				if(response.PROTOCOL != NetworkHandler.NETWORK_PROTOCOL) {
					Main.disconnect(false, "Wrong protocol!");
				} else {
					Main.disconnect(false, "Login failed.");
				}

				Logger.log(LogLevel.WARN,"Login failed");
			}
		}

		if(object instanceof PacketAddPlayer){
			PacketAddPlayer packet = (PacketAddPlayer) object;

			OtherPlayer newPlayer = new OtherPlayer();
			System.out.println(Main.network);
			if(Main.network == null) {
				Main.disconnect(false, "Unknown");
			} else if(Main.network.players == null) {
				Main.network.players = new HashMap<>();
			}
			Main.network.players.put(packet.id, newPlayer);


		} 
		else if(object instanceof PacketRemovePlayer){
			PacketRemovePlayer packet = (PacketRemovePlayer) object;
			if(packet.id == Main.network.client.getID()) {
				Main.network.disconnect();
				Main.disconnect(packet.kick, packet.message);
			} else {
				if(Main.thePlayer != null) {
					if(Main.network.players.get(packet.id) != null && Main.network.players.get(packet.id).userName != null) {
						if(!Main.network.players.get(packet.id).userName.contentEquals(Main.network.player.userName)) {
							Main.network.rawMessages.add(new ChatMessage(Main.network.players.get(packet.id).userName + " left the game", true));
							if(Main.currentScreen instanceof GuiChat) {
								((GuiChat)Main.currentScreen).updateMessages();
							}
						}

					}
				}
				Main.network.players.remove(packet.id);

			}
		}
		else if(object instanceof PacketUserName){
			PacketUserName packet = (PacketUserName) object;
			if(Main.network == null) {
				Main.disconnect(false, "Unknown");
			} else if(Main.network.players == null) {
				Main.network.players = new HashMap<>();
			}
			for(Map.Entry<Integer, OtherPlayer> entry : Main.network.players.entrySet() ){
				if(entry.getKey() == packet.id){
					entry.getValue().userName = packet.userName;
					if(Main.thePlayer != null) {
						if(packet.userName != null) {
							if(!Main.network.players.get(packet.id).userName.contentEquals(Main.network.player.userName)) {
								Main.network.rawMessages.add(new ChatMessage(packet.userName + " joined the game", true));
								if(Main.currentScreen instanceof GuiChat) {
									((GuiChat)Main.currentScreen).updateMessages();
								}
							}

						}
					}
				}
			}
		}
		else if(object instanceof PacketUpdateX){
			PacketUpdateX packet = (PacketUpdateX) object;
			if(Main.network.players.get(packet.id) != null) {
				Main.network.players.get(packet.id).x = packet.x;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateY){
			PacketUpdateY packet = (PacketUpdateY) object;
			if(Main.network.players.get(packet.id) != null) {
				Main.network.players.get(packet.id).y = packet.y;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateZ){
			PacketUpdateZ packet = (PacketUpdateZ) object;
			if(Main.network.players.get(packet.id) != null) {
				Main.network.players.get(packet.id).z = packet.z;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateRotX){
			PacketUpdateRotX packet = (PacketUpdateRotX) object;
			if(Main.network.players.get(packet.id) != null) {
				Main.network.players.get(packet.id).rotX = packet.x;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateRotY){

			PacketUpdateRotY packet = (PacketUpdateRotY) object;
			if(Main.network.players.get(packet.id) != null) {
				Main.network.players.get(packet.id).rotY = packet.y;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateRotZ){
			PacketUpdateRotZ packet = (PacketUpdateRotZ) object;
			if(Main.network.players.get(packet.id) != null) {
				Main.network.players.get(packet.id).rotZ = packet.z; 
			} else {
				requestInfo(connection);
			}
		}
		else if(object instanceof PacketUpdateWithheldBlock) {
			PacketUpdateWithheldBlock packet = (PacketUpdateWithheldBlock) object;
			if(Main.network.players.get(packet.id) != null) {
				Main.network.players.get(packet.id).block = packet.block;
			} else {
				requestInfo(connection);
			}
		}
		else if(object instanceof PacketUpdateChunk) {
			PacketUpdateChunk packet = (PacketUpdateChunk) object;
			byte[][][] blocks = new byte[1][1][1];
			try {
				blocks = (byte[][][])Maths.uncompressStream(packet.data);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Vector3f chunkPos = new Vector3f(packet.x, 0, packet.z);

			if(packet.add) {
				Main.theWorld.addChunk(new MasterChunk(chunkPos, blocks));
			} else {
				if(Main.theWorld.getChunkFromPosition(chunkPos) != null) { 
					Main.theWorld.getChunkFromPosition(chunkPos).replaceBlocks(blocks);
				}
			}
		}
		else if(object instanceof PacketWorldJoin) {
			PacketWorldJoin packet = (PacketWorldJoin) object;
			Main.theWorld = new World(packet.seed);
			Main.thePlayer.setPos(packet.x,packet.y,packet.z);
			
			System.out.println("Server world seed:" + packet.seed);
		} 
		else if(object instanceof PacketTickPlayer) {
			PacketTickPlayer packet = (PacketTickPlayer) object;
			Main.thePlayer.tick = packet.shouldTick;
		}

		else if(object instanceof PacketPlaySoundAt) {
			PacketPlaySoundAt packet = (PacketPlaySoundAt) object;
			Block block = Block.getBlockFromOrdinal(packet.blockID);
			if(block != null) {
				if(packet.place) {
					SoundMaster.playBlockPlaceSFX(block, packet.x, packet.y, packet.z);
				} else {
					SoundMaster.playBlockBreakSFX(block, packet.x, packet.y, packet.z);
				}
			}
		}
		else if(object instanceof PacketChatMessage) {
			PacketChatMessage packet = (PacketChatMessage) object;
			Main.network.rawMessages.add(new ChatMessage(packet.message, false));
			if(Main.currentScreen instanceof GuiChat) {
				((GuiChat)Main.currentScreen).updateMessages();
			}
		}
	}

	private void requestInfo(Connection connection) {
		if(!Main.network.players.keySet().contains(connection.getID())) {
			Main.network.players.put(connection.getID(), new OtherPlayer());
		}
	}
}
