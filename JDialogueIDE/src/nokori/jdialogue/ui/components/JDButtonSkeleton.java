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
public class JDButtonSkeleton extends FloatingPane {
	
	public static final int DEFAULT_HEIGHT = 50;
	
	protected static final int HIGHLIGHT_SPEED_IN_MILLIS = 200;
	
	protected static final int PADDING = 10;
	
	protected static final int CORNER_RADIUS = 3;
	protected static final float HIGHLIGHT_OPACITY = 0.25f;
	protected static final int FONT_SIZE = 26;
	
	protected DropShadow dropShadow;
	protected Rectangle background, highlight;
	
	private FillTransition fillTransition = null;
	
	private boolean highlighted = false;
	
	public JDButtonSkeleton(int absoluteX, int absoluteY, int width, int height, boolean backgroundEnabled, boolean highlightingEnabled) {
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
				playHighlightFillTransition(new FillTransition(HIGHLIGHT_SPEED_IN_MILLIS, highlight.getFill(), Color.WHITE_SMOKE.alpha(0.25f)));
				highlighted = true;
			}
			
			mouseEntered(e);
		});
		
		setOnMouseExited(e -> {
			if (highlightingEnabled) {
				playHighlightFillTransition(new FillTransition(HIGHLIGHT_SPEED_IN_MILLIS, highlight.getFill(), Color.TRANSPARENT));
				highlighted = false;
			}
			
			mouseExited(e);
		});
		
		setOnMouseClicked(e -> {
			mouseClicked(e);
		});
	}
	
	private void playHighlightFillTransition(FillTransition transition) {
		if (fillTransition != null) {
			fillTransition.stop();
			fillTransition = null;
		}
		
		fillTransition = transition;
		fillTransition.play();
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	protected void mouseEntered(Event e) {
		
	}
	
	protected void mouseExited(Event e) {
		
	}
	
	protected void mouseClicked(Event e) {
		
	}
}
