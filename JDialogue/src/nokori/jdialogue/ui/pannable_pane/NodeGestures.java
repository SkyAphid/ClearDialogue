package nokori.jdialogue.ui.pannable_pane;

import java.util.HashMap;
import java.util.Stack;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.node.DialogueNodePane;

/**
 * Listeners for making the nodes draggable via left mouse button. Considers if
 * parent is zoomed.
 * 
 * Found here:
 * https://stackoverflow.com/questions/32220042/pick-and-move-a-node-in-a-pannable-zoomable-pane
 * 
 */
public class NodeGestures {

	private NodeDragContext nodeDragContext = new NodeDragContext();

	private JDialogueCore core;
	private PannablePane pannablePane;

	private boolean nodeSelected = false;
	private boolean nodeSnappingEnabled = false;
	
	public NodeGestures(JDialogueCore core, PannablePane pannablePane) {
		this.core = core;
		this.pannablePane = pannablePane;
	}

	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseReleasedEventHandler() {
		return onMouseReleasedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}
	
	public EventHandler<KeyEvent> getAnyKeyEventHandler() {
		return anyKeyEventHandler;
	}

	/**
	 * @return true if a node is currently being dragged around.
	 */
	public boolean isNodeSelected() {
		return nodeSelected;
	}
	
	private boolean isUsingDragControls(MouseEvent event) {
		return event.isPrimaryButtonDown();
	}
	
	/**
	 * Set the context hint for dragging nodes if one is selected
	 */
	private void updateNodeSelectedContextHint() {
		if (nodeSelected) {
			core.setContextHint("Hold Shift-key = Node Snapping (" + (nodeSnappingEnabled ? "ON" : "OFF") + ")");
		}
	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		public void handle(MouseEvent event) {
			
			nodeSelected = false;
			
			if (!isUsingDragControls(event)) {
				return;
			}

			//Set mouse anchors for scene 
			nodeDragContext.mouseAnchorX = event.getSceneX();
			nodeDragContext.mouseAnchorY = event.getSceneY();

			//Set mouse anchors for node
			Node node = (Node) event.getSource();
			nodeDragContext.translateAnchors.put(node, new Pair<>(node.getTranslateX(), node.getTranslateY()));
			
			//Set mouse anchors for multi-selected nodes
			Stack<DialogueNodePane> multiSelected = core.getAllMultiSelected();
			
			while(!multiSelected.isEmpty()) {
				DialogueNodePane n = multiSelected.pop();
				nodeDragContext.translateAnchors.put(n, new Pair<>(n.getTranslateX(), n.getTranslateY()));
			}
			
			//Turn on "Node is being selected" flag.
			nodeSelected = true;
			updateNodeSelectedContextHint();
		}

	};
	
	private EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			nodeSelected = false;
			core.setDefaultContextHint();
		}
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			
			if (!isUsingDragControls(event)) {
				return;
			}

			//Pan the selected node
			Node node = (Node) event.getSource();
			node.setTranslateX(getNodeDragTranslateX(node, event));
			node.setTranslateY(getNodeDragTranslateY(node, event));
			((DialogueNodePane) node).syncPositionOnDrag(event);
			
			Stack<DialogueNodePane> multiSelected = core.getAllMultiSelected();
			
			while(!multiSelected.isEmpty()) {
				DialogueNodePane n = multiSelected.pop();
				n.setTranslateX(getNodeDragTranslateX(n, event));
				n.setTranslateY(getNodeDragTranslateY(n, event));
				n.syncPositionOnDrag(event);
			}
			
			clampToParentBounds(node);
			
			mouseDragged(event);

			event.consume();
		}
	};

	private EventHandler<KeyEvent> anyKeyEventHandler = new EventHandler<KeyEvent>() {
		public void handle(KeyEvent event) {
			nodeSnappingEnabled = event.isShiftDown();
			updateNodeSelectedContextHint();
		}
	};
	
	private static final double GRID_SIZE_MULTIPLIER = 1.5;
	private static final double GRID_X_START_OFFSET = 80;
	private static final double GRID_Y_START_OFFSET = 70;
	
	private double getNodeDragTranslateX(Node node, MouseEvent event) {
		double x = nodeDragContext.translateAnchors.get(node).getKey() + ((event.getSceneX() - nodeDragContext.mouseAnchorX) / pannablePane.getScale());
		
		if (nodeSnappingEnabled) {
			double nodeWidth = DialogueNodePane.WIDTH;
			double snapXGrid = nodeWidth * GRID_SIZE_MULTIPLIER;
			return GRID_X_START_OFFSET + (x - (x % snapXGrid));
		} else {
			return x;
		}
	}
	
	private double getNodeDragTranslateY(Node node, MouseEvent event) {
		double y = nodeDragContext.translateAnchors.get(node).getValue() + ((event.getSceneY() - nodeDragContext.mouseAnchorY) / pannablePane.getScale());
		
		if (nodeSnappingEnabled) {
			double nodeHeight = DialogueNodePane.HEIGHT;
			double snapYGrid = nodeHeight * GRID_SIZE_MULTIPLIER;
			return GRID_Y_START_OFFSET + (y - (y % snapYGrid));
		} else {
			return y;
		}
	}
	
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

class NodeDragContext {

	double mouseAnchorX;
	double mouseAnchorY;
	
	HashMap<Node, Pair<Double, Double>> translateAnchors = new HashMap<Node, Pair<Double, Double>>();
}