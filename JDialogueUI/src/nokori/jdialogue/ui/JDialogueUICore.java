package nokori.jdialogue.ui;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Window;
import nokori.jdialogue.ui.window_design.MainWindowDesign;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;

public class JDialogueUICore {
	
	private static final String programName = "JDialogue UI";
	private static final String programVersion = "Rev. 10";
	private static final String programDeveloper = "NOKORI•WARE";
	
	private static final int DEFAULT_WIDTH = 1280;
	private static final int DEFAULT_HEIGHT = 720;
	
	public static void main(String args[]) {
		if (!glfwInit())throw new IllegalStateException("Unable to initialize GLFW");

		//Create a standard opengl 3.2 window.
		long windowID = LWJGUIUtil.createOpenGLCoreWindow(programName, DEFAULT_WIDTH, DEFAULT_HEIGHT, true, false);
				
		//Initialize LWJGUI for this window ID.
		Window window = LWJGUI.initialize(windowID);
		new MainWindowDesign(window);
				
		//Loop
		while (!GLFW.glfwWindowShouldClose(windowID)) {
			// Render GUI
			LWJGUI.render();
		}

		//Stop GLFW after the window closes.
		glfwTerminate();
	}
	
	public static void update() {
		
	}
}
