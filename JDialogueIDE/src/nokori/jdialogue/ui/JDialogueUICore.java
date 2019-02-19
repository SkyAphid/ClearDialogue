package nokori.jdialogue.ui;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Window;
import nokori.jdialogue.ui.theme.JDialogueWindowDesigner;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFW;

public class JDialogueUICore {
	
	public static final String PROGRAM_NAME = "JDialogue";
	public static final String PROGRAM_VERSION = "Rev. 10";
	public static final String PROGRAM_DEVELOPER = "NOKORI•WARE";
	
	private static final int DEFAULT_WIDTH = 1280;
	private static final int DEFAULT_HEIGHT = 720;
	
	private long windowID;
	private Window window;
	
	private JDUIController controller = new JDUIController();
	
	public static void main(String args[]) {
		//Restarts the JVM if necessary on the first thread to ensure Mac compatibility
		if (LWJGUIUtil.restartJVMOnFirstThread(true, args)) {
			return;
		}
		
		//Fail to start the program if GLFW can't be initialized
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

		//Begin running the program.
		new JDialogueUICore().run();
				
		//Stop GLFW after the window closes.
		glfwTerminate();
	}
	
	public JDialogueUICore() {
		//Create a standard opengl 3.2 window.
		windowID = LWJGUIUtil.createOpenGLCoreWindow(PROGRAM_NAME, DEFAULT_WIDTH, DEFAULT_HEIGHT, true, false);
	
		//Initialize LWJGUI for this window ID.
		window = LWJGUI.initialize(windowID);
		window.getContext().setContextSizeLimits(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		new JDialogueWindowDesigner(window, controller);
	}

	public void run() {
		//Software loop
		while (!GLFW.glfwWindowShouldClose(windowID)) {
			//Render the program
			LWJGUI.render();
			
		}
	}
}
