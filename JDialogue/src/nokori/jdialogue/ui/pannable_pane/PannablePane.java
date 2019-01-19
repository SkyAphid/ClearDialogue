package nokori.jdialogue.ui.pannable_pane;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

/**
 * The canvas which holds all of the nodes of the application.
 * 
 * Found here:
 * https://stackoverflow.com/questions/32220042/pick-and-move-a-node-in-a-pannable-zoomable-pane
 */
public class PannablePane extends Pane {

	private DoubleProperty scale = new SimpleDoubleProperty(1.0);

	/**
	 * We limit the width and height to prevent headaches regarding node placement and configuring
	 */
	public PannablePane(int width, int height) {
		// add scale transform
		scaleXProperty().bind(scale);
		scaleYProperty().bind(scale);
		
		//Set Size
		setSize(width, height);
		
		//Set CSS
		setStyle("-fx-border-color: gray; -fx-border-width: 5; -fx-border-style: segments(50) line-cap butt;");

		// logging
		/*addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			System.out.println("canvas event: "
					+ (((event.getSceneX() - getBoundsInParent().getMinX()) / getScale()) + ", scale: " + getScale()));
			System.out.println("canvas bounds: " + getBoundsInParent());
		});*/
	}
	
	public void setSize(int width, int height) {
		setPrefSize(width, height);
		setMinSize(width, height);
		setMaxSize(width, height);
	}

	public double getScale() {
		return scale.get();
	}

	/**
	 * Set x/y scale
	 * 
	 * @param scale
	 */
	public void setScale(double scale) {
		this.scale.set(scale);
	}

	/**
	 * Set x/y pivot points
	 * 
	 * @param x
	 * @param y
	 */
	public void setPivot(double x, double y) {
		setTranslateX(getTranslateX() - x);
		setTranslateY(getTranslateY() - y);
	}
}
