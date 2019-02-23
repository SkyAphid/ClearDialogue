package nokori.jdialogue.ui;

import java.awt.Desktop;
import java.net.URL;

import lwjgui.Color;
import lwjgui.event.Event;
import lwjgui.font.Font;
import lwjgui.font.FontStyle;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;
import lwjgui.transition.FillTransition;
import nokori.jdialogue.io.JDialogueJsonIO;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.components.SelectableLabel;
import nokori.jdialogue.ui.components.dropdown.DropdownDivider;
import nokori.jdialogue.ui.components.dropdown.DropdownMenu;
import nokori.jdialogue.ui.components.dropdown.DropdownOption;
import nokori.jdialogue.ui.components.ProjectNameField;
import nokori.jdialogue.ui.util.JDialogueIDEUtil;

import static nokori.jdialogue.ui.JDialogueIDECore.*;

/**
 * This class assembles the design of the main window; such as the UI and canvas.
 * 
 * After it's intially called, it shouldn't ever be needed for the rest of runtime.
 */
public class JDialogueIDEDesigner {
	
	private static final int PADDING = 10;
	
	public JDialogueIDEDesigner(SharedResources sharedResources) {
		Window window = sharedResources.getWindow();
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
		createHUD(sharedResources, rootPane);
	}
	
	private void createHUD(SharedResources sharedResources, StackPane rootPane) {
		
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
		SelectableLabel programInformation = new SelectableLabel(PROGRAM_NAME + " " + PROGRAM_VERSION + " by " + PROGRAM_DEVELOPER) {
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
	
	private DropdownMenu newFileMenu(SharedResources sharedResources) {

		DropdownOption newProject = new DropdownOption("NEW PROJECT...", e -> {
			JDialogueIDEUtil.showProjectDirectorySelectDialog();
		});
		
		DropdownOption setProjectDirectory = new DropdownOption("PROJECT DIR...", e -> {
			JDialogueIDEUtil.showProjectDirectorySelectDialog();
		});
		
		DropdownOption mergeProject = new DropdownOption("MERGE PROJECT...", e -> {
			Project merge = JDialogueIDEUtil.showImportProjectDialog("Merge JSON Project", new JDialogueJsonIO());
			
			if (merge!= null) {
				sharedResources.getProject().mergeProject(merge);
				sharedResources.getCanvasPane().refresh();
			}
			
		});
		
		DropdownOption exportJson = new DropdownOption("EXPORT JSON...", e -> {
			JDialogueIDEUtil.showExportProjectDialog(sharedResources.getProject(), new JDialogueJsonIO());
		});
		
		DropdownOption importJson = new DropdownOption("IMPORT JSON...", e -> {
			Project importProject = JDialogueIDEUtil.showImportProjectDialog("Import JSON Project", new JDialogueJsonIO());
			
			if (importProject != null) {
				sharedResources.setProject(importProject);
			}
		});
		
		DropdownMenu file = new DropdownMenu(getToolbarAbsoluteX(0), PADDING, sharedResources.getTheme().getSansFont(), "FILE", 
				newProject, 
				setProjectDirectory, 
				mergeProject, 
				new DropdownDivider(),
				exportJson, 
				importJson);
		
		return file;
	}
	
	/*
	 * Tool Menu
	 * 
	 * Contains various useful tools for dialogue editing.
	 */
	

	private DropdownMenu newToolMenu(SharedResources sharedResources) {
		DropdownOption viewSyntax = new DropdownOption("VIEW SYNTAX", e -> {
			
		});
		
		DropdownOption refreshSyntax = new DropdownOption("REFRESH SYNTAX", e -> {
			
		});
		
		DropdownOption setSyntax = new DropdownOption("SET SYNTAX...", e -> {
			JDialogueIDEUtil.showSyntaxFileSelectDialog();
		});
		
		DropdownOption replace = new DropdownOption("REPLACE...", e -> {
			
		});

		DropdownMenu tool = new DropdownMenu(getToolbarAbsoluteX(1), PADDING, sharedResources.getTheme().getSansFont(), "TOOL", 
				viewSyntax, 
				refreshSyntax, 
				setSyntax, 
				new DropdownDivider(),
				replace);
		
		return tool;
	}
	
	/*
	 * Project Name Field
	 */
	
	private ProjectNameField newProjectNameField(SharedResources sharedResources) {
		return new ProjectNameField(sharedResources, getToolbarAbsoluteX(2), PADDING, sharedResources.getTheme().getSansFont());
	}
	
	/**
	 * Sets the absolute position for a toolbar element based on the given index (element 0, 1, 2, etc)
	 * 
	 * @param index
	 * @return
	 */
	private static int getToolbarAbsoluteX(int index) {
		return PADDING + (DropdownMenu.DEFAULT_WIDTH + PADDING) * index;
	}
}
