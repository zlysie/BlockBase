package net.oikmo.network.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.ImageBuffer;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import net.oikmo.engine.entity.Player;
import net.oikmo.engine.gui.ChatMessage;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.World;
import net.oikmo.engine.world.blocks.Block;
import net.oikmo.engine.world.chunk.MasterChunk;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordHelper;
import net.oikmo.engine.world.chunk.coordinate.ChunkCoordinates;
import net.oikmo.main.Main;
import net.oikmo.main.gui.GuiChat;
import net.oikmo.network.shared.LoginResponse;
import net.oikmo.network.shared.PacketAddPlayer;
import net.oikmo.network.shared.PacketChatMessage;
import net.oikmo.network.shared.PacketChunk;
import net.oikmo.network.shared.PacketModifyChunk;
import net.oikmo.network.shared.PacketPlaySoundAt;
import net.oikmo.network.shared.PacketRemovePlayer;
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
import net.oikmo.toolbox.Logger;
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;

public class PlayerClientListener extends Listener {

	public static World theWorld;
	
	public void received(Connection connection, Object object){
		//System.out.println(object);

		if(object instanceof LoginResponse){
			LoginResponse response = (LoginResponse) object;
			if(response.getResponseText().equalsIgnoreCase("ok")){
				Logger.log(LogLevel.INFO,"Login Ok");
			} else {
				Main.theNetwork.disconnect();
				System.out.println(response.PROTOCOL + " " + NetworkHandler.NETWORK_PROTOCOL);
				if(response.PROTOCOL != NetworkHandler.NETWORK_PROTOCOL) {
					Main.disconnect(false, Main.lang.translateKey("network.disconnect.p").replace("%s", ""+response.PROTOCOL));
				} else {
					Main.disconnect(false, Main.lang.translateKey("network.disconnect.l"));
				}

				Logger.log(LogLevel.WARN,"Login failed");
			}
		}

		if(object instanceof PacketAddPlayer){
			PacketAddPlayer packet = (PacketAddPlayer) object;

			OtherPlayer newPlayer = new OtherPlayer();
			System.out.println(Main.theNetwork + " network is?");
			if(Main.theNetwork == null) {
				Main.disconnect(false, Main.lang.translateKey("network.disconnect.g"));
			} else {
				if(!Main.theNetwork.players.containsKey(packet.id)) {
					Main.theNetwork.players.put(packet.id, newPlayer);
				}
			}
		}
		else if(object instanceof PacketRemovePlayer){
			PacketRemovePlayer packet = (PacketRemovePlayer) object;
			if(packet.id == Main.theNetwork.client.getID()) {
				Main.theNetwork.disconnect();
				Main.disconnect(packet.kick, packet.message);
			} else {
				if(Main.thePlayer != null) {
					if(Main.theNetwork.players.get(packet.id) != null && Main.theNetwork.players.get(packet.id).userName != null) {
						if(!Main.theNetwork.players.get(packet.id).userName.contentEquals(Main.theNetwork.player.userName)) {
							Main.theNetwork.rawMessages.add(new ChatMessage(Main.theNetwork.players.get(packet.id).userName + " left the game", true));
							if(Main.currentScreen instanceof GuiChat) {
								((GuiChat)Main.currentScreen).updateMessages();
							}
						}

					}
				}
				Main.theNetwork.players.remove(packet.id);

			}
		}
		else if(object instanceof PacketUserName){
			PacketUserName packet = (PacketUserName) object;
			if(Main.theNetwork == null) {
				Main.theNetwork.disconnect();
				Main.disconnect(false, Main.lang.translateKey("network.disconnect.u"));
			} else if(Main.theNetwork.players == null) {
				Main.theNetwork.players = new HashMap<>();
			}

			if(!Main.theNetwork.players.containsKey(packet.id) && packet.id != Main.theNetwork.client.getID()) {
				Main.theNetwork.players.put(packet.id, new OtherPlayer());
				OtherPlayer p = Main.theNetwork.players.get(packet.id);
				p.userName = packet.userName;
				
				BufferedImage image = null;
				try {
					URL url = new URL("http://blockbase.gurdit.com/users/"+packet.userName+"/skin_"+ packet.userName + ".png");
					URLConnection conn = url.openConnection();
					InputStream in = conn.getInputStream();
					image = ImageIO.read(in);
				} catch(Exception e) {}
				
				ImageBuffer buf = new ImageBuffer(64,64);
				
				if(image != null) {
					for(int x = 0; x < 64; x++) {
						for(int y = 0; y < 64; y++) {
							int rgba = image.getRGB(x, y);
							if((rgba & 0xff000000) == 0) {
								image.setRGB(x,y, (java.awt.Color.black.getRGB()));
							}
							java.awt.Color c = new java.awt.Color(image.getRGB(x, y), true);
							buf.setRGBA(x, y, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
						}
					}
					p.buffer = buf;
				}
				p.id = connection.getID();
				
				if(Main.thePlayer != null) {
					if(packet.userName != null) {
						if(!Main.theNetwork.players.get(packet.id).userName.contentEquals(Main.theNetwork.player.userName)) {
							Main.theNetwork.rawMessages.add(new ChatMessage(packet.userName + " joined the game", true));

							if(Main.currentScreen instanceof GuiChat) {
								((GuiChat)Main.currentScreen).updateMessages();
							}
							if(Main.theNetwork.players.get(packet.id).userName == null) {
								Main.theNetwork.players.get(packet.id).userName =  packet.userName;
								if(image != null) {
									p.buffer = buf;
								}
							}
						}

					}
				}
			} else if(Main.theNetwork.players.containsKey(packet.id)) {
				OtherPlayer p = Main.theNetwork.players.get(packet.id);
				p.userName = packet.userName;
				
				BufferedImage image = null;
				try {
					URL url = new URL("http://blockbase.gurdit.com/users/"+packet.userName+"/skin_"+ packet.userName + ".png");
					URLConnection conn = url.openConnection();
					InputStream in = conn.getInputStream();
					image = ImageIO.read(in);
				} catch(Exception e) {}
				
				ImageBuffer buf = new ImageBuffer(64,64);
				
				if(image != null) {
					for(int x = 0; x < 64; x++) {
						for(int y = 0; y < 64; y++) {
							int rgba = image.getRGB(x, y);
							if((rgba & 0xff000000) == 0) {
								image.setRGB(x,y, (java.awt.Color.black.getRGB()));
							}
							java.awt.Color c = new java.awt.Color(image.getRGB(x, y), true);
							buf.setRGBA(x, y, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
						}
					}
					p.buffer = buf;
				}
				
				if(Main.thePlayer != null) {
					if(packet.userName != null) {
						if(!Main.theNetwork.players.get(packet.id).userName.contentEquals(Main.theNetwork.player.userName)) {
							Main.theNetwork.rawMessages.add(new ChatMessage(packet.userName + " joined the game", true));
							if(Main.currentScreen instanceof GuiChat) {
								((GuiChat)Main.currentScreen).updateMessages();
							}
							if(Main.theNetwork.players.get(packet.id).userName == null) {
								Main.theNetwork.players.get(packet.id).userName =  packet.userName;	
								if(image != null) {
									p.buffer = buf;
								}
							}
						}
					}
				}
			}
		}
		else if(object instanceof PacketUpdateX){
			PacketUpdateX packet = (PacketUpdateX) object;
			if(Main.theNetwork.players.get(packet.id) != null) {
				Main.theNetwork.players.get(packet.id).x = packet.x;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateY){
			PacketUpdateY packet = (PacketUpdateY) object;
			if(Main.theNetwork.players.get(packet.id) != null) {
				Main.theNetwork.players.get(packet.id).y = packet.y;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateZ){
			PacketUpdateZ packet = (PacketUpdateZ) object;
			if(Main.theNetwork.players.get(packet.id) != null) {
				Main.theNetwork.players.get(packet.id).z = packet.z;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateRotX){
			PacketUpdateRotX packet = (PacketUpdateRotX) object;
			if(Main.theNetwork.players.get(packet.id) != null) {
				Main.theNetwork.players.get(packet.id).rotX = packet.x;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateRotY){
			
			PacketUpdateRotY packet = (PacketUpdateRotY) object;
			if(Main.theNetwork.players.get(packet.id) != null) {
				Main.theNetwork.players.get(packet.id).rotY = packet.y;
			} else {
				requestInfo(connection);
			}
		} 
		else if(object instanceof PacketUpdateRotZ){
			PacketUpdateRotZ packet = (PacketUpdateRotZ) object;
			if(Main.theNetwork.players.get(packet.id) != null) {
				Main.theNetwork.players.get(packet.id).rotZ = packet.z; 
			} else {
				requestInfo(connection);
			}
		}
		else if(object instanceof PacketUpdateWithheldBlock) {
			PacketUpdateWithheldBlock packet = (PacketUpdateWithheldBlock) object;
			if(Main.theNetwork.players.get(packet.id) != null) {
				Main.theNetwork.players.get(packet.id).block = packet.block;
			} else {
				requestInfo(connection);
			}
		}
		else if(object instanceof PacketChunk) {
			PacketChunk packet = (PacketChunk) object;

			byte[] blocks = new byte[1];
			try {
				blocks = (byte[])Maths.uncompressStream(packet.data);
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			ChunkCoordinates chunkPos = ChunkCoordHelper.create(packet.x, packet.z);
			
			theWorld.addChunk(new MasterChunk(chunkPos, blocks));
		}
		else if(object instanceof PacketModifyChunk) {
			PacketModifyChunk packet = (PacketModifyChunk) object;
			Vector3f blockPos = new Vector3f(packet.x,packet.y,packet.z);
			Block block = Block.getBlockFromOrdinal(packet.block);
			
			System.out.println(blockPos + " " + block);
			
			if(packet.refresh) {
				theWorld.setBlockNoNet(blockPos, block);
			} else {
				theWorld.setBlockNoUpdateNoNet(blockPos, block);
			}
		}
		else if(object instanceof PacketWorldJoin) {
			PacketWorldJoin packet = (PacketWorldJoin) object;
			theWorld = new World(packet.seed);
			Main.theWorld = theWorld;
			if(Main.thePlayer == null) {
				Main.thePlayer = new Player(new Vector3f(0,120,0),new Vector3f(0,0,0));
			}
			Main.thePlayer.setPos(packet.x,packet.y,packet.z);
			
			Main.thePlayer.getCamera().setRotation(packet.rotX, packet.rotY, packet.rotZ);
			
			System.out.println("Server world seed:" + packet.seed + " pos:" + packet.x + " " + packet.y + " " + packet.z);
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
			} else {
				if(packet.sfxID != null) {
					SoundMaster.playSFX(packet.sfxID);
				}
			}
		}
		else if(object instanceof PacketChatMessage) {
			PacketChatMessage packet = (PacketChatMessage) object;
			Main.theNetwork.rawMessages.add(new ChatMessage(packet.message, false));
			if(!packet.message.startsWith(" [SERVER]")) {
				if(Main.theNetwork.players.get(packet.id).userName == null) {
					Main.theNetwork.players.get(packet.id).userName =  packet.message.split(">")[0].replace("<", "").replace(">","").trim();
				}
			}
			
			if(Main.currentScreen instanceof GuiChat) {
				((GuiChat)Main.currentScreen).updateMessages();
			}
		}
	}

	private void requestInfo(Connection connection) {
		if(!Main.theNetwork.players.keySet().contains(connection.getID())) {
			//Main.theNetwork.players.put(connection.getID(), new OtherPlayer());
		}
	}
}
