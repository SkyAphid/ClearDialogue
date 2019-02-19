package nokori.jdialogue.ui.dialogue_nodes;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.control.ContextMenu;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.control.text_input.TextField;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.floating.DraggablePane;
import lwjgui.scene.shape.DropShadow;
import lwjgui.scene.shape.Rectangle;
import lwjgui.theme.Theme;
import lwjgui.transition.FillTransition;
import lwjgui.transition.SizeTransition;
import nokori.jdialogue.project.Dialogue;
import nokori.jdialogue.ui.JDUIController;

public abstract class DialogueNode extends DraggablePane {
	
	public static final int MINI_WIDTH = 80;
	public static final int MINI_HEIGHT = 80;
	
	public static final int EXPANDED_WIDTH = MINI_WIDTH * 2;
	public static final int EXPANDED_HEIGHT = MINI_HEIGHT * 2;
	
	private boolean expanded = false;
	
	//Background
	private DropShadow dropShadow;
	private Rectangle background;
	
	//Title/Tags
	private TextField title, tags;
	
	//Data
	private Dialogue dialogue;
	
	public DialogueNode(JDUIController controller, Dialogue dialogue, Font sansFont, Font serifFont) {
		setAbsolutePosition(dialogue.getX(), dialogue.getY());
		setAlignment(Pos.TOP_LEFT);
		
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
		
		title = new TextField(dialogue.getName()) {
			@Override
			public void render(Context context) {
				setMaxWidth(DialogueNode.this.getWidth()-10);
				super.render(context);
			}
		};
		title.setFont(sansFont);
		title.setFontSize(22);
		title.setMouseTransparent(true);
		
		title.setBackground(null);
		title.setEditable(false);
		title.setDecorated(false);
		title.setSelectionOutlineEnabled(false);
		title.setPadding(new Insets(5, 0, 0, 5));
		
		tags = new TextField(dialogue.getTag()) {
			@Override
			public void render(Context context) {
				setMaxWidth(DialogueNode.this.getWidth()-10);
				super.render(context);
			}
		};
		tags.setFontSize(14);
		tags.setMouseTransparent(true);
		
		getChildren().addAll(title);
		
		/*
		 * 
		 * Context Menu
		 * 
		 */
		
		ContextMenu contextMenu = new ContextMenu();
		
		MenuItem editNode = new MenuItem("Edit Node");
		editNode.setOnAction(e -> {
			System.out.println("Open node editor");
		});
		
		MenuItem deleteNode = new MenuItem("Delete Node");
		deleteNode.setOnAction(e -> {
			controller.removeDialogueNode(this);
		});
		
		contextMenu.getItems().addAll(editNode, deleteNode);
		contextMenu.setAutoHide(false);

		/*
		 * 
		 * Events
		 * 
		 */
		
		setOnMouseEntered(e -> {
			new FillTransition(200, bgStrokeFill, bgStrokeFillSelected).play();
			
			String expandHint = expanded ? "2xLMB = Contract" : "2xLMB = Expand";
			controller.setContextHint("LMB = Drag node | " + expandHint + " | RMB = Context Menu"); 
		});
		
		setOnMouseExited(e -> {
			new FillTransition(200, bgStrokeFill, bgStrokeFillDefault).play();;
			controller.resetContextHint();
		});
		
		setOnMouseClicked(e -> {
			if (e.getClickCount() >= 2 && e.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				toggleExpanded();
			}
				
			if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
				contextMenu.show(getScene(), cached_context.getMouseX(), cached_context.getMouseY());
			}
		});
	}
	
	public void toggleExpanded() {
		if (!expanded) {
			new Resizer(200, EXPANDED_WIDTH, EXPANDED_HEIGHT).play();
			expanded = true;
		} else {
			new Resizer(200, MINI_WIDTH, MINI_HEIGHT).play();
			expanded = false;
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
	
	public void setWidth(double width) {
		background.setPrefWidth(width);
		dropShadow.setPrefWidth(width);
	}
	
	public void setHeight(double height) {
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
