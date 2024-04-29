package net.oikmo.network.server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Random;

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
import net.oikmo.network.client.OtherPlayer;
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
	public static int xSpawn,zSpawn;
	
	private static String[] splashes;
	
	public static World theWorld;
	
	private static String version = "S0.0.3";
	public static final int NETWORK_PROTOCOL = 1;
	
	private static Thread saveThread;

	public MainServer(int tcpPort, int udpPort) {
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		server = new Server();

		kryo = server.getKryo();
		registerKryoClasses();
	}

	public void startServer() {
		if(SaveSystem.loadWorld("server-level") == null) {
			theWorld = new World();
			theWorld.createChunkRadius(8);
			xSpawn = 0;
			zSpawn = 0;
			SaveSystem.saveWorldPosition("server-level", new WorldPositionData(xSpawn, zSpawn));
			logPanel.append("Created world at .blockbase-server/saves/server-level.dat!\n\n");
		} else {
			theWorld = World.loadWorld("server-level");
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
						MainServer.theWorld.saveWorld("server-level");
						logPanel.append("[AUTO] Saving world!\n");
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
	}

	// Try changing this to non staic and see where this effects our game
	public static void stopServer() {
		Logger.log(LogLevel.INFO,"Server stopped");
		logPanel.append("Server stopped.");
		logPanel.append("\n");
		theWorld.saveWorld("server-level");
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
		kryo.register(PacketUpdateWithheldBlock.class);
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
			logPanel.append("kick - kicks a player from their ip - (kick <id> <reason>)\n");
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
				logPanel.append("Unable to set spawn position as inputted values were invalid.");
			}
		} else if(command.contentEquals("seed")) {
			logPanel.append("World seed is: " + theWorld.getSeed());
		} else if(command.contentEquals("save")) {
			theWorld.saveWorld("server-level");
			logPanel.append("Saved world!");
		} else if(command.startsWith("kick ")) {
			String[] split = cmd.split(" ");
			boolean continueToDoStuff = true;
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
				continueToDoStuff = false;
			}
			
			if(message == null) {
				continueToDoStuff = false;
			}
			
			if(MainServerListener.players.get(playerID) == null) {
				continueToDoStuff = false;
			}
			
			if(continueToDoStuff) {
				String reason = cmd.split("kick " + playerID + " ")[1];
				
				PacketRemovePlayer packetKick = new PacketRemovePlayer();
				packetKick.id = playerID;
				packetKick.kick = true;
				packetKick.message = reason;
				MainServer.server.sendToAllTCP(packetKick);
				
				logPanel.append("Kicked " + playerID + " from the server\n");
				logPanel.append("\n");
				
			} else {
				logPanel.append("ID was not valid / Reason was not supplied");
			}
		} else if(cmd.contentEquals("chunks")) {
			logPanel.append("Server has a total chunk size of" + theWorld.chunkMap.size() + "\n");
			logPanel.append("\n");
			
		} else {
			logPanel.append("Command \""+ cmd + "\" was not recognized!");
		} 
		logPanel.append("\n");
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
	public static File getDir() {
		return getAppDir("blockbase-server");
	}

	/**
	 * Uses {@code Main.getOS} to locate an APPDATA directory in the system.
	 * Then it creates a new directory based on the given name e.g <b>.name/</b>
	 * 
	 * @param name (String)
	 * @return Directory (File)
	 */
	public static File getAppDir(String name) {
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
