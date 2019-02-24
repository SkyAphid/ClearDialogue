package nokori.jdialogue.ui;

import java.io.File;

import lwjgui.LWJGUIProgram;
import lwjgui.scene.Window;

public class JDialogueIDECore extends LWJGUIProgram {
	
	public static final String PROGRAM_NAME = "JDialogue";
	public static final String PROGRAM_VERSION = "Rev. 10";
	public static final String PROGRAM_DEVELOPER = "NOKORI•WARE";

	public static void main(String args[]) {
		LWJGUIProgram.launch(args);
	}

	@Override
	public void init(String[] args, Window window) {
		window.setIcon(".png", new File("res/icons/").listFiles());
		window.getContext().setContextSizeLimits(getDefaultWindowWidth(), getDefaultWindowHeight());
		
		new JDialogueIDEDesigner(new SharedResources(window));
	}

	@Override
	public void run() {}
	
	@Override
	public String getProgramName() {
		return PROGRAM_NAME;
	}

	@Override
	public int getDefaultWindowWidth() {
		return 1280;
	}

	@Override
	public int getDefaultWindowHeight() {
		return 720;
	}
}
