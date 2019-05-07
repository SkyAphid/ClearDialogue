package nokori.clear_dialogue.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import nokori.clear.windows.Window;

public class WindowMemory {
	
	public static final int CURRENT_VERSION = 1;
	private static int version = CURRENT_VERSION;
	
	public static int x, y, width, height; 
	public static boolean maximized;
	
	/**
	 * Initializes WindowMemory by using the given window as a base.
	 * 
	 * @param window
	 */
	public static void init(Window window) {
		x = window.getX();
		y = window.getY();
		width = window.getWidth();
		height = window.getHeight();
		maximized = window.isMaximized();
	}
	
	/**
	 * Loads the WindowMemory and synchronizes this class with it
	 */
	public static boolean load() {
		File f = new File("window_memory.ini");
		
		if (f.exists()) {
			try (InputStream in = new FileInputStream(f)) {
				
				Properties p = new Properties();
				p.load(in);
				
				version = Integer.parseInt(p.getProperty("version"));
				
				x = Integer.parseInt(p.getProperty("windowX"));
				y = Integer.parseInt(p.getProperty("windowY"));
				width = Integer.parseInt(p.getProperty("windowWidth"));
				height = Integer.parseInt(p.getProperty("windowHeight"));
				
				maximized = Boolean.parseBoolean(p.getProperty("windowMaximized"));
				
				return true;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	/**
	 * Saves an ini file with this class's current settings
	 */
	public static void save() {
		try (OutputStream out = new FileOutputStream("window_memory.ini")) {
			
			Properties p = new Properties();
			
			p.setProperty("version", Integer.toString(version));
			
			p.setProperty("windowX", Integer.toString(x));
			p.setProperty("windowY", Integer.toString(y));
			p.setProperty("windowWidth", Integer.toString(width));
			p.setProperty("windowHeight", Integer.toString(height));
			
			p.setProperty("windowMaximized", Boolean.toString(maximized));
			
			p.store(out, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
