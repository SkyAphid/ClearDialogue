package nokori.jdialogue.ui.components;


import lwjgui.Color;
import lwjgui.event.Event;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.shape.DropShadow;
import lwjgui.scene.shape.Rectangle;
import lwjgui.theme.Theme;
import lwjgui.transition.ShapeFillTransition;

/**
 * This class serves as the skeleton for all of the Toolbar buttons on the HUD.
 *
 */
public class JDButtonSkeleton extends FloatingPane {
	
	public static final int DEFAULT_HEIGHT = 50;
	
	protected static final int HIGHLIGHT_SPEED_IN_MILLIS = 200;
	
	protected static final int CORNER_RADIUS = 3;
	protected static final int DROP_SHADOW_SIZE_OFFSET = 3;
	protected static final double HIGHLIGHT_OPACITY = 0.25;
	protected static final int FONT_SIZE = 26;
	
	protected static final Insets TEXT_PADDING = new Insets(10, 0, 0, 20);
	
	protected DropShadow dropShadow;
	protected Rectangle background, highlight;
	
	private ShapeFillTransition fillTransition = null;
	
	private boolean highlighted = false;
	
	public JDButtonSkeleton(int absoluteX, int absoluteY, int width, int height, boolean backgroundEnabled, boolean highlightingEnabled) {
		setAbsolutePosition(absoluteX, absoluteY);
		setAlignment(Pos.TOP_LEFT);
		
		if (backgroundEnabled) {
			dropShadow = new DropShadow(width + DROP_SHADOW_SIZE_OFFSET, height + DROP_SHADOW_SIZE_OFFSET, 5);
			dropShadow.setMouseTransparent(true);
			
			background = new Rectangle(width, height, Theme.currentTheme().getSelection());
			background.setCornerRadius(CORNER_RADIUS);
			background.setMouseTransparent(true);
			getChildren().addAll(dropShadow, background);
		}
		
		if (highlightingEnabled) {
			highlight = new Rectangle(width, height, Color.TRANSPARENT);
			highlight.setCornerRadius(CORNER_RADIUS);
			highlight.setMouseTransparent(true);
			getChildren().add(highlight);
		}
		
		setOnMouseEntered(e -> {
			if (highlightingEnabled) {
				playShapeFillTransition(new ShapeFillTransition(HIGHLIGHT_SPEED_IN_MILLIS, highlight, highlight.getFill(), Color.WHITE.opaque(HIGHLIGHT_OPACITY)));
				highlighted = true;
			}
			
			mouseEntered(e);
		});
		
		setOnMouseExited(e -> {
			if (highlightingEnabled) {
				playShapeFillTransition(new ShapeFillTransition(HIGHLIGHT_SPEED_IN_MILLIS, highlight, highlight.getFill(), Color.TRANSPARENT));
				highlighted = false;
			}
			
			mouseExited(e);
		});
		
		setOnMouseClicked(e -> {
			mouseClicked(e);
		});
	}
	
	private void playShapeFillTransition(ShapeFillTransition transition) {
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
