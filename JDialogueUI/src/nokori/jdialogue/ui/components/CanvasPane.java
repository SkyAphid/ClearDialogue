package nokori.jdialogue.ui.components;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUIDialog;
import lwjgui.scene.control.ContextMenu;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.floating.PannablePane;
import lwjgui.scene.shape.Rectangle;
import nokori.jdialogue.project.DialogueText;
import nokori.jdialogue.ui.JDUIController;
import nokori.jdialogue.ui.dialogue_nodes.DialogueNode;
import nokori.jdialogue.ui.dialogue_nodes.DialogueTextNode;

/**
 * CanvasPane is used for storing draggable DialogueNodes.
 * 
 * I've made it to extend PannablePane so that custom behaviors can be added if needed.
 */
public class CanvasPane extends PannablePane {
	
	public CanvasPane(JDUIController controller, Font sansFont, Font serifFont) {
		ContextMenu contextMenu = new ContextMenu();
		
		/*
		 * Add Nodes
		 */
		MenuItem newDialogueTextNode = new MenuItem("+Text Node");
		newDialogueTextNode.setOnAction(e -> {
			DialogueText text = new DialogueText(controller.getProject(), "Dialogueeeeeeeeeeeeeeeeeeeeeee", "", cached_context.getMouseX(), cached_context.getMouseY());
			DialogueTextNode node = new DialogueTextNode(controller, text, sansFont, serifFont);
			controller.addDialogueNode(node);
		});
		
		MenuItem newDialogueResponseNode = new MenuItem("+Response Node");
		newDialogueResponseNode.setOnAction(e -> {
			
		});
		
		/*
		 * Tools
		 */
		
		MenuItem center = new MenuItem("Center Viewport");
		center.setOnAction(e -> {
			center();
		});
		
		/*
		 * Configure Context Menu
		 */

		contextMenu.getItems().addAll(newDialogueTextNode, newDialogueResponseNode, center);
		contextMenu.setAutoHide(false);
		
		setOnMouseClicked(e -> {
			if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				contextMenu.show(getScene(), cached_context.getMouseX(), cached_context.getMouseY());
			}
		});
	}

	
	/**
	 * Clears this CanvasPane of existing nodes and re-adds the crosshair - basically prepares it for a new canvas (E.G. when loading another project)
	 * 
	 * @return - returns this CanvasPane for some syntax sugar
	 */
	public CanvasPane clear() {
		getChildren().clear();
		
		//Create a crosshair that shows the center of the pane
		int crosshairSize = 10;
		int crosshairCenterReticle = 2;
		Color crosshairFill = Color.DARK_GRAY;
		
		getChildren().add(new Rectangle(crosshairCenterReticle, crosshairCenterReticle, crosshairFill));
		getChildren().add(new Rectangle(1, crosshairSize, crosshairFill));
		getChildren().add(new Rectangle(crosshairSize, 1, crosshairFill));
		
		return this;
	}
}
