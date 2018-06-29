package nokori.jdialogue.ui.util;

import javafx.animation.Transition;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class RectangleResizeHeightTransition extends Transition {

	private Rectangle rectangle;
	private double startHeight;
	private double endHeight;

	public RectangleResizeHeightTransition(Duration duration, Rectangle rectangle, double endHeight) {
		setCycleDuration(duration);
		this.rectangle = rectangle;
		this.startHeight = rectangle.getHeight();
		this.endHeight = endHeight;
	}

	@Override
	protected void interpolate(double fraction) {
		rectangle.setHeight(startHeight + ((endHeight - startHeight) * fraction));
	}
}