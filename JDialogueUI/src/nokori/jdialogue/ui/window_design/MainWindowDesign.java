package nokori.jdialogue.ui.window_design;

import java.io.File;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.event.listener.KeyListener;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.FloatingPane;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;
import nokori.jdialogue.ui.components.JDButton;
import nokori.jdialogue.ui.components.JDTextField;
import nokori.jdialogue.ui.theme.JDialogueTheme;

public class MainWindowDesign {

	private static final int TOOLBAR_START_X = 10;
	private static final int TOOLBAR_START_Y = 10;
	
	private static final Color TOOLBAR_BG_FILL = Color.CORAL;
	private static final Color TOOLBAR_TEXT_FILL = Color.WHITE_SMOKE;
	
	public MainWindowDesign(Window window) {
		/*
		 * Main Window settings
		 */
		
		Scene scene = window.getScene();
		
		//Set window theme
		window.setIcon(new File("res/icon_pngs/").listFiles());
		
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
		
		//Create UI pane for toolbar for HUD elements
		FloatingPane uiPane = new FloatingPane();
		uiPane.setAlignment(Pos.TOP_LEFT);
		
		canvasPane.getChildren().add(uiPane);
		
		//Configure toolbar
		addFileButton(uiPane, sansFont);
		addToolButton(uiPane, sansFont);
		addNodeButton(uiPane, sansFont);
		addProjectNameField(uiPane, sansFont);
	}
	
	private void addFileButton(FloatingPane pane, Font sansFont) {
		JDButton file = new JDButton("FILE", sansFont, TOOLBAR_BG_FILL, TOOLBAR_TEXT_FILL);
		file.setAbsolutePosition(getButtonX(0), TOOLBAR_START_Y);
		pane.getChildren().add(file);
	}
	
	private void addToolButton(FloatingPane pane, Font sansFont) {
		JDButton tool = new JDButton("TOOL", sansFont, TOOLBAR_BG_FILL, TOOLBAR_TEXT_FILL);
		tool.setAbsolutePosition(getButtonX(1), TOOLBAR_START_Y);
		pane.getChildren().add(tool);
	}
	
	private void addNodeButton(FloatingPane pane, Font sansFont) {
		JDButton file = new JDButton("+NODE", sansFont, TOOLBAR_BG_FILL, TOOLBAR_TEXT_FILL);
		file.setAbsolutePosition(getButtonX(2), TOOLBAR_START_Y);
		pane.getChildren().add(file);
	}
	
	private void addProjectNameField(FloatingPane pane, Font sansFont) {
		JDTextField field = new JDTextField("Default Project", TOOLBAR_BG_FILL, TOOLBAR_TEXT_FILL);
		field.setAbsolutePosition(getButtonX(3), TOOLBAR_START_Y);
		pane.getChildren().add(field);
	}
	
	private static int getButtonX(int index) {
		int startX = TOOLBAR_START_X;
		int incrementX = 10;
		return startX + (JDButton.DEFAULT_WIDTH + incrementX) * index;
	}
}
