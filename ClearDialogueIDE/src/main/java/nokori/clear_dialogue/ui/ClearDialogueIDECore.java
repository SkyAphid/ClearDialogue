package nokori.clear_dialogue.ui;

import java.io.File;

import nokori.clear.vg.ClearApp;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.windows.GLFWException;
import nokori.clear.windows.Window;
import nokori.clear.windows.WindowManager;
import nokori.clear.windows.callback.WindowPosCallback;
import nokori.clear.windows.callback.WindowSizeCallback;
import nokori.clear_dialogue.ui.util.WindowMemory;

public class ClearDialogueIDECore extends ClearApp {
	
	public static final String PROGRAM_NAME = "ClearDialogue IDE";
	public static final String PROGRAM_VERSION = "Rev. 2";
	public static final String PROGRAM_DEVELOPER = "NOKORI•WARE";
	
	public static final String PROGRAM_INFORMATION = PROGRAM_NAME + " " + PROGRAM_VERSION + " by " + PROGRAM_DEVELOPER;
	
	public static final int DEFAULT_WINDOW_WIDTH = 1280;
	public static final int DEFAULT_WINDOW_HEIGHT = 720;

	public static void main(String args[]) {
		launch(new ClearDialogueIDECore(), args);
	}

	public ClearDialogueIDECore() {
		super(new ClearDialogueRootWidgetAssembly());
	}
	
	@Override
	public void init(WindowManager windowManager, Window window, NanoVGContext context, WidgetAssembly rootWidgetAssembly, String[] args) {
		SharedResources sharedResources = new SharedResources();
		sharedResources.init(this, window, context, (ClearDialogueRootWidgetAssembly) rootWidgetAssembly);
	}

	@Override
	protected void endOfNanoVGApplicationCallback() {
		WindowMemory.save();
	}

	@Override
	public Window createWindow(WindowManager windowManager) throws GLFWException {
		
		/*
		 * Window creation
		 */
		
		Window window = windowManager.createWindow(PROGRAM_NAME, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT, true, true);
		window.setSizeLimits(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
		window.setIcons(".png", new File("res/icons/").listFiles());
		
		/*
		 * WindowMemory
		 */
		
		if (WindowMemory.load()) {
			window.setPosition(WindowMemory.x, WindowMemory.y);
			window.setSize(WindowMemory.width, WindowMemory.height);
			
			if (WindowMemory.maximized) {
				window.maximize();
			}
		} else {
			WindowMemory.init(window);
			WindowMemory.save();
		}

		//Window position memory
		window.getWindowPosCallbacks().add(new WindowPosCallback() {
			@Override
			public void windowPositionEvent(Window window, long timestamp, int x, int y) {
				WindowMemory.x = x;
				WindowMemory.y = y;
			}
		});
		
		//Window size memory
		window.getWindowSizeCallbacks().add(new WindowSizeCallback() {

			@Override
			public void windowSizeEvent(Window window, long timestamp, int width, int height) {
				WindowMemory.width = width;
				WindowMemory.height = height;
				WindowMemory.maximized = window.isMaximized();
			}
			
		});
		
		
		
		return window;
	}
	

}
