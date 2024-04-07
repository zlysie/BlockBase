package net.oikmo.toolbox.error;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import net.oikmo.main.Main;
import net.oikmo.toolbox.Logger;

public class PanelCrashReport extends Panel {
	private static final long serialVersionUID = 1L;
	String report = "";
	String main = "";
	
	public PanelCrashReport(UnexpectedThrowable unexpectedthrowable) {
		this.set(unexpectedthrowable);
	}
	
	public void set(UnexpectedThrowable unexpectedthrowable) {
		setBackground(new Color(0x2e3444));
		setLayout(new BorderLayout());
		StringWriter stringwriter = new StringWriter();
		unexpectedthrowable.exception.printStackTrace(new PrintWriter(stringwriter));
		String error = stringwriter.toString();
		String graphicsInfo = "";
		try {
			log(1, "Generated " + new SimpleDateFormat().format(new Date()) + "");
			log(1, "");
			log(1, Main.gameVersion+"");
			log(1, "OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
			log(1, "Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
			log(1, "VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
			log(1, "LWJGL: " + Sys.getVersion() + "");
			
			graphicsInfo = GL11.glGetString(GL11.GL_VENDOR);
			
			try {
				log(1, "OpenGL: " + GL11.glGetString(GL11.GL_RENDERER) + " version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR));
			} catch(RuntimeException e) {
				log(1, "OpenGL : [MAY HAVE NOT STARTED SINCE ERROR OCCURED EXACTLY AT START]");
			}
		}
		catch(Throwable throwable) {
			log(1, "[failed to get system properties (" + throwable + ")]");
		}
		
		log(1, "");
		log(1, error);
		
		log(0, "");
		
		if(error.contains("Pixel format not accelerated")) {
			log(0, "Bad video card drivers!");
			log(0, "-----------------------");
			log(0, "");
			log(0, Main.gameName + " was unable to start because it failed to find an accelerated OpenGL mode.");
			log(0, "This can usually be fixed by updating the video card drivers.");
			if(graphicsInfo.toLowerCase().contains("nvidia")) {
				log(0, "");
				log(0, "You might be able to find drivers for your video card here:");
				log(0, "  http://www.nvidia.com/");
			} else
				if(graphicsInfo.toLowerCase().contains("ati")) {
					log(0, "");
					log(0, "You might be able to find drivers for your video card here:");
					log(0, "  http://www.amd.com/");
				}
		} else {
			log(0, "Unfortunately " + Main.gameName +" has crashed :P");
			log(0, "----------------------------\n");
			log(0, Main.gameName + " has stopped running because it encountered a problem. [an error]\n");
			log(0, "If you wish to report this, please copy this entire text and contact us or submit an issue on github.");
			log(0, "Describe what happened before this occured. (cuz you did it :]).");
		}
		
		log(0, "");
		log(0, "-------- BEGIN ERROR REPORT --------");
		logN(0, report);
		log(0, "--------- END ERROR REPORT ---------");
		
		TextArea textArea = new TextArea(main, 0, 0, 1);
		textArea.setFont(new Font("Monospaced", Font.BOLD, 11));
		add(new CanvasLogo(), "North");
		add(new CanvasCrashReport(80), "East");
		add(new CanvasCrashReport(80), "West");
		add(new CanvasLogo("SHUTUP", 1, (byte)85), "South");
		add(textArea, "Center");
		
		File crashLogs = new File(Main.getDir()+"/crash-logs");
		if(!crashLogs.exists()) {
			crashLogs.mkdir();
		}
		File logFile = new File(crashLogs +"/"+Logger.getCurrentTimeFile()+".log");
		try {
			logFile.createNewFile();
		} catch (IOException e) {}
		try {
			FileWriter fw = new FileWriter(logFile);
			fw.write("---- BlockBase Crash Report ----");
			fw.write("\n// "+phrases[new Random().nextInt(phrases.length)]+"\n");
			fw.write(textArea.getText());
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println(textarea.getText());
	}
	
	void log(int report, String wantToApply) {
		if(report == 1) {
			this.report += wantToApply + "\n";
		} else {
			this.main += wantToApply + "\n";
		}
	}
	void logN(int report, String wantToApply) {
		if(report == 1) {
			this.report += wantToApply;
		} else {
			this.main += wantToApply;
		}
	}
	
	private String[] phrases = {
			"How could you?",
			"Man.",
			"Seriously?!?!",
			"I hope this wasn't you!",
			"Do I have to fix this?!?"
	};
}
