package nokori.jdialogue.ui.dialogue_nodes;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUIDialog;
import lwjgui.LWJGUIDialog.DialogIcon;
import lwjgui.LWJGUIDialog.DialogType;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.control.ContextMenu;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.control.SeparatorMenuItem;
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
import nokori.jdialogue.ui.JDUIController;

public abstract class DialogueNode extends DraggablePane {
	
	public static final int MINI_WIDTH = 100;
	public static final int MINI_HEIGHT = 100;
	
	public static final int EXPANDED_WIDTH = MINI_WIDTH * 2;
	public static final int EXPANDED_HEIGHT = MINI_HEIGHT * 2;
	
	public static final int EDITING_WIDTH = EXPANDED_WIDTH * 6;
	public static final int EDITING_HEIGHT = EXPANDED_HEIGHT * 3;

	protected static final int TOP_PADDING = 15;
	protected static final int LEFT_PADDING = 15;
	protected static final int EDGE_PADDING = LEFT_PADDING * 2;
	
	protected boolean expanded = false;
	protected boolean editing = false;
	
	protected boolean widthResizable = true;
	protected boolean heightResizable = true;
	
	//Background
	private DropShadow dropShadow;
	protected Rectangle background;
	
	//Title/Tags
	private FloatingPane namePane, tagPane;
	private TextField name, tags;
	
	//Context menu
	protected ContextMenu contextMenu = new ContextMenu();
	
	//Data
	private Dialogue dialogue;
	
	public DialogueNode(JDUIController controller, Dialogue dialogue) {
		setAlignment(Pos.TOP_LEFT);
		
		Font sansFont = controller.getTheme().getSansFont();
		
		//setBackground(Color.BLACK)
		
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
		namePane.setMouseTransparent(true);
		
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
		tagPane.setMouseTransparent(true);

		tags = new TextField(dialogue.getTag()) {
			@Override
			public void render(Context context) {
				setMaxWidth(background.getWidth() - EDGE_PADDING);
				setPrefWidth(getMaxWidth());
				super.render(context);
			}
		};
		tags.setFont(sansFont);
		tags.setFontSize(14);

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
		 * Context Menu
		 * 
		 */

		MenuItem editNode = new MenuItem("Edit Node", sansFont);
		editNode.setOnAction(e -> {
			toggleEditing();
			
			setContextHint(controller);
			
			if (editing) {
				editNode.setContent("End Editing", sansFont, null);
			} else {
				editNode.setContent("Edit Node", sansFont, null);
			}
		});
		
		MenuItem deleteNode = new MenuItem("Delete Node", sansFont);
		deleteNode.setOnAction(e -> {
			if (LWJGUIDialog.showConfirmDialog("Delete Node", "Are you sure you want to delete this node?", 
					DialogType.YES_NO, DialogIcon.QUESTION, false)) {
				
				controller.removeDialogueNode(this);
			}
		});
		
		MenuItem toggleSize = new MenuItem("Toggle Size", sansFont);
		toggleSize.setOnAction(e -> {
			toggleExpanded(true);
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
			
			setContextHint(controller);
		});
		
		setOnMouseExited(e -> {
			new FillTransition(200, bgStrokeFill, bgStrokeFillDefault).play();;
			controller.resetContextHint();
		});
		
		setOnMouseClicked(e -> {
			if (e.getClickCount() >= 2 && e.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT && !editing) {
				toggleExpanded(true);
			}
			
			if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				contextMenu.show(getScene(), cached_context.getMouseX(), cached_context.getMouseY());
			}
		});
		
		setOnKeyPressed(e -> {
			if (editing && e.getKey() == GLFW.GLFW_KEY_ESCAPE) {
				toggleEditing();
				setContextHint(controller);
			}
		});
	}
	
	/**
	 * Sets the context hint for this Dialogue Node.
	 */
	private void setContextHint(JDUIController controller) {
		String expandHint = expanded ? "2xLMB = Contract" : "2xLMB = Expand";
		String hint = "LMB = Drag node | " + expandHint + " | RMB = Context Menu";
		
		if (editing) {
			hint += " | ESC-Key = End Editing";
		}
		
		controller.setContextHint(hint); 
	}

	/**
	 * Toggles whether or not the DialogueNode is expanded. This doesn't have an effect if the node is currently being edited.
	 * 
	 * @param changeMode - if false, this function merely acts as a "refresh" to set the dimensions back to the current setting and doesn't actually change the expand state.
	 */
	protected void toggleExpanded(boolean changeMode) {
		if (changeMode) {
			expanded = !expanded;
		}
		
		if (expanded) {
			new Resizer(200, EXPANDED_WIDTH, EXPANDED_HEIGHT).play();
		} else {
			new Resizer(200, MINI_WIDTH, MINI_HEIGHT).play();
		}
	}
	
	/**
	 * Toggles editing mode for this DialogueNode.
	 */
	protected void toggleEditing() {
		if (!editing) {
			new Resizer(200, EDITING_WIDTH, EDITING_HEIGHT).play();
			editing = true;
		} else {
			toggleExpanded(false);
			editing = false;
		}

		namePane.setMouseTransparent(!editing);
		tagPane.setMouseTransparent(!editing);
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

	public boolean isExpanded() {
		return expanded;
	}
	
	public Dialogue getDialogue() {
		return dialogue;
	}
}
