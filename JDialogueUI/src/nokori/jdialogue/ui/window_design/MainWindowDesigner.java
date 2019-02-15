package nokori.jdialogue.ui.window_design;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;

import lwjgui.Color;
import lwjgui.event.Event;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;
import nokori.jdialogue.ui.JDUIController;
import nokori.jdialogue.ui.components.JDDropdownMenu;
import nokori.jdialogue.ui.components.JDSelectableLabel;
import nokori.jdialogue.ui.components.JDProjectNameField;
import nokori.jdialogue.ui.theme.JDialogueTheme;
import nokori.jdialogue.ui.transitions.LabelFillTransition;

import static nokori.jdialogue.ui.JDialogueUICore.*;

/**
 * This class assembles the design of the main window; such as the UI and canvas.
 * @author Brayden
 *
 */
public class MainWindowDesigner {
	
	private static final int PADDING = 10;
	
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
		rootPane.setFillToParentWidth(true);
		rootPane.setFillToParentHeight(true);
		rootPane.setAlignment(Pos.BOTTOM_LEFT);
		rootPane.setPadding(new Insets(0, 0, PADDING, PADDING));
		scene.setRoot(rootPane);
		
		//Set canvas pane
		FloatingPane canvasPane = new FloatingPane();
		canvasPane.setAlignment(Pos.CENTER);
		canvasPane.setAbsolutePosition(windowWidth/2, windowHeight/2);
		canvasPane.setMinWidth(windowWidth);
		canvasPane.setMinHeight(windowHeight);
		rootPane.getChildren().add(canvasPane);
		
		//Create "toolbar" pane
		FloatingPane uiPaneTop = new FloatingPane();
		rootPane.getChildren().add(uiPaneTop);
		
		uiPaneTop.getChildren().add(newFileMenu(sansFont));
		uiPaneTop.getChildren().add(newToolMenu(sansFont));
		uiPaneTop.getChildren().add(newNodeMenu(sansFont));
		uiPaneTop.getChildren().add(newProjectNameField(sansFont, controller));
		
		//Create program information and context hints pane
		int fontSize = 24;
		
		GridPane uiPaneBottom = new GridPane();
		uiPaneBottom.setHgap(50);
		rootPane.getChildren().add(uiPaneBottom);
		
		JDSelectableLabel programInformation = new JDSelectableLabel(PROGRAM_NAME + " " + PROGRAM_VERSION + " by " + PROGRAM_DEVELOPER) {
			@Override
			protected void mouseClicked(Event e) {
				try {
					Desktop.getDesktop().browse(new URL("https://github.com/SkyAphid/JDialogue").toURI());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
		programInformation.setFont(sansFont);
		programInformation.setFontSize(fontSize);
		programInformation.setFontStyle(FontStyle.LIGHT);
		programInformation.setClickingEnabled(Desktop.isDesktopSupported());

		Label contextHint = new Label() {
			@Override
			public void render(Context context) {
				if (!this.getText().equals(controller.getContextHint())) {
					new LabelFillTransition(200, this, Color.TRANSPARENT, Theme.currentTheme().getText()).play();
					setText(controller.getContextHint());
				}
				super.render(context);
			}
		};
		contextHint.setFont(sansFont);
		contextHint.setFontSize(fontSize);
		contextHint.setFontStyle(FontStyle.LIGHT);
		
		uiPaneBottom.add(programInformation, 0, 0);
		uiPaneBottom.add(contextHint, 1, 0);
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
		
		JDDropdownMenu file = new JDDropdownMenu(getToolbarAbsoluteX(0), PADDING, sansFont, "FILE", options);
		
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
		
		JDDropdownMenu tool = new JDDropdownMenu(getToolbarAbsoluteX(1), PADDING, sansFont, "TOOL", options);
		
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
		
		JDDropdownMenu node = new JDDropdownMenu(getToolbarAbsoluteX(2), PADDING, sansFont, "+NODE", options);
		
		return node;
	}
	
	/*
	 * Project Name Field
	 */
	
	private JDProjectNameField newProjectNameField(Font sansFont, JDUIController controller) {
		return new JDProjectNameField(getToolbarAbsoluteX(3), PADDING, sansFont, controller);
	}
	
	/**
	 * Sets the absolute position for a toolbar element based on the given index (element 0, 1, 2, etc)
	 * 
	 * @param index
	 * @return
	 */
	private static int getToolbarAbsoluteX(int index) {
		return PADDING + (JDDropdownMenu.DEFAULT_WIDTH + PADDING) * index;
	}
}
