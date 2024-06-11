package net.oikmo.main.gui;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.opengl.Texture;

import net.oikmo.engine.DisplayManager;
import net.oikmo.engine.InputManager;
import net.oikmo.engine.ResourceLoader;
import net.oikmo.engine.entity.Camera;
import net.oikmo.engine.gui.GuiScreen;
import net.oikmo.engine.models.CubeModel;
import net.oikmo.engine.models.PlayerModel;
import net.oikmo.engine.renderers.MasterRenderer;
import net.oikmo.engine.sound.SoundMaster;
import net.oikmo.engine.world.World;
import net.oikmo.main.GameSettings;
import net.oikmo.main.Main;
import net.oikmo.toolbox.Download;
import net.oikmo.toolbox.Maths;
import net.oikmo.toolbox.UnzipUtility;
import net.oikmo.toolbox.properties.OptionsHandler;

public class GuiComponentLoader extends GuiScreen {

	private String password;
	
	public GuiComponentLoader(String password) {
		super("Component Loader");
		this.password = password;
	}

	private Texture icon;
	private Camera camera;

	private Thread workingThread;

	private int step = 0;
	private int steps = 10;

	private String working = "";
	private boolean runOnce = false;
	private int timer = 0;

	private int size = 128 + 32;
	private int progressSize = size * 2;
	private int offset = 4;

	private boolean skinStatusCheck = false;
	private String skinStatus = "ONLINE";

	public boolean isDone = false;

	private String jcode = "";

	public void onInit() {
		icon = ResourceLoader.loadUITexture("icon");
		camera = new Camera(new Vector3f(), new Vector3f());
		SoundMaster.soundSystem.backgroundMusic("loading", Main.class.getResource("/assets/gu-l_menu.ogg"), "gu-l_menu.ogg", true);
		Keyboard.enableRepeatEvents(false);
	}

	public void onTick() {
		timer++;
	}

	public void onUpdate() {
		if(DisplayManager.keyboardHasNext()) {
			char c = Keyboard.getEventCharacter();

			if(Character.isAlphabetic(c)) {
				jcode += c;
				Main.jmode = jcode.contains("jerma");
			}
		}

		MasterRenderer.getInstance().render(camera);

		int x = Display.getWidth()/2;
		int y = Display.getHeight()/2;

		//background
		this.drawSquareFilled(Color.white, 0, 0, Display.getWidth(), Display.getHeight());
		//icon
		this.drawTexture(icon, x, y-70, size, size);
		//progress bar
		this.drawSquareFilled(Color.red, x-(progressSize/2), y+70, ((float)progressSize/(float)steps)*step, 22);
		this.drawSquare(Color.red, 4, x-offset-(progressSize/2), y+70-offset, progressSize+offset*2, 22+offset*2);
		//status text
		this.drawStringCentered(Color.black, x, y+74+fontSize*3, working);

		if(skinStatusCheck) {
			this.drawString(Color.black, 0, 0, skinStatus);
			if(Main.jmode) {
				this.drawString(Color.black, 0, 0+fontSize*2-fontSize/2, "JERMA MODE");
			}
		} else {
			if(Main.jmode) {
				this.drawString(Color.black, 0, 0, "JERMA MODE");
			}
		}
		
		if(!runOnce && timer >= 120) {
			initialise();
			runOnce = true;
		}
		
		if(isDone) {
			if(this.workingThread != null) {
				this.workingThread.interrupt();
				this.workingThread = null;
			}

			if(timer >= 60) {
				MasterRenderer.getInstance().setupSkybox();
				prepareCleanUp();
				SoundMaster.soundSystem.stop("loading");
				Main.currentScreen = new GuiMainMenu(Main.splashText);
			}
		}
	}


	public void initialise() {
		workingThread = new Thread(new Runnable() {
			public void run() {
				working = "Downloading resources...";
				try {
					downloadResources();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				step++;
				
				working = "Checking profile...";
				try {
					if(Main.playerName != null && password != null) {
						System.out.println(Main.playerName);
						String urlString = String.format("http://blockbase.gurdit.com/api.php?username=%s&password=%s", Main.playerName, password);
						URL url = new URL(urlString);
						URLConnection conn = url.openConnection();
						InputStream is;
						is = conn.getInputStream();
						String content = IOUtils.toString(is, StandardCharsets.UTF_8);
						
						if(content.contains("\"result\":false")) {
							Main.disableNetworking = true;
							Main.playerName = null;
							skinStatus = "OFFLINE";
						}
					} else {
						Main.disableNetworking = true;
						skinStatus = "OFFLINE";
					}
				} catch(MalformedURLException e) {} catch (IOException e) {}
				
				
				step++;
				working = "Setting up models...";
				CubeModel.setup();
				PlayerModel.setup();
				step++;
				working = "Setting up InputManager...";
				Main.im = new InputManager();
				step++;

				working = "Retriving player skin...";
				BufferedImage image = null;
				try {
					URL url = new URL("http://blockbase.gurdit.com/users/"+Main.playerName+"/skin_"+ Main.playerName + ".png");
					URLConnection conn = url.openConnection();
					InputStream in = conn.getInputStream();
					image = ImageIO.read(in);
				} catch(Exception e) {}
				step++;

				ImageBuffer buf = new ImageBuffer(64,64);

				working = "Constructing player texture...";
				if(image != null) {
					try {
						for(int x = 0; x < 64; x++) {
							for(int y = 0; y < 64; y++) {
								java.awt.Color c = new java.awt.Color(image.getRGB(x, y), true);
								buf.setRGBA(x, y, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
							}
						}

						Main.playerSkinBuffer = buf;
						

					} catch(ArrayIndexOutOfBoundsException e) {}
				}
				skinStatusCheck = true;
				step++;

				working = "Loading splash texts...";
				if(!Main.jmode) {
					Main.splashes = Maths.fileToArray("splashes.txt");
				} else {
					Main.splashes = Maths.fileToArray("jermasplashes.txt");
				}
				step++;

				working = "Setting splash text...";
				Main.splashText = Main.getRandomSplash();
				step++;

				//Main.currentScreen = new GuiMainMenu(Main.splashText);

				working = "Setting configs...";
				try {
					GameSettings.globalVolume = Float.parseFloat(OptionsHandler.getInstance().translateKey("audio.volume"));
				} catch(NumberFormatException e) {
					OptionsHandler.getInstance().insertKey("audio.volume", GameSettings.globalVolume+"");
				}
				try {
					GameSettings.sensitivity = Float.parseFloat(OptionsHandler.getInstance().translateKey("input.sensitivity"));
				} catch(NumberFormatException e) {
					OptionsHandler.getInstance().insertKey("input.sensitivity", GameSettings.sensitivity+"");
				}
				try {
					World.updateRenderSize(Integer.parseInt(OptionsHandler.getInstance().translateKey("graphics.distance"))*2);
				} catch(NumberFormatException e) {
					OptionsHandler.getInstance().insertKey("graphics.distance", 2+"");
				}

				SoundMaster.setVolume();
				step++;
				isDone = true;
				timer = 0;
			}

		});
		this.workingThread.start();

	}

	@SuppressWarnings("resource")
	private void downloadResources() throws IOException {
		working = "Downloading resources...";
		File dir = Main.getResources();
		File tmp = new File(Main.getWorkingDirectory() + "/tmp");
		File musicDir = new File(Main.getResources() + "/music");
		File sfxDir = new File(Main.getResources() + "/sfx");

		step = 0;
		steps = 2;

		tmp.mkdir();
		working = "Checking if resources exists...";
		try {
			File versionTXT = new File(dir + "/resourcesVersion.txt");
			if (versionTXT.createNewFile()) {
				working = "Resources doesn't exist! Creating version txt!";
				System.out.println("resourcesVersion.txt is created");
				FileWriter txtOut = new FileWriter(dir + "/resourcesVersion.txt");

				txtOut.write(Integer.toString(Main.resourceVersion));
				txtOut.close();

				working = "Downloading resource version: " + Main.resourceVersion + "...";
				File zip = new File(tmp + "/resources.zip");
				download("https://oikmo.github.io/resources/blockbase/resources"+Main.resourceVersion+".zip", zip+"");
				step = 0;
				steps = 2;

				step++;

				UnzipUtility unzipper = new UnzipUtility();
				working = "Unzipping resources...";
				unzipper.unzip(tmp + "/resources.zip", dir+"");
				step++;

			} else {
				System.out.println("resourcesVersion.txt already exists.");

				BufferedReader brr = new BufferedReader(new FileReader(dir + "/resourcesVersion.txt"));     
				String temp = brr.readLine();
				if (temp == null) {
					FileWriter txtOut = new FileWriter(dir + "/resourcesVersion.txt");
					txtOut.write(Integer.toString(Main.resourceVersion));
					txtOut.close();
					brr = new BufferedReader(new FileReader(dir + "/resourcesVersion.txt"));     
					temp = brr.readLine();
				}
				if(!temp.contentEquals(Integer.toString(Main.resourceVersion))) {
					int tempInt = Integer.parseInt(temp.trim());
					if(tempInt < Main.resourceVersion || tempInt > Main.resourceVersion) {
						if(dir.exists()) {
							Files.walk(Paths.get(new File(dir+"/").getPath())).sorted(Comparator.reverseOrder()) .forEach(path -> {
								try {
									if(!path.toString().contains("custom")) {
										Files.delete(path);  //delete each file or directory
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							});
						}

						FileWriter txtOut = new FileWriter(Main.getResources() + "/resourcesVersion.txt");
						txtOut.write(Integer.toString(Main.resourceVersion));
						txtOut.close();
					}
				}
				brr.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("[ERROR] Version could NOT be verified!");
		}
		
		if(!musicDir.exists()) {
			musicDir.mkdirs();
		}
		if(!sfxDir.exists()) {
			sfxDir.mkdirs();
		}
		
		if(!(musicDir.list().length != 0 && sfxDir.list().length != 0)) {
			working = "Downloading resource version: " + Main.resourceVersion + "...";
			download("https://oikmo.github.io/resources/blockbase/resources"+Main.resourceVersion+".zip", tmp + "/resources.zip");
			step = 0;
			steps = 2;
			step++;
			UnzipUtility unzipper = new UnzipUtility();
			working = "Unzipping resources...";
			unzipper.unzip(tmp+"/resources.zip", Main.getResources()+"/");
			step++;
		}
		
		if(tmp.exists()) {
			new File(tmp + "/resources.zip").delete();
			tmp.delete();
		}
		step = 1;
		steps = 10;
	}

	private void download(String urlStr, String file) throws IOException {
		download(urlStr, new File(file));
	}

	private void download(String urlStr, File file) throws IOException {
		Download d = new Download(new URL(urlStr), file);
		System.out.println(d.getUrl());
		steps = 100;
		while(d.getProgress() < 100f) {
			step = (int)d.getProgress();
			working = "Downloading resources: " + step + "%";
		}
	}
}
