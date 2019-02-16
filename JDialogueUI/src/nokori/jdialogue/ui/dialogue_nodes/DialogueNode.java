package nokori.jdialogue.ui.dialogue_nodes;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.geometry.Pos;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.floating.DraggablePane;
import lwjgui.scene.shape.DropShadow;
import lwjgui.scene.shape.Rectangle;
import lwjgui.theme.Theme;
import lwjgui.transition.FillTransition;
import lwjgui.transition.SizeTransition;
import nokori.jdialogue.project.Dialogue;

public class DialogueNode extends DraggablePane {
	
	protected static final int MINI_WIDTH = 80;
	protected static final int MINI_HEIGHT = 80;
	
	protected static final int EXPANDED_WIDTH = MINI_WIDTH * 2;
	protected static final int EXPANDED_HEIGHT = MINI_HEIGHT * 2;
	
	private boolean highlighted = false;
	private boolean expanded = false;
	
	//Components
	private DropShadow dropShadow;
	private Rectangle background;
	
	private Label title, tags;
	
	public DialogueNode(Dialogue dialogue, Font sansFont, Font serifFont) {
		setAlignment(Pos.CENTER);
		
		/*
		 * Background
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
		
		

		/*
		 * Events
		 */
		
		setOnMouseEntered(e -> {
			new FillTransition(200, bgStrokeFill, bgStrokeFillSelected).play();
			highlighted = true;
		});
		
		setOnMouseExited(e -> {
			new FillTransition(200, bgStrokeFill, bgStrokeFillDefault).play();
			highlighted = false;
		});
		
		setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
					System.err.println("Edit Node");
				}
				
				if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
					toggleExpanded();
				}
			}
		});
		
		setOnKeyPressed(e -> {
			if (highlighted) {
				if (e.getKey() == GLFW.GLFW_KEY_D) {
					System.err.println("Delete node");
				}
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
}
