package nokori.jdialogue.ui.window_design;

import java.io.File;

import org.lwjgl.glfw.GLFW;

import lwjgui.event.listener.KeyListener;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

public class MainWindowDesign {

	public MainWindowDesign(Window window) {
		Scene scene = window.getScene();
		
		// Create a simple pane
		StackPane pane = new StackPane();
		
		window.setIcon(new File("res/icon_pngs/").listFiles());
		
		// Set the pane as the scenes root
		scene.setRoot(pane);
		
		window.addEventListener(new KeyListener() {
			public void invoke(long window, int key, int scancode, int action, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
				System.err.println(GLFW.glfwGetKeyName(key, scancode));
			}
		});
	}

}
