package net.oikmo.engine.gui.component.slick;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.gui.Gui;
import net.oikmo.engine.gui.component.slick.button.GuiButton;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.main.gui.GuiServerList;
import net.oikmo.network.client.Server;
import net.oikmo.toolbox.Maths;

public class GuiServer extends Gui implements GuiComponent {
	
	private static Map<String, File> tempFiles = new HashMap<>();
	
	private Image trashClosed = new Image(ResourceLoader.loadUITexture("ui/trash")).getSubImage(0, 0, 16, 16);
	private Image trashOpen = new Image(ResourceLoader.loadUITexture("ui/trash")).getSubImage(17, 0, 16, 16);
	
	private GuiCommand command;

	private float x, y, width, height;
	private boolean lockButton = false;
	private boolean isHovering = false;
	
	private GuiButton trashCan;
	private Server server;
	
	private Thread textureThread;
	private Texture image;
	private File imageFile;
	private boolean useImage = false;
	
	private String playerCount = null;
	
	private int playerFontSize = (int) (Gui.fontSize/1.5f);
	private int serverFontSize = (int) (Gui.fontSize/1.25f);
	private UnicodeFont playersFontSize = this.calculateFont(playerFontSize);
	private UnicodeFont serversFontSize = this.calculateFont(serverFontSize);

	public GuiServer(float x, float y, float width, float height, Server server) {
		this.server = server;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		components.add(this);
		trashCan = new GuiButton(trashClosed, trashOpen, x+(width/2)-(height/2), y-(height/2)+(height/2), height-6, height-6, "");
		trashCan.setGuiCommand(new GuiCommand(){
			public void invoke() {
				GuiServerList.servers.remove(server);
				Gui.cleanUp();
			}
			
			public void update() {
				x = x+(width/2)-(height/2)-6;
				y = y-(height/2)+(height/2);
			}
		});
		refresh();
	}
	
	Socket socket;
	InputStream in;
	DataOutputStream dOut;
	BufferedReader br;
	public void refresh() {
		textureThread = new Thread(new Runnable() {
			public void run() {
				try {
					socket = new Socket(server.getIP(), 25555);
					InputStream in = socket.getInputStream();
					DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
					br = new BufferedReader(new InputStreamReader(in));
					// request image info
					dOut.writeByte(2); 
					dOut.flush(); // Send off the data
					
					byte[] b = new byte[30];
				    int len = in.read(b);

				    int filesize = Integer.parseInt(new String(b).substring(0, len));
					byte[] imgBytes = Maths.readExactly(in, filesize);
					ByteArrayInputStream bais = new ByteArrayInputStream(imgBytes);
					BufferedImage img = ImageIO.read(bais);
					
					ImageIO.write(img, "PNG", imageFile);
					
					
					dOut.writeByte(-1);
					dOut.flush(); // Send off the data
					
					socket.close();
					useImage = true;
					socket = null;
					imageFile.deleteOnExit();
				} catch (Exception e) {
					//StringWriter writer = new StringWriter();
					e.printStackTrace();
					//System.out.println(writer.toString() + " " + server.getIP());*/
					socket = null;
					useImage = true;
				}
				imageFile.deleteOnExit();
			}
		});
		
		if(playerCount == null) {
			playerCount = "Failed to get players...";
		}
		
		if(tempFiles.get(server.getIP()) == null)  {
			try {
				imageFile = File.createTempFile("tmp-", ".tmp");
				tempFiles.put(server.getIP(), imageFile);
				textureThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			
			imageFile = tempFiles.get(server.getIP());
			useImage = true;
			textureThread = null;
			System.gc();
		}
		
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(500);
					socket = new Socket(server.getIP(), 25555);
					InputStream in = socket.getInputStream();
					DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
					br = new BufferedReader(new InputStreamReader(in));
					
					// request players
					dOut.writeByte(1); 
					dOut.flush(); // Send off the data
					
					int playersOnline = Integer.parseInt(br.readLine());
					if(playersOnline == 0) {
						playerCount = "No players!";
					} else if(playersOnline == 1) {
						playerCount = "1 player";
					} else {
						playerCount = playersOnline + " players";
					}
					in.close();
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		
		imageFile.deleteOnExit();
		image = ResourceLoader.loadUITexture("pack");
	}
	
	private Color nothing = new Color(0,0,0,255/4);
	private Color highlight = new Color(0,0,0,255/2);
	private Color outline = new Color(255,255,255,255/2);
	
	public void tick(boolean delete, boolean shouldTicky) {
		
		if(useImage) {
			if(imageFile != null && imageFile.exists() && imageFile.length() != 0) {
				try {
					image = TextureLoader.getTexture("PNG", new FileInputStream(imageFile), Image.FILTER_NEAREST);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					@SuppressWarnings("resource")
					FileLock fl = new RandomAccessFile(imageFile, "rwd").getChannel().tryLock();
					fl.release();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					imageFile.deleteOnExit();
				}
				
			} else {
				image = ResourceLoader.loadUITexture("pack");
			}
			useImage = false;
		}
		
		float mouseX = Mouse.getX();
		float mouseY = Math.abs(Display.getHeight()-Mouse.getY());
		Color base = nothing;
		if(y + height/2 > mouseY && y-height/2 < mouseY && x + width/2 > mouseX && x-width/2 < mouseX) {
			base = highlight;
			isHovering = true;
			if(Mouse.isButtonDown(0) && shouldTicky) {
				if(command != null && !trashCan.isHovering()) {
					if(!lockButton) {
						if(!lockedRightNow && current != this) {
							lockedRightNow = true;
							current = this;
							command.invoke();
							SoundMaster.playSFX("ui.button.click");
						}
					}
				}
			} else {
				lockButton = false;
			}
		} else {
			isHovering = false;
			base = nothing;
			if(lockedRightNow && current == this) {
				lockedRightNow = false;
				current = null;

			}
		}
		
		
		this.drawSquareFilled(base, x-(width/2), y-(height/2), width, height);
		this.drawSquare(isHovering ? Color.white : outline, 1, x-(width/2), y-(height/2), width, height);
		if(!delete) {
			if(image != null) 
				this.drawTexture(image, x+(width/2)-(height/2), y-(height/2)+(height/2), height-6, height-6);
		} else{
			trashCan.tick();
		}
		//Color c = isHovering ? new Color(0.9f,0.9f,0.1f,1f) : Color.white;
		drawShadowStringCentered(Color.white, x-(width/2)+(font.getWidth(server.getName())/2)+5, y-(height/2)+(playerFontSize/2)+5, server.getName());
		drawShadowStringCentered(serversFontSize, Color.lightGray, x-(width/2)+(serversFontSize.getWidth(server.getIP())/2)+5, y-(height/2)+(serverFontSize/2)+10+serverFontSize, server.getIP());
		drawShadowStringCentered(playersFontSize, playerCount.contains("Failed") ? Color.red : Color.yellow, x-(width/2)+(playersFontSize.getWidth(playerCount)/2)+5, y+(height/2)-(playerFontSize/2), playerCount);
	}

	public void updateComponent() {
		command.update();
		this.x = command.getX();
		this.y = command.getY();
	}

	@Override
	public void onCleanUp() {
		if(textureThread != null) {
			textureThread.interrupt();
			textureThread = null;
		}
		
		imageFile = null;
		
		System.gc();
		components.remove(this);
	}

	public boolean isHovering() {
		return isHovering;
	}

	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void setGuiCommand(GuiCommand command) {
		this.command = command;
	}
	
	@Override
	public void tick() {}
}
