package nokori.jdialogue.ui;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Window;
import lwjgui.util.UpdateTimer;
import nokori.jdialogue.ui.window_design.MainWindowDesign;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;

public class JDialogueUICore {
	
	private static final String programName = "JDialogue";
	private static final String programVersion = "Rev. 10";
	private static final String programDeveloper = "NOKORI•WARE";
	
	private static final int DEFAULT_WIDTH = 1280;
	private static final int DEFAULT_HEIGHT = 720;
	
	public static final int UPDATES_PER_SECOND = 30;
	
	private long windowID;
	private Window window;
	
	public static void main(String args[]) {
		if (!glfwInit())throw new IllegalStateException("Unable to initialize GLFW");

		//Begin running the program.
		new JDialogueUICore().run();
				
		//Stop GLFW after the window closes.
		glfwTerminate();
	}
	
	public JDialogueUICore() {
		//Create a standard opengl 3.2 window.
		windowID = LWJGUIUtil.createOpenGLCoreWindow(programName, DEFAULT_WIDTH, DEFAULT_HEIGHT, true, false);
	
		//Initialize LWJGUI for this window ID.
		window = LWJGUI.initialize(windowID);
		new MainWindowDesign(window);
	}

	public void run() {
		//Software loop
		while (!GLFW.glfwWindowShouldClose(windowID)) {
			//Render the program
			LWJGUI.render();
			
		}
	}
}
