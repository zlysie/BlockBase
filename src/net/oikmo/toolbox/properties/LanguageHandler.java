package net.oikmo.toolbox.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.oikmo.main.Main;

public class LanguageHandler {
	private static LanguageHandler instance = new LanguageHandler();
	private Properties properties = new Properties();
	private InputStream filePath;
	
	protected LanguageHandler() {
		try {
			this.filePath = Main.class.getResourceAsStream("/assets/lang/pl_PL.lang");
			properties.load(filePath);
		} catch (IOException var2) {
			var2.printStackTrace();
		}
	}
	
	public static LanguageHandler getInstance() {
		return instance;
	}
	
	public String translateKey(String key) {
		return this.properties.getProperty(key, key);
	}
	
	public String translateKeyFormat(String key, Object toFormat) {
		String property = this.properties.getProperty(key, key);
		return String.format(property, toFormat);
	}

	public String translateNamedKey(String key) {
		return this.properties.getProperty(key + ".name", "");
	}
}
