package nokori.jdialogue.ui.node;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.CubicCurve;
import nokori.jdialogue.project.DialogueNodeConnector;
import nokori.jdialogue.ui.pannable_pane.PannablePane;

/**
 * Creates a Line that's attached to two Nodes.
 * 
 * I know this entire implementation is questionable but this was the prettiest and only solution I could get to work properly
 * 
 * Pulled from
 * https://stackoverflow.com/questions/43115807/how-to-draw-line-between-two-nodes-placed-in-different-panes-regions
 */
public class BoundLine extends CubicCurve {

	private Pane commonAncestor;
	private Arc node1, node2;
	private DialogueNodeConnector connector1, connector2;
	
	private boolean followMouseMode = false;
	
	/**
	 * Connect a single node to the mouse
	 */
	public BoundLine(Pane commonAncestor, Arc node1, DialogueNodeConnector connector1) {
		this(commonAncestor, node1, connector1, null, null);
		followMouseMode = true;
	}
	
	/**
	 * Connect two nodes together.
	 */
	public BoundLine(Pane commonAncestor, Arc node1, DialogueNodeConnector connector1, Arc node2, DialogueNodeConnector connector2) {
		
		this.commonAncestor = commonAncestor;
		this.node1 = node1;
		this.connector1 = connector1;
		
		this.node2 = node2;
		this.connector2 = connector2;
		
		setFill(Color.TRANSPARENT);
		setStrokeWidth(2);
		setStroke(Color.BLACK);

		setMouseTransparent(true);
	}
	
	/**
	 * @return false if this line is no longer valid
	 */
	public boolean update(MouseEvent event, PannablePane pannablePane) {
		//If the nodes are no longer connected, delete this line, as it's no longer valid
		//Rule only applies for connected nodes
		if (!followMouseMode && !connector1.isConnected(connector2)) return false;

		/*
		 * Calculate first point coordinates
		 */
		
		Bounds n1InCommonAncestor = getRelativeBounds(node1, commonAncestor);
		Point2D n1Center = getCenter(n1InCommonAncestor);

		double n1X = n1Center.getX();
		double n1Y = n1Center.getY();
		
		/*
		 * Calculate second point coordinates
		 */
		
		double n2X;
		double n2Y;
		
		if (followMouseMode) {
			n2X = event.getX();
			n2Y = event.getY();
			
			//System.err.println(connector1.getParent().getName() + " connector follow mouse");
		}else {
			Bounds n2InCommonAncestor = getRelativeBounds(node2, commonAncestor);
			Point2D n2Center = getCenter(n2InCommonAncestor);
			
			n2X = n2Center.getX();
			n2Y = n2Center.getY();
		}
		
		double n1ControlX = ((n2X - n1X) > 0 ? n1X + 50 : n1X - 50);
		double n1ControlY = n1Y;
		
		double n2ControlX = ((n2X - n1X) < 0 ? n2X + 50 : n2X - 50);
		double n2ControlY = n2Y;
		
		/*
		 * Update Line
		 */
		
		setStartX(n1X);
		setStartY(n1Y);
		
		setEndX(n2X);
		setEndY(n2Y);

		setControlX1(n1ControlX);
		setControlY1(n1ControlY);
		
		setControlX2(n2ControlX);
		setControlY2(n2ControlY);
		
		toBack();
		
		return true;
	}
	
	private Bounds getRelativeBounds(Node node, Node relativeTo) {
	    Bounds nodeBoundsInScene = node.localToScene(node.getBoundsInLocal());
	    return relativeTo.sceneToLocal(nodeBoundsInScene);
	}

	private Point2D getCenter(Bounds b) {
	    return new Point2D(b.getMinX() + b.getWidth() / 2, b.getMinY() + b.getHeight() / 2);
	}
}