package nokori.jdialogue.ui;

import java.awt.Desktop;
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
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;
import lwjgui.transition.FillTransition;
import nokori.jdialogue.io.JDialogueJsonIO;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.components.JDDropdownMenu;
import nokori.jdialogue.ui.components.JDSelectableLabel;
import nokori.jdialogue.ui.components.JDProjectNameField;
import nokori.jdialogue.ui.util.JDialogueIDEUtil;

import static nokori.jdialogue.ui.JDialogueIDECore.*;

/**
 * This class assembles the design of the main window; such as the UI and canvas.
 * 
 * After it's intially called, it shouldn't ever be needed for the rest of runtime.
 */
public class JDialogueIDEDesigner {
	
	private static final int PADDING = 10;
	
	public JDialogueIDEDesigner(Window window, SharedResources sharedResources) {
		Scene scene = window.getScene();

		/*
		 * 
		 * Create user-interface
		 * 
		 */
		
		//Set root pane
		StackPane rootPane = new StackPane();
		rootPane.setFillToParentWidth(true);
		rootPane.setFillToParentHeight(true);
		rootPane.setAlignment(Pos.BOTTOM_LEFT);
		rootPane.setPadding(new Insets(0, 0, PADDING, PADDING));
		scene.setRoot(rootPane);

		//Add canvas to root
		rootPane.getChildren().add(sharedResources.getCanvasPane());
		
		//Create HUD (toolbar, etc)
		createHUD(window, sharedResources, rootPane);
	}
	
	private void createHUD(Window window, SharedResources sharedResources, StackPane rootPane) {
		
		Font sansFont = sharedResources.getTheme().getSansFont();
		
		/*
		 * Create "toolbar" pane
		 */
		FloatingPane uiPaneTop = new FloatingPane();
		rootPane.getChildren().add(uiPaneTop);
		
		uiPaneTop.getChildren().add(newFileMenu(sharedResources));
		uiPaneTop.getChildren().add(newToolMenu(sharedResources));
		uiPaneTop.getChildren().add(newProjectNameField(sharedResources));
		
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
				//Context hint was changed (fade it back in)
				if (!this.getText().equals(sharedResources.getContextHint())) {
					new FillTransition(200, Color.TRANSPARENT, Theme.current().getText(), getTextFill()).play();
					setText(sharedResources.getContextHint());
				}
				
				super.render(context);
			}
		};

		contextHint.setTextFill(Theme.current().getText().copy());
		contextHint.setFont(sansFont);
		contextHint.setFontSize(fontSize);
		contextHint.setFontStyle(FontStyle.LIGHT);
		contextHint.setPadding(new Insets(0, 0, 0, 350));
		
		uiPaneBottom.getChildren().add(contextHint);
	}
	
	/*
	 * File Menu
	 */
	
	private static final String NEW_PROJECT = "NEW PROJECT";
	private static final String SELECT_PROJECT_DIRECTORY = "PROJECT DIR...";
	private static final String MERGE_PROJECT = "MERGE PROJECT...";
	private static final String EXPORT_JSON = "EXPORT JSON...";
	private static final String IMPORT_JSON = "IMPORT JSON...";
	
	private JDDropdownMenu newFileMenu(SharedResources sharedResources) {
		String[] options = new String[] {
			NEW_PROJECT, SELECT_PROJECT_DIRECTORY, MERGE_PROJECT, EXPORT_JSON, IMPORT_JSON	
		};
		
		JDDropdownMenu file = new JDDropdownMenu(getToolbarAbsoluteX(0), PADDING, sharedResources.getTheme().getSansFont(), "FILE", options) {
			@Override
			public void optionClicked(Event e, String option) {
				switch(option) {
				case NEW_PROJECT:
					break;
				case SELECT_PROJECT_DIRECTORY:
					JDialogueIDEUtil.showProjectDirectorySelectDialog();
					break;
				case MERGE_PROJECT:
					Project mergeProject = JDialogueIDEUtil.showImportProjectDialog("Merge JSON Project", new JDialogueJsonIO());
					
					if (mergeProject != null) {
						sharedResources.getProject().mergeProject(mergeProject);
						sharedResources.getCanvasPane().refresh();
					}
					
					break;
				case EXPORT_JSON:
					JDialogueIDEUtil.showExportProjectDialog(sharedResources.getProject(), new JDialogueJsonIO());
					break;
				case IMPORT_JSON:
					Project importProject = JDialogueIDEUtil.showImportProjectDialog("Import JSON Project", new JDialogueJsonIO());
					
					if (importProject != null) {
						sharedResources.setProject(importProject);
					}
					break;
				}
			}
		};
		
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
	
	private JDDropdownMenu newToolMenu(SharedResources sharedResources) {
		String[] options = new String[] {
				VIEW_SYNTAX, REFRESH_SYNTAX, SET_SYNTAX, REPLACE
		};
		
		JDDropdownMenu tool = new JDDropdownMenu(getToolbarAbsoluteX(1), PADDING, sharedResources.getTheme().getSansFont(), "TOOL", options) {
			@Override
			public void optionClicked(Event e, String option) {
				switch(option) {
				case VIEW_SYNTAX:
					break;
				case REFRESH_SYNTAX:
					break;
				case SET_SYNTAX:
					JDialogueIDEUtil.showSyntaxFileSelectDialog();
					
					break;
				case REPLACE:
					break;
				}
			}
		};
		
		return tool;
	}
	
	/*
	 * Project Name Field
	 */
	
	private JDProjectNameField newProjectNameField(SharedResources sharedResources) {
		return new JDProjectNameField(sharedResources, getToolbarAbsoluteX(2), PADDING, sharedResources.getTheme().getSansFont());
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
