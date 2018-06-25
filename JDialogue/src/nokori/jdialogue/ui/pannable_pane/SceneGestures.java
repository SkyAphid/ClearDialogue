package nokori.jdialogue.ui.pannable_pane;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Listeners for making the scene's canvas draggable and zoomable
 */
public class SceneGestures {

	private static final double MAX_SCALE = 1.0;
	private static final double MIN_SCALE = 0.1;

	private DragContext sceneDragContext = new DragContext();

	PannablePane pannablePane;

	public SceneGestures(PannablePane pannablePane) {
		this.pannablePane = pannablePane;
	}

	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	public EventHandler<ScrollEvent> getOnScrollEventHandler() {
		return onScrollEventHandler;
	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		public void handle(MouseEvent event) {

			// middle mouse button => panning
			//if (!event.isMiddleButtonDown())
			//	return;

			sceneDragContext.mouseAnchorX = event.getSceneX();
			sceneDragContext.mouseAnchorY = event.getSceneY();

			sceneDragContext.translateAnchorX = pannablePane.getTranslateX();
			sceneDragContext.translateAnchorY = pannablePane.getTranslateY();

		}

	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {

			// middle mouse button => panning
			//if (!event.isMiddleButtonDown())
			//	return;

			pannablePane.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
			pannablePane.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

			mouseDragged(event);
			
			event.consume();
		}
	};
	
	public void mouseDragged(MouseEvent event) {
		
	}

	/**
	 * Mouse wheel handler: zoom to pivot point
	 */
	private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

		@Override
		public void handle(ScrollEvent event) {

			double delta = 1.2;

			double scale = pannablePane.getScale(); // currently we only use Y, same value is used for X
			double oldScale = scale;

			if (event.getDeltaY() < 0) {
				scale /= delta;
			} else {
				scale *= delta;
			}

			scale = clamp(scale, MIN_SCALE, MAX_SCALE);

			double f = (scale / oldScale) - 1;

			double dx = (event.getSceneX() - (pannablePane.getBoundsInParent().getWidth()/2 + pannablePane.getBoundsInParent().getMinX()));
			double dy = (event.getSceneY() - (pannablePane.getBoundsInParent().getHeight()/2 + pannablePane.getBoundsInParent().getMinY()));

			pannablePane.setScale(scale);

			// note: pivot value must be untransformed, i. e. without scaling
			pannablePane.setPivot(f * dx, f * dy);

			event.consume();

		}

	};

	public static double clamp(double value, double min, double max) {

		if (Double.compare(value, min) < 0)
			return min;

		if (Double.compare(value, max) > 0)
			return max;

		return value;
	}
}