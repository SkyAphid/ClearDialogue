package nokori.jdialogue.ui.layout;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.event.MouseEvent;
import lwjgui.scene.Node;
import lwjgui.scene.control.ContextMenu;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.control.SeparatorMenuItem;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.floating.PannablePane;
import lwjgui.scene.shape.Rectangle;
import nokori.jdialogue.project.Connection;
import nokori.jdialogue.project.Dialogue;
import nokori.jdialogue.project.DialogueConnector;
import nokori.jdialogue.project.DialogueResponse;
import nokori.jdialogue.project.DialogueText;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.SharedResources;
import nokori.jdialogue.ui.dialogue_nodes.ConnectorLineNode;
import nokori.jdialogue.ui.dialogue_nodes.DialogueNode;
import nokori.jdialogue.ui.dialogue_nodes.DialogueConnectorNode;
import nokori.jdialogue.ui.dialogue_nodes.DialogueResponseNode;
import nokori.jdialogue.ui.dialogue_nodes.DialogueTextNode;
import nokori.jdialogue.ui.throwable.MissingConnectorError;

/**
 * CanvasPane is used for storing draggable DialogueNodes.
 * 
 * I've made it to extend PannablePane so that custom behaviors can be added if needed.
 */
public class CanvasPane extends PannablePane {
	
	private SharedResources sharedResources;
	
	public CanvasPane(SharedResources sharedResources) {
		this.sharedResources = sharedResources;
		
		Font sansFont = sharedResources.getTheme().getSansFont();
		
		ContextMenu contextMenu = new ContextMenu();

		/*
		 * Add Nodes
		 */
		MenuItem newDialogueTextNode = new MenuItem("+Text Node", sansFont);
		newDialogueTextNode.setOnAction(e -> {
			DialogueText text = new DialogueText(sharedResources.getProject(), "Dialogue", "No tag", cached_context.getMouseX(), cached_context.getMouseY());
			DialogueTextNode node = new DialogueTextNode(sharedResources, text);
			node.setAbsolutePosition(text.getX(), text.getY());
			sharedResources.addDialogueNode(node);
		});
		
		MenuItem newDialogueResponseNode = new MenuItem("+Response Node", sansFont);
		newDialogueResponseNode.setOnAction(e -> {
			DialogueResponse text = new DialogueResponse(sharedResources.getProject(), "Response", "", cached_context.getMouseX(), cached_context.getMouseY());
			DialogueResponseNode node = new DialogueResponseNode(sharedResources, text);
			node.setAbsolutePosition(text.getX(), text.getY());
			sharedResources.addDialogueNode(node);
		});
		
		/*
		 * Tools
		 */
		
		MenuItem center = new MenuItem("Center Viewport", sansFont);
		center.setOnAction(e -> {
			center();
		});
		
		/*
		 * Configure Context Menu
		 */

		contextMenu.getItems().addAll(newDialogueTextNode, newDialogueResponseNode, new SeparatorMenuItem(), center);
		contextMenu.setAutoHide(false);
		
		setOnMouseClicked(e -> {
			if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				contextMenu.show(getScene(), cached_context.getMouseX(), cached_context.getMouseY());
			}
		});
		
		/*
		 * Call clear (adds crosshairs)
		 */
		
		clear();
	}
	
	/**
	 * Update the viewport position every time the canvas is dragged around by the mouse.
	 */
	@Override
	public void drag(MouseEvent e) {
		super.drag(e);
		sharedResources.getProject().setViewportPosition(getX(), getY());
	}
	
	/**
	 * Clears this CanvasPane of existing nodes and re-adds the crosshair - basically prepares it for a new canvas (E.G. when loading another project)
	 */
	private void clear() {
		//Clears all old nodes from this Canvas
		getChildren().clear();
		
		//Create a crosshair that shows the center of the pane
		int crosshairSize = 10;
		int crosshairCenterReticle = 2;
		Color crosshairFill = Color.DARK_GRAY;
		
		getChildren().add(new Rectangle(crosshairCenterReticle, crosshairCenterReticle, crosshairFill));
		getChildren().add(new Rectangle(1, crosshairSize, crosshairFill));
		getChildren().add(new Rectangle(crosshairSize, 1, crosshairFill));
	}
	
	/**
	 * Refreshes the canvas by clearing it and re-populating it with data from the current project. Call this if you load a new project or initialize the program.
	 */
	public void refresh() {
		
		Project project = sharedResources.getProject();
		
		/*
		 * 
		 * Reset the canvas and sync it up to the Projects viewport settings
		 * 
		 */
		
		clear();
		
		setAbsolutePosition(project.getViewportX(), project.getViewportY());
		
		/*
		 * 
		 * 
		 * Set up DialogueNodes
		 * 
		 * 
		 */

		for (int i = 0; i < project.getNumDialogue(); i++) {
			Dialogue dialogue = project.getDialogue(i);
			DialogueNode node = null;
			
			if (dialogue instanceof DialogueText) {
				node = new DialogueTextNode(sharedResources, (DialogueText) dialogue);
			}
			
			if (dialogue instanceof DialogueResponse) {
				node = new DialogueResponseNode(sharedResources, (DialogueResponse) dialogue);
			}

			getChildren().add(node);
		}
		
		/*
		 * 
		 * 
		 * Set up connectors
		 * 
		 * 
		 */
		
		// Build all ConnectorLine objects for each Connection
		for (int i = 0; i < project.getNumConnections(); i++) {
			Connection connection = project.getConnection(i);

			//Get the two connected connectors from the connection
			DialogueConnector connector1 = connection.getConnector1();
			DialogueConnector connector2 = connection.getConnector2();

			//Find their UI counterparts in canvas with the getDialogueConnectorNodeOf() function.
			DialogueConnectorNode nodeConnector1 = getDialogueConnectorNodeOf(connector1);
			DialogueConnectorNode nodeConnector2 = getDialogueConnectorNodeOf(connector2);

			//If both UI connectors are found for the connection, then build a line between them.
			if (nodeConnector1 != null && nodeConnector2 != null) {
				ConnectorLineNode line = new ConnectorLineNode(nodeConnector1, nodeConnector2);
				getChildren().add(line);
			} else {
				//Throw an error if there's a Connection but no corresponding UI elements for them
				throw new MissingConnectorError("\n" + connector1.getParent().getName() + " -> " + nodeConnector1 + "\n" + connector2.getParent().getName() + " -> " + nodeConnector2);
			}
		}
	}
	
	/**
	 * Searches for the DialogueConnectorNode that contains the inputted connector.
	 * 
	 * @param connector
	 * @return
	 */
	private DialogueConnectorNode getDialogueConnectorNodeOf(DialogueConnector connector) {
		//Scan canvas for nodes
		for (int i = 0; i < getChildren().size(); i++) {
			Node n = getChildren().get(i);
			
			//Check the node if its a DialogueNode
			if (n instanceof DialogueNode) {
				DialogueNode node = (DialogueNode) n;
				
				//Fetch the UI representation of the connector that's connected to the inputted connector
				DialogueConnectorNode connector2 = node.getDialogueNodeConnectorOf(connector);
				
				//If we found one, return it.
				if (connector2 != null) {
					return connector2;
				}
			}
		}
		
		return null;
	}
}
