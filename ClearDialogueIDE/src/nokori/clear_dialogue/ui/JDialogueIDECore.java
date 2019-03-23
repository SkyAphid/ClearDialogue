package nokori.clear_dialogue.ui;

import java.io.File;

import nokori.clear.vg.ClearApplication;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.windows.GLFWException;
import nokori.clear.windows.Window;
import nokori.clear.windows.WindowManager;

public class JDialogueIDECore extends ClearApplication {
	
	public static final String PROGRAM_NAME = "JDialogue";
	public static final String PROGRAM_VERSION = "Rev. 10";
	public static final String PROGRAM_DEVELOPER = "NOKORI•WARE";
	
	public static final String PROGRAM_NAME_FULL = PROGRAM_NAME + " " + PROGRAM_VERSION + " by " + PROGRAM_DEVELOPER;
	
	public static final int DEFAULT_WINDOW_WIDTH = 1280;
	public static final int DEFAULT_WINDOW_HEIGHT = 720;

	public static void main(String args[]) {
		launch(new JDialogueIDECore(), args);
	}

	public JDialogueIDECore() {
		super(new JDialogueWidgetAssembly());
	}
	
	@Override
	public void init(WindowManager windowManager, Window window, NanoVGContext context, WidgetAssembly rootWidgetAssembly, String[] args) {
		/*
		 * Initialize SharedResources
		 */
		
		SharedResources sharedResources = new SharedResources();
		sharedResources.init(context);
		
		/*
		 * Initialize JDialogueWidgetAssembly (the user-interface)
		 */
		
		JDialogueWidgetAssembly root = (JDialogueWidgetAssembly) rootWidgetAssembly;
		root.init(sharedResources);
	}

	@Override
	protected void endOfNanoVGApplicationCallback() {}

	@Override
	public Window createWindow(WindowManager windowManager) throws GLFWException {
		Window window = windowManager.createWindow(PROGRAM_NAME, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, true, true);
		window.setSizeLimits(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		window.setIcon(".png", new File("res/icons/").listFiles());
		return window;
	}
	

}
