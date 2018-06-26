package nokori.jdialogue.ui.node;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueNodeConnector;
import nokori.jdialogue.ui.Button;
import nokori.jdialogue.ui.pannable_pane.PannablePane;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public abstract class DialogueNodePane extends StackPane {
	
	//Start/Dispose animation
	private static final int FADE_TIME = 200;
	private static final double START_ROT = 25.0;
	
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
	private Text title;

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
			connectorClicked(event, core, connector, node.getInConnector());
		});
		
		connector.setOnMouseEntered(event -> {
			connectorHighlightTransition(core.getScene(), connector, inConnectorColor, true);
		});
		
		connector.setOnMouseExited(event -> {
			connectorHighlightTransition(core.getScene(), connector, inConnectorColor, false);
		});
		
		StackPane.setAlignment(connector, Pos.CENTER_LEFT);
		StackPane.setMargin(connector, new Insets(0, 0, 0, -CONNECTOR_RADIUS));
		
		//Title text
		title = new Text(node.getName());
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

		setOnMouseClicked(event -> {
			checkDispose(event, core);
		});
		
		//Initial animation
		RotateTransition rotateTransition = new RotateTransition(Duration.millis(FADE_TIME), this);
		rotateTransition.setFromAngle(START_ROT);
		rotateTransition.setToAngle(0.0);
		rotateTransition.play();
		
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME), this);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}
	
	/**
	 * Refresh nodes to have the latest Node data
	 */
	public void refresh(JDialogueCore core) {
		title.setText(node.getName());
	}
	
	protected boolean checkDispose(MouseEvent event, JDialogueCore core) {
		if (event.getClickCount() > 1 && event.getButton() == MouseButton.SECONDARY) {
			dispose(core);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Begin the process of deleting this node
	 */
	protected void dispose(JDialogueCore core) {
		setMouseTransparent(true);
		
		RotateTransition rotateTransition = new RotateTransition(Duration.millis(FADE_TIME/2), this);
		rotateTransition.setFromAngle(0.0);
		rotateTransition.setToAngle(START_ROT);
		rotateTransition.play();
		
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME/2), this);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.play();
		
		fadeTransition.setOnFinished(event -> {
			core.removeDialogueNode(this);
		});
	}
	
	public void setBackgroundHeight(int height) {
		outline.setHeight(height);
		background.setHeight(height);
	}
	
	public DialogueNode getNode() {
		return node;
	}

	protected void connectorClicked(MouseEvent event, JDialogueCore core, Arc connectorNode, DialogueNodeConnector connector) {
		PannablePane pannablePane = core.getPannablePane();
		
		if (core.getSelectedConnector() == null) {
			connector.disconnectAll();
			
			//Select the connector
			core.setSelectedConnector(new ConnectorSelection(this, connectorNode, connector));
			//System.out.println("Selected connector belonging to " + connector.getParent().getName());
			
			//Update old connectors to delete any that may have just been disconnected
			core.updateConnectors(event);
			
			event.consume();
		}else {
			ConnectorSelection selected = core.getSelectedConnector();
			
			Arc selectedNode = selected.getConnectorNode();
			DialogueNodeConnector selectedConnector = selected.getConnector();
			
			if (selectedConnector != connector) {
				
				//Connect the connectors
				selectedConnector.connect(connector);
			
				//Add UI representation of connection
				BoundLine line = new BoundLine(selectedNode, selectedConnector, connectorNode, connector);
				core.addConnectorLine(line);

				line.update(event, pannablePane);
			}
			
			//Reset selection
			core.setSelectedConnector(null);
			
			//System.out.println("Connected " + connector.getParent().getName() + " and " + selected.getConnector().getParent().getName());
		}
	}
	
	/**
	 * Convenience function for highlighting nodes when selected
	 */
	public static void connectorHighlightTransition(Scene scene, Arc node, Color originalColor, boolean highlighted) {
		FillTransition fillTransition = new FillTransition(Duration.millis(Button.FADE_TIME), node);
		
		if (highlighted) {
			fillTransition.setFromValue(originalColor);
			fillTransition.setToValue(originalColor.brighter().brighter());
			scene.setCursor(Cursor.HAND);
		}else {
			fillTransition.setFromValue(originalColor.brighter().brighter());
			fillTransition.setToValue(originalColor);
			scene.setCursor(Cursor.DEFAULT);
		}
		
		fillTransition.play();
	}
}
