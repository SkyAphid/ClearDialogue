package nokori.jdialogue.ui.pannable_pane;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.ui.node.DialogueNodePane;
import nokori.jdialogue.ui.util.JDialogueUtils;

/**
 * Listeners for making the scenes canvas draggable and zoomable
 * 
 * Found here:
 * https://stackoverflow.com/questions/32220042/pick-and-move-a-node-in-a-pannable-zoomable-pane
 * 
 */
public class SceneGestures {

	private static final double MAX_SCALE = 1.0;
	private static final double MIN_SCALE = 0.1;

	private SceneDragContext sceneDragContext = new SceneDragContext();

	private JDialogueCore core;
	private PannablePane pannablePane;
	private NodeGestures nodeGestures;
	
	private RectangleHighlightNode mouseHighlighter = null;
	
	public SceneGestures(JDialogueCore core, PannablePane pannablePane, NodeGestures nodeGestures) {
		this.core = core;
		this.pannablePane = pannablePane;
		this.nodeGestures = nodeGestures;
	}

	public SceneDragContext getSceneDragContext() {
		return sceneDragContext;
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

	public EventHandler<ScrollEvent> getOnScrollEventHandler() {
		return onScrollEventHandler;
	}
	
	/**
	 * Inputs for panning the viewport
	 */
	private boolean isUsingPanningControls(MouseEvent event) {
		return (event.isPrimaryButtonDown() && !event.isSecondaryButtonDown());
	}
	
	/**
	 * Inputs for highlighting nodes
	 */
	private boolean isUsingHighlightingControls(MouseEvent event) {
		return (!event.isPrimaryButtonDown() && event.isSecondaryButtonDown());
	}
	
	/**
	 * Used as the default JDialogueCore context hint. I've put the function here because this class is where you change the controls if needed.
	 * @return
	 */
	public static String getSceneContextHint() {
		return "Drag LMB = Pan viewport | Drag RMB = Multi-Select | Scroll Wheel = Zoom in/out on mouse position";
	}
	
	/**
	 * Get the context hint for when multiple nodes are selected with the highlighter.
	 * @param nodesSelected
	 * @return
	 */
	public static String getMultiSelectContextHint(int nodesSelected) {
		return "Nodes selected: " + nodesSelected + " | Drag LMB = Drag all nodes | RMB = De-select all\nT-Key = Add tag to all | R-Key = Remove tags from all";
	}
	
	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		public void handle(MouseEvent event) {
			/*
			 * Panning
			 */
			if (isUsingPanningControls(event)) {
				sceneDragContext.mouseAnchorX = event.getSceneX();
				sceneDragContext.mouseAnchorY = event.getSceneY();
			
				sceneDragContext.translateAnchorX = pannablePane.getTranslateX();
				sceneDragContext.translateAnchorY = pannablePane.getTranslateY();
			}
			
			/*
			 * Highlighting
			 */
			if (isUsingHighlightingControls(event) && !nodeGestures.isNodeSelected()) {

				//Clear all selected from the last highlight
				core.setDefaultContextHint();

				for (int i = 0; i < pannablePane.getChildren().size(); i++) {
					Node node = pannablePane.getChildren().get(i);

					if (node instanceof DialogueNodePane) {
						DialogueNodePane dNode = (DialogueNodePane) node;
						dNode.setMultiSelected(false);
					}
				}

				//Create new highlighter at the mouse coordinates
				setMouseHighlighter(new RectangleHighlightNode(getClampedScaledMouseX(event), getClampedScaledMouseY(event)));
			}
			
		}

	};
	
	private EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			setMouseHighlighter(null);
		}
	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {

			/*
			 * Panning
			 */
			if (isUsingPanningControls(event)) {
				double newTranslateX = sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX;
				double newTranslateY = sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY;
				
				pannablePane.setTranslateX(newTranslateX);
				pannablePane.setTranslateY(newTranslateY);
	
				mouseDragged(event, newTranslateX, newTranslateY);
			}
			
			/*
			 * Highlighting
			 */
			if (isUsingHighlightingControls(event) && mouseHighlighter != null) {
				mouseHighlighter.update(getClampedScaledMouseX(event), getClampedScaledMouseY(event));
				
				//Run through all the DialogueNodePanes and update the ones within the highlighter to be multi-selected
				for (int i = 0; i < pannablePane.getChildren().size(); i++) {
					Node node = pannablePane.getChildren().get(i);
					
					if (node instanceof DialogueNodePane) {
						DialogueNodePane dNode = (DialogueNodePane) node;
						
						boolean bSelected = dNode.isMultiSelected();
						boolean selected = mouseHighlighter.getBoundsInParent().intersects(dNode.getBoundsInParent());
						dNode.setMultiSelected(selected);
						
						//Context hint updated every time a new node is highlighted
						if (!bSelected && selected) {
							core.setContextHint(getMultiSelectContextHint(core.getNumMultiSelected()));
						}
					}
				}
			}
			
			event.consume();
		}
	};
	
	private double getClampedScaledMouseX(MouseEvent event) {
		return JDialogueUtils.clamp(pannablePane.getScaledMouseX(event), 0, pannablePane.getWidth());
	}
	
	private double getClampedScaledMouseY(MouseEvent event) {
		return JDialogueUtils.clamp(pannablePane.getScaledMouseY(event), 0, pannablePane.getHeight());
	}
	
	private void setMouseHighlighter(RectangleHighlightNode h) {
		if (mouseHighlighter != null) {
			pannablePane.getChildren().remove(mouseHighlighter);
		}
		
		mouseHighlighter = h;
		
		if (h != null) {
			pannablePane.getChildren().add(mouseHighlighter);
		}
	}
	
	public void mouseDragged(MouseEvent event, double newTranslateX, double newTranslateY) {
		
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

			scale = JDialogueUtils.clamp(scale, MIN_SCALE, MAX_SCALE);

			double f = (scale / oldScale) - 1;

			double dx = (event.getSceneX() - (pannablePane.getBoundsInParent().getWidth()/2 + pannablePane.getBoundsInParent().getMinX()));
			double dy = (event.getSceneY() - (pannablePane.getBoundsInParent().getHeight()/2 + pannablePane.getBoundsInParent().getMinY()));

			pannablePane.setScale(scale);

			// note: pivot value must be untransformed, i. e. without scaling
			pannablePane.setPivot(f * dx, f * dy);
			
			mouseScrolled(event, scale);

			event.consume();
		}

	};
	
	public void mouseScrolled(ScrollEvent event, double newScale) {
		
	}
}

class SceneDragContext {

	double mouseAnchorX;
	double mouseAnchorY;

	double translateAnchorX;
	double translateAnchorY;
}