package nokori.jdialogue.ui.window_design;

import java.io.File;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;
import nokori.jdialogue.ui.JDUIController;
import nokori.jdialogue.ui.components.JDDropdownMenu;
import nokori.jdialogue.ui.components.JDTextField;
import nokori.jdialogue.ui.theme.JDialogueTheme;

/**
 * This class assembles the design of the main window; such as the UI and canvas.
 * @author Brayden
 *
 */
public class MainWindowDesigner {
	
	private static final int TOOLBAR_PADDING = 10;
	
	public MainWindowDesigner(Window window, JDUIController controller) {
		/*
		 * Main Window settings
		 */
		
		Scene scene = window.getScene();
		
		//Set window theme
		File[] files = new File("res/icons/").listFiles();
		
		window.setIcon(".png", files);
		
		JDialogueTheme theme = new JDialogueTheme();
		Theme.setTheme(theme);
		
		Font sansFont = theme.getSansFont();
		Font serifFont = theme.getSerifFont();
		
		/*
		 * Create user-interface
		 */
		
		int windowWidth = window.getContext().getWidth();
		int windowHeight = window.getContext().getHeight();
		
		//Set root pane
		StackPane rootPane = new StackPane();
		scene.setRoot(rootPane);
		
		//Set canvas pane
		FloatingPane canvasPane = new FloatingPane();
		canvasPane.setAlignment(Pos.CENTER);
		canvasPane.setAbsolutePosition(windowWidth/2, windowHeight/2);
		canvasPane.setMinWidth(windowWidth);
		canvasPane.setMinHeight(windowHeight);
		rootPane.getChildren().add(canvasPane);
		
		//Create "toolbar" pane
		int padding = 10;
		
		FloatingPane uiPaneTop = new FloatingPane();
		uiPaneTop.setPadding(new Insets(padding, 0, 0, padding));
		uiPaneTop.setAlignment(Pos.TOP_LEFT);
		canvasPane.getChildren().add(uiPaneTop);
		
		uiPaneTop.getChildren().add(newFileMenu(sansFont));
		uiPaneTop.getChildren().add(newToolMenu(sansFont));
		uiPaneTop.getChildren().add(newNodeMenu(sansFont));
		uiPaneTop.getChildren().add(newProjectNameField(sansFont));
		
		//Create program information and context hints pane
		
	}
	
	/*
	 * File Menu
	 */
	
	private static final String NEW_PROJECT = "NEW PROJECT";
	private static final String SELECT_PROJECT_DIRECTORY = "PROJECT DIR...";
	private static final String MERGE_PROJECT = "MERGE PROJECT...";
	private static final String EXPORT_JSON = "EXPORT JSON...";
	private static final String IMPORT_JSON = "IMPORT JSON...";
	
	private JDDropdownMenu newFileMenu(Font sansFont) {
		String[] options = new String[] {
			NEW_PROJECT, SELECT_PROJECT_DIRECTORY, MERGE_PROJECT, EXPORT_JSON, IMPORT_JSON	
		};
		
		JDDropdownMenu file = new JDDropdownMenu(getToolbarAbsoluteX(0), TOOLBAR_PADDING, sansFont, "FILE", options);
		
		return file;
	}
	
	/*
	 * Tool Menu
	 */
	
	private static final String CANVAS_SIZE = "CANVAS SIZE...";
	private static final String REPLACE = "REPLACE...";
	private static final String MULTIREPLACE = "MULTI-REPLACE...";
	private static final String VIEW_SYNTAX = "VIEW SYNTAX";
	private static final String REFRESH_SYNTAX = "REFRESH SYNTAX";
	private static final String SET_SYNTAX = "SET SYNTAX...";
	
	private JDDropdownMenu newToolMenu(Font sansFont) {
		String[] options = new String[] {
			CANVAS_SIZE, REPLACE, MULTIREPLACE, VIEW_SYNTAX, REFRESH_SYNTAX, SET_SYNTAX	
		};
		
		JDDropdownMenu tool = new JDDropdownMenu(getToolbarAbsoluteX(1), TOOLBAR_PADDING, sansFont, "TOOL", options);
		
		return tool;
	}
	
	/*
	 * Node Menu
	 */
	
	private static final String DIALOGUE = "DIALOGUE...";
	private static final String RESPONSE = "RESPONSE...";
	
	private JDDropdownMenu newNodeMenu(Font sansFont) {
		String[] options = new String[] {
			DIALOGUE, RESPONSE	
		};
		
		JDDropdownMenu node = new JDDropdownMenu(getToolbarAbsoluteX(2), TOOLBAR_PADDING, sansFont, "+NODE", options);
		
		return node;
	}
	
	/*
	 * Project Name Field
	 */
	
	private JDTextField newProjectNameField(Font sansFont) {
		JDTextField field = new JDTextField(getToolbarAbsoluteX(3), TOOLBAR_PADDING, "Default Project");
		
		return field;
	}
	
	/**
	 * Sets the absolute position for a toolbar element based on the given index (element 0, 1, 2, etc)
	 * 
	 * @param index
	 * @return
	 */
	private static int getToolbarAbsoluteX(int index) {
		return TOOLBAR_PADDING + (JDDropdownMenu.DEFAULT_WIDTH + TOOLBAR_PADDING) * index;
	}
}
