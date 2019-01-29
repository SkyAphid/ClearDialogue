package nokori.jdialogue.ui.pannable_pane;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import nokori.jdialogue.JDialogueCore;

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

		//Tooltip
		Tooltip tooltip = new Tooltip("Hold and drag LMB to pan viewport.\nHold and drag RMB to highlight nodes.\nUse the scroll wheel to zoom in and out on the mouse location.");
		tooltip.setShowDelay(Duration.seconds(JDialogueCore.TOOLTIP_SHOW_DELAY));
		Tooltip.install(this, tooltip);
		
		// logging
		/*addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
		 * Thank GOD for the line below. I couldn't figure out how to get the correct mouse coordinates for the life of me. 
		 * I spent HOURS trying to figure out how to apply the right scaling because of my inexperience with JavaFX
		 * But then I exhaustedly glanced up and noticed the line below and was like NO WAY! Thank you mysterious internet man for PannablePane!
			System.out.println("canvas event: " + (((event.getSceneX() - getBoundsInParent().getMinX()) / getScale()) + ", scale: " + getScale()));
			System.out.println("canvas bounds: " + getBoundsInParent());
		});*/
	}
	
	/**
	 * Adjusts the given MouseEvent and returns a mouse x-coordinate that will work with this PannablePane.
	 * @return
	 */
	public double getScaledMouseX(MouseEvent event) {
		return ((event.getSceneX() - getBoundsInParent().getMinX()) / getScale());
	}
	
	/**
	 * Adjusts the given MouseEvent and returns a mouse y-coordinate that will work with this PannablePane.
	 * @return
	 */
	public double getScaledMouseY(MouseEvent event) {
		return ((event.getSceneY() - getBoundsInParent().getMinY()) / getScale());
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
