package net.oikmo.main;

public class GameSettings {
	
	public static final float GRAVITY = -40;
	
	public static float sensitivity = 0.3f;
	
	public static float globalVolume = 0.5f;
	
	/*public static void loadValues() throws IOException {
		Logger.log(LogLevel.INFO, "honey, im loading the config!");
		File save =  new File(Main.getDir().getPath()+"/options.save");
		if(!save.exists()) {
			save.createNewFile();
			StringTranslate st = StringTranslate.getInstance();
			st.insertKey("input.sensitivity", Float.toString(sensitivity));
		} else {
			StringTranslate st = StringTranslate.getInstance();
			globalVolume = Float.parseFloat(st.translateKey("audio.globalVolume"));
			sensitivity = Float.parseFloat(st.translateKey("input.sensitivity"));
		}
	}
	
	public static void saveValues() {
		Logger.log(LogLevel.INFO, "honey, im saving the config!");
		File save =  new File(Main.getDir().getPath()+"/options.save");
		if(!save.exists()) {
			try {
				save.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		StringTranslate st = StringTranslate.getInstance();
		st.insertKey("audio.globalVolume", Float.toString(globalVolume));
		st.insertKey("input.sensitivity", Float.toString(sensitivity));
	}
	//store from player and load values from predefined file
	*/
}
