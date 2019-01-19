package nokori.jdialogue.ui.pannable_pane;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.Project;

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if
 * parent is zoomed.
 * 
 * Found here:
 * https://stackoverflow.com/questions/32220042/pick-and-move-a-node-in-a-pannable-zoomable-pane
 * 
 */
public class NodeGestures {

	private DragContext nodeDragContext = new DragContext();

	private JDialogueCore core;
	private PannablePane pannablePane;

	public NodeGestures(JDialogueCore core, PannablePane pannablePane) {
		this.core = core;
		this.pannablePane = pannablePane;
	}

	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		public void handle(MouseEvent event) {
			
			// left mouse button => dragging
			if (!event.isPrimaryButtonDown())
				return;

			nodeDragContext.mouseAnchorX = event.getSceneX();
			nodeDragContext.mouseAnchorY = event.getSceneY();

			Node node = (Node) event.getSource();

			nodeDragContext.translateAnchorX = node.getTranslateX();
			nodeDragContext.translateAnchorY = node.getTranslateY();

		}

	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			
			// left mouse button => dragging
			if (!event.isPrimaryButtonDown())
				return;

			double scale = pannablePane.getScale();

			Node node = (Node) event.getSource();
			
			node.setTranslateX(nodeDragContext.translateAnchorX + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / scale));
			node.setTranslateY(nodeDragContext.translateAnchorY + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / scale));
			
			clampToParentBounds(node);
			
			mouseDragged(event);

			event.consume();
		}
	};
	
	/**
	 * Custom callback because the normal one has to consume the event for the dragging to work correctly
	 * @param event
	 */
	public void mouseDragged(MouseEvent event) {
		
	}
	
	/**
	 * Ensure that draggable nodes don't leave bounds
	 * @param newX
	 * @param newY
	 * @return
	 */
	public boolean clampToParentBounds(Node node) {
        Bounds childBounds = node.getBoundsInLocal();
        
        double translateX = node.getTranslateX();
        double translateY = node.getTranslateY();
        
        //Bounds parentBounds = pannablePane.getLayoutBounds();
        //node.setTranslateX(clamp(translateX, parentBounds.getMinX() - childBounds.getMinX(), parentBounds.getMaxX() - childBounds.getMaxX()));
        //node.setTranslateY(clamp(translateY, parentBounds.getMinY() - childBounds.getMinY(), parentBounds.getMaxY() - childBounds.getMaxY()));
        
        //Bypasses unreliable JavaFX code for getting the PannablePane's dimensions
        Project project = core.getActiveProject();
        node.setTranslateX(clamp(translateX, 0, project.getCanvasWidth() - childBounds.getWidth()));
        node.setTranslateY(clamp(translateY, 0, project.getCanvasHeight() - childBounds.getHeight()));
        
        
        return false;
    }
	
	private static double clamp(double val, double min, double max) {
	    return Math.max(min, Math.min(max, val));
	}
}