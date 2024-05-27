package net.oikmo.network.server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;

import net.oikmo.engine.save.SaveSystem;
import net.oikmo.engine.save.WorldPositionData;
import net.oikmo.engine.world.World;
import net.oikmo.network.client.OtherPlayer;
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
import net.oikmo.toolbox.Logger.LogLevel;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.PTextField;
import net.oikmo.toolbox.os.EnumOS;
import net.oikmo.toolbox.os.EnumOSMappingHelper;

public class MainServer {

	private int tcpPort;
	private int udpPort;
	public static Server server;
	private Kryo kryo;
	public static float randomFloatNumber;

	public static JFrame window;
	public static JTextArea logPanel;
	private static PTextField commandInputPanel;
	public static JList<String> playersPanel;
	static MainServerListener listener = new MainServerListener();
	public static int xSpawn = 0,zSpawn = 0;

	private static String[] splashes;

	public static World theWorld;

	private static String version = "S0.1.0";
	public static final int NETWORK_PROTOCOL = 6;

	private static Thread saveThread;

	public MainServer(int tcpPort, int udpPort) {
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		server = new Server();

		kryo = server.getKryo();
		registerKryoClasses();
	}

	public void startServer() {
		if(!new File(MainServer.getWorkingDirectory()+"/saves/server-level/server-level.dat").exists()) {
			theWorld = new World();
			theWorld.initLevelLoader();
			theWorld.createChunkRadius(8);
			xSpawn = 0;
			zSpawn = 0;
			SaveSystem.saveWorldPosition("server-level", new WorldPositionData(xSpawn, zSpawn));
			logPanel.append("Created world at .blockbase-server/saves/server-level.dat!\n\n");
		} else {
			theWorld = World.loadWorld();
			WorldPositionData data = SaveSystem.loadWorldPosition("server-level");
			xSpawn = data.xSpawn;
			zSpawn = data.zSpawn;
			logPanel.append("Loaded world at .blockbase-server/saves/server-level.dat!\n\n");
		}

		Logger.log(LogLevel.INFO,"Starting Server");
		logPanel.append("Starting Server...\n");
		server.start();
		try {
			server.bind(tcpPort, udpPort);
			server.addListener(listener);
			logPanel.append("Server online! (PORT="+ tcpPort +")\n");
			logPanel.append("Don't forget to port forward 25555 for server info!\n");
			Logger.log(LogLevel.INFO, "Server online! (PORT="+ tcpPort +")");
			logPanel.append("----------------------------");
			logPanel.append("\n");
			saveThread = new Thread(new Runnable() {
				public void run() {
					while(true) {
						System.gc();
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						MainServer.theWorld.saveWorld();
						System.gc();
					}
				}
			});
			saveThread.setName("World Save Thread");
			saveThread.start();

		} catch (IOException e) {
			Logger.log(LogLevel.INFO,"Port already used");
			logPanel.append("Port already in use");
			logPanel.append("\n");
			e.printStackTrace();
		}
		
		new Thread(new Runnable() {

			public void run() {    
				try {
					ServerSocket pingSocket = new ServerSocket(25555);
					Socket socket = pingSocket.accept(); // Set up receive socket
					
					DataInputStream dIn = new DataInputStream(socket.getInputStream());
					OutputStream out = socket.getOutputStream();
					PrintWriter dOut = new PrintWriter(out, true);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					boolean done = false;
					while(!done) {
						byte messageType = (byte) dIn.read();
						
						switch(messageType) {
							case 1: // request for players
								dOut.println(MainServerListener.players.size());
								dOut.flush();
								break;
							case 2: //request forimage
								byte imgBytes[];
								File packpng = new File(MainServer.getWorkingDirectory() + "/server-icon.png");
								if(packpng.exists()) {
									BufferedImage bimg = ImageIO.read(packpng);
									if(bimg.getWidth() == 128 && bimg.getWidth() == bimg.getHeight()) {
										ImageIO.write(bimg,"PNG",baos);
										System.out.println("sending picture!");
									} else {
										bimg = ImageIO.read(MainServer.class.getResourceAsStream("/assets/pack.png"));
										ImageIO.write(bimg,"PNG",baos);
									}
								
								} else {
									BufferedImage bimg = ImageIO.read(MainServer.class.getResourceAsStream("/assets/pack.png"));
									ImageIO.write(bimg,"PNG",baos);
								}
								imgBytes = baos.toByteArray();
								baos.close();
								out.write((Integer.toString(imgBytes.length)).getBytes());
								out.write(imgBytes,0,imgBytes.length);
								dOut.flush();
								break;
							default:
								done = true;
						}
						
					}
					dOut.close();
					dIn.close();
					pingSocket.close();
					run();
				} catch(Exception e) {
					e.printStackTrace();
					run();
				}
			}

		}).start();
	}

	// Try changing this to non staic and see where this effects our game
	public static void stopServer() {
		Logger.log(LogLevel.INFO,"Server stopped");
		logPanel.append("Server stopped.");
		logPanel.append("\n");
		theWorld.saveWorld();
		for (OtherPlayer p : MainServerListener.players.values()) {
			PacketRemovePlayer packetDisconnect = new PacketRemovePlayer();
			packetDisconnect.id = p.c.getID();
			packetDisconnect.message = "Server closed";
			// connection.sendTCP(packetUserName2);
			p.c.sendUDP(packetDisconnect);
		}
		SaveSystem.saveWorldPosition("server-level", new WorldPositionData(xSpawn, zSpawn));
		server.stop();
		Logger.saveLog();
	}

	private void registerKryoClasses() {
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

	public static void createServerInterface() {
		splashes = Maths.fileToArray("splashes.txt");
		window = new JFrame("BlockBase Server Console");

		URL iconURL = MainServer.class.getResource("/assets/iconx32.png");
		ImageIcon icon = new ImageIcon(iconURL);
		window.setIconImage(icon.getImage());
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		window.add(panel);
		playersPanel = new JList<String>();
		logPanel = new JTextArea();
		DefaultCaret caret = (DefaultCaret)logPanel.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		logPanel.append("<BlockBase " + version + ">\n");
		logPanel.append("\n");
		logPanel.append(getRandomSplash()+"\n");

		logPanel.setLineWrap(false);
		logPanel.setEditable(false);

		commandInputPanel = new PTextField("Insert command here...");
		commandInputPanel.setToolTipText("hi! :D");
		commandInputPanel.setMaximumSize(new Dimension(300, (int) commandInputPanel.getPreferredSize().getHeight()));

		window.setResizable(true);
		window.setSize(400, 300);
		window.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent evt) {
				commandInputPanel.setMaximumSize(new Dimension(window.getSize().width, (int) commandInputPanel.getPreferredSize().getHeight()));
			}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//int i = JOptionPane.showConfirmDialog(null, "You want to shut down the server?");
				stopServer();
				System.exit(0); // successful exit
			}
		});

		commandInputPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getID() == ActionEvent.ACTION_PERFORMED) {
					if(!e.getActionCommand().contentEquals("")) {
						handleCommand(e.getActionCommand());
					}
					commandInputPanel.setText("");
				}
			}
		});


		JScrollPane scrollPane = new JScrollPane(logPanel);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		panel.add(scrollPane);
		panel.add(commandInputPanel);
		DefaultListModel<String> listModel = new DefaultListModel<>();
		listModel.addElement("PLAYERS");
		playersPanel.setModel(listModel);

		panel.add(playersPanel);
	}

	public static String getRandomSplash() {
		return splashes[new Random().nextInt(splashes.length)];
	}

	public static void refreshList() {
		DefaultListModel<String> listModel = new DefaultListModel<>();
		listModel.addElement("PLAYERS");
		for(Map.Entry<Integer, OtherPlayer> entry : MainServerListener.players.entrySet()) {
			listModel.addElement(entry.getValue().userName + " ("+entry.getKey()+")");
		}
		playersPanel.setModel(listModel);
	}

	private static void handleCommand(String cmd) {
		String command = cmd.replace("/", "");

		if(command.contentEquals("help")) {
			logPanel.append("\n");
			logPanel.append("setSpawn - sets spawn location of server - (setSpawn <x> <z>)\n");
			logPanel.append("seed - returns the seed of the world - (seed)\n");
			logPanel.append("save - saves the world - (save)\n");
			logPanel.append("kick - kicks a player from their ip - (kick <id> <reason>) or (kick <playerName> <reason>)\n");
			logPanel.append("chunks - returns total chunk size of world - (chunks)\n");
		} else if(command.startsWith("setSpawn ")) {
			String[] split = cmd.split(" ");
			boolean continueToDoStuff = true;
			String toX = null;
			String toZ = null;

			int tempX = Integer.MIN_VALUE;
			int tempZ = Integer.MIN_VALUE;
			try {
				toX = split[1];
				toZ = split[2];
			} catch(ArrayIndexOutOfBoundsException e) {
				continueToDoStuff = false;
			}

			try {
				tempX = Integer.valueOf(toX);
				tempZ = Integer.valueOf(toZ);
			} catch(NumberFormatException e) {
				continueToDoStuff = false;
			}

			if(continueToDoStuff) {
				xSpawn = tempX;
				zSpawn = tempZ;
				SaveSystem.saveWorldPosition("server-level", new WorldPositionData(xSpawn, zSpawn));

				logPanel.append("Successfully set spawn position to: [X="+xSpawn+", Z="+zSpawn+"]!");
			} else {
				logPanel.append("Unable to set spawn position as inputted values were invalid.\n");
			}
		} else if(command.contentEquals("seed")) {
			logPanel.append("World seed is: " + theWorld.getSeed());
		} else if(command.contentEquals("save")) {
			theWorld.saveWorld();
			logPanel.append("Saved world!");
		} else if(command.startsWith("kick ")) {
			String[] split = cmd.split(" ");
			boolean continueToDoStuff = true;
			boolean noIDGiven = false;
			String toID = null;
			String message = null;

			int playerID = Integer.MIN_VALUE;
			try {
				toID = split[1];
				message = split[2];
			} catch(ArrayIndexOutOfBoundsException e) {
				continueToDoStuff = false;
			}

			try {
				playerID = Integer.valueOf(toID);
			} catch(NumberFormatException e) {
				noIDGiven = true;
			}

			if(message == null) {
				continueToDoStuff = false;
			}

			if(MainServerListener.players.get(playerID) == null) {
				continueToDoStuff = false;
			}

			continueToDoStuff = false;
			for(Map.Entry<Integer, OtherPlayer> entry : MainServerListener.players.entrySet()) {
				OtherPlayer p = entry.getValue();

				if(p.userName.contentEquals(toID)) {
					playerID = entry.getKey();
					continueToDoStuff = true;
				}
			}

			if(continueToDoStuff) {
				System.out.println("kick " + (noIDGiven ? toID : playerID) + " ");

				String reason = cmd.split("kick " + (noIDGiven ? toID : playerID) + " ")[1];

				PacketRemovePlayer packetKick = new PacketRemovePlayer();
				packetKick.id = playerID;
				packetKick.kick = true;
				packetKick.message = reason;
				MainServer.server.sendToAllTCP(packetKick);

				logPanel.append("Kicked " + (noIDGiven ? toID : playerID) + " from the server.\n");

			} else {
				logPanel.append("ID was not valid / Reason was not supplied\n");
			}
		} else if(cmd.contentEquals("chunks")) {
			logPanel.append("Server has a total chunk size of: " + theWorld.chunkMap.size() + "\n");
		} else if(command.startsWith("say ")) {
			String message = command.substring(4);
			PacketChatMessage packet = new PacketChatMessage();
			packet.message=" [SERVER] " + message;
			server.sendToAllUDP(packet);
			logPanel.append(packet.message+"\n");
		} else {
			logPanel.append("Command \""+ cmd + "\" was not recognized!\n");
		}
	}

	public static void main(String args[]) {
		Random rand = new Random();
		randomFloatNumber = rand.nextFloat();
		MainServer main = new MainServer(25565, 25565);
		createServerInterface();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		main.startServer();
	}


	/**
	 * Retrieves data directory of .blockbase/ using {@code Main.getAppDir(String)}
	 * @return Directory (File)
	 */
	public static File getWorkingDirectory() {
		return getWorkingDirectory("blockbase-server");
	}

	/**
	 * Uses {@code Main.getOS} to locate an APPDATA directory in the system.
	 * Then it creates a new directory based on the given name e.g <b>.name/</b>
	 * 
	 * @param name (String)
	 * @return Directory (File)
	 */
	public static File getWorkingDirectory(String name) {
		String userDir = System.getProperty("user.home", ".");
		File folder;
		switch(EnumOSMappingHelper.os[EnumOS.getOS().ordinal()]) {
		case 1:
		case 2:
			folder = new File(userDir, '.' + name + '/');
			break;
		case 3:
			String appdataLocation = System.getenv("APPDATA");
			if(appdataLocation != null) {
				folder = new File(appdataLocation, "." + name + '/');
			} else {
				folder = new File(userDir, '.' + name + '/');
			}
			break;
		case 4:
			folder = new File(userDir, "Library/Application Support/" + name);
			break;
		default:
			folder = new File(userDir, name + '/');
		}

		if(!folder.exists() && !folder.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + folder);
		} else {
			return folder;
		}
	}


} // end total class
