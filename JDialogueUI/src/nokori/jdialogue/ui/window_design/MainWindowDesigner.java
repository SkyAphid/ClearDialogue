package nokori.jdialogue.ui.window_design;

import java.awt.Desktop;
import java.io.File;
import java.net.URL;

import org.joml.Vector2d;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIDialog;
import lwjgui.event.Event;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.floating.DraggablePane;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.layout.floating.PannablePane;
import lwjgui.scene.shape.Rectangle;
import lwjgui.scene.shape.SectorCircle;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;
import nokori.jdialogue.project.DialogueResponse;
import nokori.jdialogue.project.DialogueText;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.JDUIController;
import nokori.jdialogue.ui.components.CanvasPane;
import nokori.jdialogue.ui.components.JDDropdownMenu;
import nokori.jdialogue.ui.components.JDSelectableLabel;
import nokori.jdialogue.ui.dialogue_nodes.DialogueNode;
import nokori.jdialogue.ui.dialogue_nodes.DialogueResponseNode;
import nokori.jdialogue.ui.dialogue_nodes.DialogueTextNode;
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
		LWJGUIDialog.showMessageDialog("fuck", "you");
		
		/*
		 * 
		 * Main Window settings
		 * 
		 */
		
		Scene scene = window.getScene();
		
		//Set window theme
		File[] files = new File("res/icons/").listFiles();
		
		window.setIcon(".png", files);

		/*
		 * 
		 * Create user-interface
		 * 
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

		//Refresh canvas (DialogueNode management)
		refreshCanvas(window, controller, rootPane);
		
		//Create HUD (toolbar, etc)
		createHUD(window, controller, rootPane);
	}
	
	private void createHUD(Window window, JDUIController controller, StackPane rootPane) {
		
		Font sansFont = controller.getTheme().getSansFont();
		Font serifFont = controller.getTheme().getSerifFont();
		
		/*
		 * Create "toolbar" pane
		 */
		FloatingPane uiPaneTop = new FloatingPane();
		rootPane.getChildren().add(uiPaneTop);
		
		uiPaneTop.getChildren().add(newFileMenu(controller));
		uiPaneTop.getChildren().add(newToolMenu(controller));
		uiPaneTop.getChildren().add(newProjectNameField(controller));
		
		/*
		 * Create program information and context hints pane
		 */
		
		int fontSize = 24;
		
		StackPane uiPaneBottom = new StackPane();
		uiPaneBottom.setAlignment(Pos.BOTTOM_LEFT);
		rootPane.getChildren().add(uiPaneBottom);
		
		//Program information
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
		
		uiPaneBottom.getChildren().add(programInformation);
		
		//context hint
		Label contextHint = new Label() {
			@Override
			public void render(Context context) {
				if (!this.getText().equals(controller.getContextHint())) {
					new LabelFillTransition(200, this, Color.TRANSPARENT, Theme.current().getText()).play();
					setText(controller.getContextHint());
				}
				super.render(context);
			}
		};
		contextHint.setFont(sansFont);
		contextHint.setFontSize(fontSize);
		contextHint.setFontStyle(FontStyle.LIGHT);
		contextHint.setPadding(new Insets(0, 0, 0, 350));

		uiPaneBottom.getChildren().add(contextHint);
	}

	/**
	 * Refreshes the canvas by clearing it and re-populating it with data from the current project. Call this if you load a new project or initialize the program.
	 */
	public void refreshCanvas(Window window, JDUIController controller, StackPane rootPane) {
		//Fetch the CanvasPane from the controller and clear it.
		CanvasPane canvasPane = controller.getCanvasPane().clear();
		rootPane.getChildren().add(canvasPane);
		
		//TODO: Add loading nodes from Projects here.
	}
	
	/*
	 * File Menu
	 */
	
	private static final String NEW_PROJECT = "NEW PROJECT";
	private static final String SELECT_PROJECT_DIRECTORY = "PROJECT DIR...";
	private static final String MERGE_PROJECT = "MERGE PROJECT...";
	private static final String EXPORT_JSON = "EXPORT JSON...";
	private static final String IMPORT_JSON = "IMPORT JSON...";
	
	private JDDropdownMenu newFileMenu(JDUIController controller) {
		String[] options = new String[] {
			NEW_PROJECT, SELECT_PROJECT_DIRECTORY, MERGE_PROJECT, EXPORT_JSON, IMPORT_JSON	
		};
		
		JDDropdownMenu file = new JDDropdownMenu(getToolbarAbsoluteX(0), PADDING, controller.getTheme().getSansFont(), "FILE", options);
		
		return file;
	}
	
	/*
	 * Tool Menu
	 * 
	 * Contains various useful tools for dialogue editing.
	 */
	
	private static final String VIEW_SYNTAX = "VIEW SYNTAX";
	private static final String REFRESH_SYNTAX = "REFRESH SYNTAX";
	private static final String SET_SYNTAX = "SET SYNTAX...";
	private static final String REPLACE = "REPLACE...";
	private static final String MULTIREPLACE = "MULTI-REPLACE...";
	
	private JDDropdownMenu newToolMenu(JDUIController controller) {
		String[] options = new String[] {
				VIEW_SYNTAX, REFRESH_SYNTAX, SET_SYNTAX, REPLACE, MULTIREPLACE
		};
		
		JDDropdownMenu tool = new JDDropdownMenu(getToolbarAbsoluteX(1), PADDING, controller.getTheme().getSansFont(), "TOOL", options) {
			@Override
			public void optionClicked(Event e, String option) {
				switch(option) {
				
				}
			}
		};
		
		return tool;
	}
	
	/*
	 * Project Name Field
	 */
	
	private JDProjectNameField newProjectNameField(JDUIController controller) {
		return new JDProjectNameField(controller, getToolbarAbsoluteX(2), PADDING, controller.getTheme().getSansFont());
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
