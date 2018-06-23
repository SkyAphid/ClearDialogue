package nokori.jdialogue.util;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * 
 * Modified "DraggableNode" for JDialogue.
 * 
 * Dragging code based on
 * {@link http://blog.ngopal.com.np/2011/06/09/draggable-node-in-javafx-2-0/}
 * 
 * DraggableNode by Michael Hoffer <info@michaelhoffer.de>
 */
public class DraggableStackPane extends StackPane {

	// node position
	private double x = 0;
	private double y = 0;
	// mouse position
	private double mousex = 0;
	private double mousey = 0;
	private boolean dragging = false;
	private boolean moveToFront = true;

	public DraggableStackPane(Node... view) {
		getChildren().addAll(view);
		init();
	}

	private void init() {

		onMousePressedProperty().set(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				mousePressed(event);

				// record the current mouse X and Y position on Node
				mousex = event.getSceneX();
				mousey = event.getSceneY();

				x = getLayoutX();
				y = getLayoutY();

				if (isMoveToFront()) {
					toFront();
				}
			}
		});

		// Event Listener for MouseDragged
		onMouseDraggedProperty().set(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				// Get the exact moved X and Y

				double offsetX = event.getSceneX() - mousex;
				double offsetY = event.getSceneY() - mousey;

				x += offsetX;
				y += offsetY;

				mouseDragged(event, x, y);

				setLayoutX(x);
				setLayoutY(y);

				dragging = true;

				// again set current Mouse x AND y position
				mousex = event.getSceneX();
				mousey = event.getSceneY();

				event.consume();
			}
		});

		onMouseClickedProperty().set(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseClicked(event);
				dragging = false;
			}
		});

	}

	public void mousePressed(MouseEvent event) {

	}

	public void mouseDragged(MouseEvent event, double newX, double newY) {

	}

	public void mouseClicked(MouseEvent event) {

	}

	/**
	 * @return the dragging
	 */
	protected boolean isDragging() {
		return dragging;
	}

	/**
	 * @param moveToFront
	 *            the moveToFront to set
	 */
	public void setMoveToFront(boolean moveToFront) {
		this.moveToFront = moveToFront;
	}

	/**
	 * @return the moveToFront
	 */
	public boolean isMoveToFront() {
		return moveToFront;
	}
}
