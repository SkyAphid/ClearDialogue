package nokori.jdialogue.ui.components;


import lwjgui.Color;
import lwjgui.geometry.Pos;
import lwjgui.scene.layout.FloatingPane;
import lwjgui.scene.shape.DropShadow;
import lwjgui.scene.shape.Rectangle;
import lwjgui.transition.Transition;

/**
 * This class serves as the skeleton for all of the Toolbar buttons on the HUD.
 * 
 * @author Brayden
 *
 */
public class JDButtonSkeleton extends FloatingPane {
	
	private static final int CORNER_RADIUS = 3;
	private static final int DROP_SHADOW_SIZE_OFFSET = 3;
	private static final int HIGHLIGHT_SPEED_IN_MILLIS = 1000;
	
	private Color defaultFill;
	
	private DropShadow dropShadow;
	private Rectangle background;
	
	public JDButtonSkeleton(int width, int height, Color backgroundFill) {
		defaultFill = backgroundFill;
		
		setAlignment(Pos.TOP_LEFT);
		
		dropShadow = new DropShadow(width + DROP_SHADOW_SIZE_OFFSET, height + DROP_SHADOW_SIZE_OFFSET, 5);
		dropShadow.setMouseTransparent(true);
		
		background = new Rectangle(width, height, backgroundFill);
		background.setCornerRadius(CORNER_RADIUS);
		background.setMouseTransparent(true);
		
		setMouseEnteredEvent(e -> {
			setHighlighted(true);
			System.err.println("Mouse Entered");
		});
		
		setMouseExitedEvent(e -> {
			setHighlighted(false);
			System.err.println("Mouse Exited");
		});
		
		getChildren().addAll(dropShadow, background);
	}
	
	/**
	 * Toggles the animations for highlighting this JDButton.
	 */
	private void setHighlighted(boolean highlighted) {
		Color fromFill = new Color(background.getFill());
		
		if (highlighted) {
			//Highlight the background on select
			RectangleFillTransition transition = new RectangleFillTransition(HIGHLIGHT_SPEED_IN_MILLIS, background, fromFill, Color.CORAL.brighter());
			transition.play();
		} else {
			//Fade back to the normal color
			RectangleFillTransition transition = new RectangleFillTransition(HIGHLIGHT_SPEED_IN_MILLIS, background, fromFill, defaultFill);
			transition.play();
		}
	}
	
	public void setHeight(double height) {
		dropShadow.setPrefHeight(height + DROP_SHADOW_SIZE_OFFSET);
		background.setPrefHeight(height);
	}
}
