package net.oikmo.network.client;

import java.io.IOException;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import net.oikmo.engine.world.World;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.main.Main;
import net.oikmo.network.shared.LoginResponse;
import net.oikmo.network.shared.PacketAddPlayer;
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
			NetworkHandler.players.put(packet.id, newPlayer);
		} 
		else if(object instanceof PacketRemovePlayer){
			PacketRemovePlayer packet = (PacketRemovePlayer) object;
			if(packet.id == Main.network.client.getID()) {
				Main.network.disconnect();
				Main.disconnect(packet.kick, packet.message);
			} else {
				NetworkHandler.players.remove(packet.id);
			}
		} 
		else if(object instanceof PacketUpdateX){
			PacketUpdateX packet = (PacketUpdateX) object;
			NetworkHandler.players.get(packet.id).x = packet.x;
		} 
		else if(object instanceof PacketUpdateY){
			PacketUpdateY packet = (PacketUpdateY) object;
			NetworkHandler.players.get(packet.id).y = packet.y;
		} 
		else if(object instanceof PacketUpdateZ){
			PacketUpdateZ packet = (PacketUpdateZ) object;
			NetworkHandler.players.get(packet.id).z = packet.z;
		} 
		else if(object instanceof PacketUpdateRotX){
			PacketUpdateRotX packet = (PacketUpdateRotX) object;
			NetworkHandler.players.get(packet.id).rotX = packet.x;
		} 
		else if(object instanceof PacketUpdateRotY){
			
			PacketUpdateRotY packet = (PacketUpdateRotY) object;
			NetworkHandler.players.get(packet.id).rotY = packet.y;
			
		} 
		else if(object instanceof PacketUpdateRotZ){
			PacketUpdateRotZ packet = (PacketUpdateRotZ) object;
			NetworkHandler.players.get(packet.id).rotZ = packet.z;
		}
		else if(object instanceof PacketUserName){
			PacketUserName packet = (PacketUserName) object;
			for(Map.Entry<Integer, OtherPlayer> entry : NetworkHandler.players.entrySet() ){
				if(entry.getKey() == packet.id){
					entry.getValue().userName = packet.userName;
				}
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
				Main.theWorld.getChunkFromPosition(chunkPos).replaceBlocks(blocks);
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
		else if(object instanceof PacketUpdateWithheldBlock) {
			PacketUpdateWithheldBlock packet = (PacketUpdateWithheldBlock) object;
			NetworkHandler.players.get(packet.id).block = packet.block;
		}
	} // end received method
	
}
