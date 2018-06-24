package nokori.jdialogue.ui;

import java.awt.Event;
import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueNodeConnector;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueNodePane extends StackPane {
	//Basic background dimension data that all nodes should share in-common
	public static final int WIDTH = 200;
	public static final int HEIGHT = 200;
	public static final int TITLE_HEIGHT = 40;
	
	//Connector information that all nodes should have in-common
	public static final int CONNECTOR_RADIUS = 20;
	
	public static final Color outConnectorColor = Color.rgb(52, 205, 112);
	public static final Color inConnectorColor = Color.rgb(228, 80, 65);
	
	//Instances
	protected DialogueNode node;
	private Rectangle outline, background;
	
	//Connector system
	private ArrayList<BoundLine> connectorLines = new ArrayList<BoundLine>();

	public DialogueNodePane(JDialogueCore core, DialogueNode node, DropShadow shadow, Font titleFont) {
		this.node = node;
		
		//Outline for highlighting node
		outline = new Rectangle(WIDTH, HEIGHT);
		outline.setFill(Color.TRANSPARENT);
		outline.setStroke(Color.CORAL);
		outline.setOpacity(0.0);
		outline.setMouseTransparent(true);
		
		//Background
		background = new Rectangle(WIDTH, HEIGHT);
		background.setFill(Color.WHITE);
		background.setStroke(Color.LIGHTGRAY);
		background.setEffect(shadow);
		background.setMouseTransparent(true);
		
		//In-Connector
		Arc connector = new Arc();
		connector.setFill(inConnectorColor);
		connector.setRadiusX(CONNECTOR_RADIUS);
		connector.setRadiusY(CONNECTOR_RADIUS);
		connector.setStartAngle(90);
		connector.setLength(180);
		
		connector.setOnMouseClicked(event -> {
			connectorClicked(core, connector, node.getInConnector());
		});
		
		StackPane.setAlignment(connector, Pos.CENTER_LEFT);
		StackPane.setMargin(connector, new Insets(0, 0, 0, -CONNECTOR_RADIUS));
		
		//Title text
		Text title = new Text(node.getName());
		title.setFont(titleFont);
		title.setFill(Color.BLACK);
		title.setMouseTransparent(true);
		
		StackPane.setAlignment(title, Pos.TOP_CENTER);
		StackPane.setMargin(title, new Insets(10, 0, 0, 0));
		
		//Separator
		Line separator = new Line();
		separator.setFill(Color.BLACK);
		separator.setStartX(background.getLayoutX());
		separator.setStartY(background.getLayoutY());
		separator.endXProperty().bind(background.layoutXProperty().add(WIDTH - 20));
		separator.endYProperty().bind(background.layoutYProperty());
		separator.setMouseTransparent(true);
		
		StackPane.setAlignment(separator, Pos.TOP_CENTER);
		StackPane.setMargin(separator, new Insets(TITLE_HEIGHT, 0, 0, 0));
		
		//Configure pane
		getChildren().addAll(background, connector, title, separator, outline);
		
		setOnMousePressed(event -> {
			if (!event.isPrimaryButtonDown()) return;
			
			core.getScene().setCursor(Cursor.CLOSED_HAND);
		});
		
		setOnMouseReleased(event -> {
			core.getScene().setCursor(Cursor.DEFAULT);
		});
		
		setOnMouseEntered(event -> {
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(Button.FADE_TIME), outline);
			fadeTransition.setFromValue(outline.getOpacity());
			fadeTransition.setToValue(1.0);
			fadeTransition.play();
		});
		
		setOnMouseExited(event -> {
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(Button.FADE_TIME), outline);
			fadeTransition.setFromValue(outline.getOpacity());
			fadeTransition.setToValue(0.0);
			fadeTransition.play();
		});
		
		setOnMouseDragged(event -> {
			for (int i = 0; i < connectorLines.size(); i++) {
				BoundLine line = connectorLines.get(i);
				
				if (line.update()) {
					continue;
				}else {
					connectorLines.remove(i);
					i--;
				}
			}

			//Ensures that the dragging works, was originally called in NodeGestures, but moved here so that we could add the above functionality
			event.consume();
		});
		
		//Initial animation
		int animTime = 200;
		
		RotateTransition rotateTransition = new RotateTransition(Duration.millis(animTime), this);
		rotateTransition.setFromAngle(45.0);
		rotateTransition.setToAngle(0.0);
		rotateTransition.play();
		
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(animTime), this);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}
	
	public void setBackgroundHeight(int height) {
		outline.setHeight(height);
		background.setHeight(height);
	}
	
	public DialogueNode getNode() {
		return node;
	}
	
	protected void connectorClicked(JDialogueCore core, Arc connectorNode, DialogueNodeConnector connector) {
		if (core.getSelectedConnector() == null) {
			//Select the connector
			core.setSelectedConnector(new ConnectorSelection(connectorNode, connector));
			System.err.println("Selected connector belonging to " + connector.getParent().getName());
		}else {
			ConnectorSelection selected = core.getSelectedConnector();
			Arc selectedNode = selected.getConnectorNode();
			DialogueNodeConnector selectedConnector = selected.getConnector();
			
			//Connect the connectors
			selectedConnector.connect(connector);
		
			//Add UI representation of connection
			BoundLine line = new BoundLine(core.getPannablePane(), selectedNode, selectedConnector, connectorNode, connector);
			connectorLines.add(line);
			core.getPannablePane().getChildren().add(line);
			
			//Reset selection
			core.setSelectedConnector(null);
			
			System.err.println("Connected " + connector.getParent().getName() + " and " + selected.getConnector().getParent().getName());
		}
	}
}

/**
 * Creates a Line that's attached to two Nodes.
 * 
 * I know this entire implementation is hacky af but this is literally the only solution I could get to work properly
 * 
 * Pulled from
 * https://stackoverflow.com/questions/43115807/how-to-draw-line-between-two-nodes-placed-in-different-panes-regions
 */
class BoundLine extends Line {

	private Pane commonAncestor;
	private Arc node1, node2;
	private DialogueNodeConnector connector1, connector2;
	
	public BoundLine(Pane commonAncestor, Arc node1, DialogueNodeConnector connector1, Arc node2, DialogueNodeConnector connector2) {
		
		this.commonAncestor = commonAncestor;
		this.node1 = node1;
		this.connector1 = connector1;
		
		this.node2 = node2;
		this.connector2 = connector2;
		
		update();
		
		setStrokeWidth(2);
		setStroke(Color.GRAY.deriveColor(0, 1, 1, 0.5));
		setStrokeLineCap(StrokeLineCap.BUTT);
		getStrokeDashArray().setAll(10.0, 5.0);

		setMouseTransparent(true);
	}
	
	/**
	 * @return false if this line is no longer valid
	 */
	boolean update() {
		if (!connector1.isConnected(connector2)) return false;
		
		Bounds n1InCommonAncestor = getRelativeBounds(node1, commonAncestor);
		Bounds n2InCommonAncestor = getRelativeBounds(node2, commonAncestor);
		
		Point2D n1Center = getCenter(n1InCommonAncestor);
		Point2D n2Center = getCenter(n2InCommonAncestor);
		
		setStartX(n1Center.getX());
		setStartY(n1Center.getY());
		
		setEndX(n2Center.getX());
		setEndY(n2Center.getY());
		
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
