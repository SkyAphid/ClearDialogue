package nokori.jdialogue.ui.dialogue_nodes;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIDialog;
import lwjgui.LWJGUIDialog.DialogIcon;
import lwjgui.LWJGUIDialog.DialogType;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.control.ContextMenu;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.control.SeparatorMenuItem;
import lwjgui.scene.control.text_input.TextArea;
import lwjgui.scene.control.text_input.TextField;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.floating.DraggablePane;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.shape.DropShadow;
import lwjgui.scene.shape.Rectangle;
import lwjgui.theme.Theme;
import lwjgui.transition.FillTransition;
import lwjgui.transition.SizeTransition;
import nokori.jdialogue.project.Dialogue;
import nokori.jdialogue.project.DialogueConnector;
import nokori.jdialogue.ui.SharedResources;
import nokori.jdialogue.ui.dialogue_nodes.DialogueConnectorNode.ConnectorType;

public abstract class DialogueNode extends DraggablePane {
	
	public static final int MINI_WIDTH = 100;
	public static final int MINI_HEIGHT = 100;
	
	public static final int EXPANDED_WIDTH = MINI_WIDTH * 2;
	public static final int EXPANDED_HEIGHT = MINI_HEIGHT * 2;
	
	public static final int EDITING_WIDTH = EXPANDED_WIDTH * 6;
	public static final int EDITING_HEIGHT = EXPANDED_HEIGHT * 3;

	protected static final int TOP_PADDING = 20;
	protected static final int LEFT_PADDING = 15;
	protected static final int EDGE_PADDING = LEFT_PADDING * 2;
	
	protected static final int TEXT_AREA_TOP_PADDING = 70;
	
	protected static final int CONNECTOR_OFFSET = 1;
	
	protected SharedResources sharedResources;
	protected Dialogue dialogue;
	
	protected boolean expanded = false;
	protected boolean editing = false;
	
	protected boolean widthResizable = true;
	protected boolean heightResizable = true;
	
	//Background
	private DropShadow dropShadow;
	protected Rectangle background;
	
	//In-Connector
	protected DialogueConnectorNode inConnector;
	
	//Title/Tags
	private FloatingPane namePane, tagPane;
	private TextField name, tags;
	
	//Context menu
	protected ContextMenu contextMenu = new ContextMenu();
	
	public DialogueNode(SharedResources sharedResources, Dialogue dialogue) {
		this.sharedResources = sharedResources;
		this.dialogue = dialogue;
		
		setAlignment(Pos.TOP_LEFT);
		Font sansFont = sharedResources.getTheme().getSansFont();
		
		//for debugging
		setBackground(Color.BLACK);
		
		/*
		 * 
		 * Background
		 * 
		 */
		
		dropShadow = new DropShadow(MINI_WIDTH, MINI_HEIGHT, 3, 3, 5);
		dropShadow.setMouseTransparent(true);
		
		Color bgStrokeFillDefault = Theme.current().getBackground().darker().darker();
		Color bgStrokeFillSelected = Theme.current().getControl();
		Color bgStrokeFill = bgStrokeFillDefault.copy();
		
		background = new Rectangle(MINI_WIDTH, MINI_HEIGHT, 2, Theme.current().getBackground());
		background.setStrokeFill(bgStrokeFill);
		background.setMouseTransparent(true);
		
		getChildren().addAll(dropShadow, background);
		
		/*
		 * 
		 * Content
		 * 
		 */
		
		//Title field
		namePane = new FloatingPane();
		namePane.setAbsolutePosition(LEFT_PADDING, TOP_PADDING);
		namePane.setAlignment(Pos.TOP_LEFT);
		namePane.setBackground(null);
		namePane.setMouseTransparent(!editing);

		name = new TextField(dialogue.getName()) {
			@Override
			public void render(Context context) {
				setMaxWidth(background.getWidth() - EDGE_PADDING);
				setPrefWidth(getMaxWidth());
				super.render(context);
			}
		};

		name.setFont(sansFont);
		name.setFontSize(22);
		
		name.setBackground(null);
		name.setDecorated(false);
		name.setSelectionOutlineEnabled(false);
		name.setPadding(new Insets(0, 0, 1, 0));
		name.setPaddingColor(Theme.current().getBackground().darker());
		
		name.setOnTextInput(e -> {
			dialogue.setName(name.getText());
		});
		
		namePane.getChildren().add(name);
		
		//Tag field
		tagPane = new FloatingPane();
		tagPane.setAbsolutePosition(LEFT_PADDING, TOP_PADDING + 40);
		tagPane.setAlignment(Pos.TOP_LEFT);
		tagPane.setBackground(null);
		tagPane.setMouseTransparent(!editing);
		
		tags = new TextField(dialogue.getTag()) {
			@Override
			public void render(Context context) {
				setMaxWidth(background.getWidth() - EDGE_PADDING);
				setPrefWidth(getMaxWidth());
				super.render(context);
			}
		};
		
		tags.setFont(sansFont);
		tags.setFontSize(16);

		tags.setBackground(null);
		tags.setDecorated(false);
		tags.setSelectionOutlineEnabled(false);
		tags.setPadding(new Insets(0, 0, 1, 0));
		tags.setPaddingColor(Theme.current().getBackground().darker());
		
		tags.setOnTextInput(e -> {
			dialogue.setTag(tags.getText());
		});
		
		tags.setContextMenu(contextMenu);
		
		tagPane.getChildren().add(tags);
		
		getChildren().addAll(namePane, tagPane);
		
		/*
		 * 
		 * In Connector
		 * 
		 * This is a default connector that every node will have. Out Connectors on the other hand will vary.
		 * 
		 */
		
		inConnector = new DialogueConnectorNode(this, dialogue.getInConnector(), ConnectorType.IN);
		getChildren().add(0, inConnector);
		
		/*
		 * 
		 * Context Menu
		 * 
		 */

		MenuItem editNode = new MenuItem("Edit Node", sansFont) {
			@Override
			public void render(Context context) {
				if (editing) {
					setContent("End Editing", sansFont, null);
				} else {
					setContent("Edit Node", sansFont, null);
				}
				
				super.render(context);
			}
		};
		editNode.setOnAction(e -> {
			setEditing(!editing);
			setDialogueNodeContextHint();
		});
		
		MenuItem deleteNode = new MenuItem("Delete Node", sansFont);
		deleteNode.setOnAction(e -> {
			if (LWJGUIDialog.showConfirmDialog("Delete Node", "Are you sure you want to delete this node?", 
					DialogType.YES_NO, DialogIcon.QUESTION, false)) {
				
				sharedResources.removeDialogueNode(this);
			}
		});
		
		MenuItem toggleSize = new MenuItem("Toggle Size", sansFont);
		toggleSize.setOnAction(e -> {
			setEditing(false);
			setExpanded(!expanded);
		});
		
		contextMenu.getItems().addAll(editNode, deleteNode, new SeparatorMenuItem(), toggleSize);
		contextMenu.setAutoHide(false);

		/*
		 * 
		 * Events
		 * 
		 */
		
		setOnMouseEntered(e -> {
			new FillTransition(200, bgStrokeFill, bgStrokeFillSelected).play();
			setDialogueNodeContextHint();
		});
		
		setOnMouseExited(e -> {
			new FillTransition(200, bgStrokeFill, bgStrokeFillDefault).play();;
			sharedResources.resetContextHint();
		});
		
		setOnMouseClicked(e -> {
			if (e.getClickCount() >= 2 && e.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT && !editing) {
				setExpanded(!expanded);
			}
			
			if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				contextMenu.show(getScene(), cached_context.getMouseX(), cached_context.getMouseY());
			}
		});
		
		setOnKeyPressed(e -> {
			if (editing && e.getKey() == GLFW.GLFW_KEY_ESCAPE) {
				setEditing(false);
				setDialogueNodeContextHint();
			}
		});
		
		/*
		 * 
		 * Sync to Dialogue later (that way everything is finished initializing by the time we call it)
		 * 
		 */
		
		LWJGUI.runLater(()-> {
			setExpanded(dialogue.isExpanded());
			setAbsolutePosition(dialogue.getX(), dialogue.getY());
		});
	}
	
	/**
	 * Updates the dialogue's position every time this Node is dragged by the mouse.
	 */
	@Override
	public void drag(MouseEvent e) {
		super.drag(e);
		dialogue.setPosition(getX(), getY());
	}
	
	/**
	 * Sets the context hint for this Dialogue Node.
	 */
	private void setDialogueNodeContextHint() {
		String expandHint = expanded ? "2xLMB = Contract" : "2xLMB = Expand";
		String hint = "LMB = Drag node | " + expandHint + " | RMB = Context Menu";
		
		if (editing) {
			hint += " | ESC-Key = End Editing";
		}
		
		sharedResources.setContextHint(hint); 
	}

	/**
	 * Toggles editing mode for this DialogueNode.
	 */
	protected void setEditing(boolean editing) {
		this.editing = editing;
		
		if (editing) {
			new Resizer(200, EDITING_WIDTH, EDITING_HEIGHT).play();
		} else {
			refreshExpanded();
		}

		namePane.setMouseTransparent(!editing);
		tagPane.setMouseTransparent(!editing);
	}
	
	public Dialogue getDialogue() {
		return dialogue;
	}

	/*
	 * 
	 * 
	 * Dialogue Connector tools
	 * 
	 * 
	 */
	
	public DialogueConnectorNode getDialogueNodeConnectorOf(DialogueConnector connector) {
		if (inConnector.getConnector() == connector) {
			return inConnector;
		}
		
		return null;
	}
	
	protected double getInConnectorX(double connectorWidth) {
		return (getX() - connectorWidth + CONNECTOR_OFFSET);
	}
	
	protected double getOutConnectorX() {
		double parentX = getX();
		double parentW = background.getWidth();
		
		return (parentX + parentW - CONNECTOR_OFFSET);
	}
	
	protected double getCenteredConnectorY(double connectorHeight) {
		return (getY() + background.getHeight()/2 - connectorHeight/2);
	}
	
	/*
	 * 
	 * 
	 * Expand/Contract Tools
	 * 
	 * 
	 */
	
	public void setWidth(double width) {
		if (!widthResizable) return;
		
		background.setPrefWidth(width);
		dropShadow.setPrefWidth(width);
	}
	
	public void setHeight(double height) {
		if (!heightResizable) return;
		
		background.setPrefHeight(height);
		dropShadow.setPrefHeight(height);
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
		refreshExpanded();
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
	private void refreshExpanded() {
		if (expanded) {
			new Resizer(200, EXPANDED_WIDTH, EXPANDED_HEIGHT).play();
		} else {
			new Resizer(200, MINI_WIDTH, MINI_HEIGHT).play();
		}
	}
	
	private class Resizer extends SizeTransition {

		public Resizer(long durationInMillis, double targetWidth, double targetHeight) {
			super(durationInMillis, targetWidth, targetHeight);
		}
		
		@Override
		protected double getCurrentWidth() {
			return background.getWidth();
		}

		@Override
		protected double getCurrentHeight() {
			return background.getHeight();
		}

		@Override
		protected void setWidth(double width) {
			DialogueNode.this.setWidth(width);
		}

		@Override
		protected void setHeight(double height) {
			DialogueNode.this.setHeight(height);
		}
	}
}
