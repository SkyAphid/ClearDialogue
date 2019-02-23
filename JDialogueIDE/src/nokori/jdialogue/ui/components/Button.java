package nokori.jdialogue.ui.components;


import lwjgui.Color;
import lwjgui.event.Event;
import lwjgui.geometry.Pos;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.shape.DropShadow;
import lwjgui.scene.shape.Rectangle;
import lwjgui.theme.Theme;
import lwjgui.transition.FillTransition;

/**
 * This class serves as the skeleton for all of the Toolbar buttons on the HUD.
 *
 */
public class Button extends FloatingPane {
	
	public static final int DEFAULT_HEIGHT = 50;
	
	protected static final int HIGHLIGHT_SPEED_IN_MILLIS = 200;
	
	protected static final int PADDING = 10;
	
	protected static final int CORNER_RADIUS = 3;
	protected static final float HIGHLIGHT_OPACITY = 0.25f;
	protected static final int FONT_SIZE = 26;
	
	protected DropShadow dropShadow;
	protected Rectangle background, highlight;
	
	private FillTransition fillTransition = null;
	
	private boolean highlighting = false;
	
	public Button(int absoluteX, int absoluteY, int width, int height, boolean backgroundEnabled, boolean highlightingEnabled) {
		setAbsolutePosition(absoluteX, absoluteY);
		setAlignment(Pos.CENTER_LEFT);
		
		if (backgroundEnabled) {
			dropShadow = new DropShadow(width, height);
			dropShadow.setMouseTransparent(true);
			
			background = new Rectangle(width, height, CORNER_RADIUS, Theme.current().getControl());
			background.setMouseTransparent(true);
			getChildren().addAll(dropShadow, background);
		}
		
		if (highlightingEnabled) {
			highlight = new Rectangle(width, height, CORNER_RADIUS, Color.TRANSPARENT.copy());
			highlight.setCornerRadius(CORNER_RADIUS);
			highlight.setMouseTransparent(true);
			getChildren().add(highlight);
		}

		setOnMouseEntered(e -> {
			if (highlightingEnabled) {
				//Only show the highlight if the mouse is in its bounds (that way it doesn't glow in cases where the mouse is over a divider, etc)
				if (cached_context.isMouseInside(highlight)) {
					playHighlightFillTransition(new FillTransition(HIGHLIGHT_SPEED_IN_MILLIS, highlight.getFill(), Color.WHITE_SMOKE.alpha(0.25f)));
				}
				
				highlighting = true;
			}
			
			mouseEntered(e);
		});
		
		setOnMouseExited(e -> {
			if (highlightingEnabled) {
				playHighlightFillTransition(new FillTransition(HIGHLIGHT_SPEED_IN_MILLIS, highlight.getFill(), Color.TRANSPARENT));
				highlighting = false;
			}
			
			mouseExited(e);
		});
		
		setOnMouseClicked(e -> {
			mouseClicked(e);
		});
	}
	
	/**
	 * Modifies the width of the background and dropshadow for this JDButton.
	 */
	protected void setWidth(double width) {
		background.forceWidth(width);
		dropShadow.forceWidth(width);
	}
	
	/**
	 * Modifies the height of the background and dropshadow for this JDButton.
	 */
	protected void setHeight(double height) {
		background.forceHeight(height);
		dropShadow.forceHeight(height);
	}
	
	private void playHighlightFillTransition(FillTransition transition) {
		if (fillTransition != null) {
			fillTransition.stop();
			fillTransition = null;
		}
		
		fillTransition = transition;
		fillTransition.play();
	}

	public boolean isHighlighting() {
		return highlighting;
	}

	protected void mouseEntered(Event e) {
		
	}
	
	protected void mouseExited(Event e) {
		
	}
	
	protected void mouseClicked(Event e) {
		
	}
}
