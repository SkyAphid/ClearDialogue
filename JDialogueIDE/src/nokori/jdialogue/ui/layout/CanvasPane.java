package nokori.jdialogue.ui.layout;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.event.MouseEvent;
import lwjgui.event.listener.MouseButtonListener;
import lwjgui.scene.Node;
import lwjgui.scene.layout.floating.PannablePane;
import lwjgui.scene.shape.Rectangle;
import nokori.jdialogue.project.Connection;
import nokori.jdialogue.project.Dialogue;
import nokori.jdialogue.project.DialogueConnector;
import nokori.jdialogue.project.DialogueResponse;
import nokori.jdialogue.project.DialogueText;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.SharedResources;
import nokori.jdialogue.ui.components.dropdown.ContextDropdownMenu;
import nokori.jdialogue.ui.components.dropdown.DropdownDivider;
import nokori.jdialogue.ui.components.dropdown.DropdownOption;
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
	
	public static final String CONTEXT_ADD_DIALOGUE = "";
	public static final String CONTEXT_ADD_RESPONSE = "";
	public static final String CONTEXT_CENTER_VIEWPORT = "";
	
	public CanvasPane(SharedResources sharedResources) {
		this.sharedResources = sharedResources;
		
		/*
		 * Add Node options
		 */
		
		DropdownOption newDialogueTextNode = new DropdownOption("+Text Node", e -> {
			DialogueText text = new DialogueText(sharedResources.getProject(), "Dialogue", "No tag", cached_context.getMouseX(), cached_context.getMouseY());
			DialogueTextNode node = new DialogueTextNode(sharedResources, text);
			node.setAbsolutePosition(text.getX(), text.getY());
			sharedResources.addDialogueNode(node);
		});
		
		DropdownOption newDialogueResponseNode = new DropdownOption("+Response Node", e -> {
			DialogueResponse text = new DialogueResponse(sharedResources.getProject(), "Response", "", cached_context.getMouseX(), cached_context.getMouseY());
			DialogueResponseNode node = new DialogueResponseNode(sharedResources, text);
			node.setAbsolutePosition(text.getX(), text.getY());
			sharedResources.addDialogueNode(node);
		});
		
		/*
		 * Misc. tools
		 */
		
		DropdownOption centerViewport = new DropdownOption("Center Viewport", e -> {
			center();
		});

		/*
		 * Configure Context Menus
		 */
		
		//Add context menu hiding to the window itself that way they'll be hidden regardless of where the click comes from.
		//We add it in this class particularly because context menus are going to be children of this pane specifically.
		sharedResources.getWindow().addEventListener(new MouseButtonListener() {
			@Override
			public void invoke(long window, int button, int downup, int modifier) {
				if (downup == GLFW.GLFW_PRESS) {
					hideContextMenus();
				}
			}
		});
		
		//Create the context menu for this pane
		ContextDropdownMenu contextMenu = new ContextDropdownMenu(sharedResources, newDialogueTextNode, newDialogueResponseNode, new DropdownDivider(), centerViewport);

		setOnMouseClicked(e -> {
			if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				contextMenu.show(e.getMouseX(), e.getMouseY());
			}
		});
		
		/*
		 * Call clear (adds crosshairs)
		 */
		
		clear();
	}
	
	/**
	 * Finds and hides context menus if applicable. This is meant to be called from setOnMousePressed().
	 */
	public void hideContextMenus() {
		for (int i = 0; i < getChildren().size(); i++) {
			Node n = getChildren().get(i);
			
			if (n instanceof ContextDropdownMenu) {
				ContextDropdownMenu m = (ContextDropdownMenu) n;
				
				if (m.isActive() && !m.isMouseHoveringThis()) {
					m.hide();
				}
			}
		}
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
