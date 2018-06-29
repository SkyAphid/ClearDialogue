package nokori.jdialogue.ui.node;

import javafx.animation.FillTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNodeConnector;
import nokori.jdialogue.ui.Button;
import nokori.jdialogue.ui.pannable_pane.PannablePane;

/**
 * A graphical and interactable representation of a DialogueNodeConnector
 *
 */
public class DialogueNodeConnectorArc extends Arc{
	//Connector information that all nodes should have in-common
	private static final int CONNECTOR_RADIUS = 20;
	
	private static final Color outConnectorColor = Color.rgb(52, 205, 112);
	private static final Color inConnectorColor = Color.rgb(228, 80, 65);
	
	public enum ConnectorType {
		IN(inConnectorColor, 90, 180, Pos.CENTER_LEFT, new Insets(0, 0, 0, -CONNECTOR_RADIUS)), 
		OUT(outConnectorColor, 90, -180, Pos.CENTER_RIGHT, new Insets(0, -CONNECTOR_RADIUS, 0, 0));
		
		private Color color;
		
		private int startAngle;
		private int length;
		
		private Pos pos;
		private Insets insets;
		
		private ConnectorType(Color color, int startAngle, int length, Pos pos, Insets insets) {
			this.color = color;
			this.startAngle = startAngle;
			this.length = length;
			this.pos = pos;
			this.insets = insets;
		}
	};
	
	private ConnectorType connectorType;
	
	private DialogueNodeConnector connector;
	
	public DialogueNodeConnectorArc(JDialogueCore core, DialogueNodePane pane, ConnectorType type, DialogueNodeConnector connector) {
		this(core, pane, type, connector, CONNECTOR_RADIUS);
	}
	
	public DialogueNodeConnectorArc(JDialogueCore core, DialogueNodePane pane, ConnectorType connectorType, DialogueNodeConnector connector, int connectorRadius) {
		this.connectorType = connectorType;
		this.connector = connector;
		
		setRadiusX(connectorRadius);
		setRadiusY(connectorRadius);
		
		setFill(connectorType.color);
		setStartAngle(connectorType.startAngle);
		setLength(connectorType.length);
	
		setOnMouseEntered(event -> {
			connectorHighlightTransition(core.getScene(), this, connectorType.color, true);
		});
		
		setOnMouseExited(event -> {
			connectorHighlightTransition(core.getScene(), this, connectorType.color, false);
		});
		
		setOnMouseClicked(event -> {
			connectorClicked(event, core, pane);
		});
		
		StackPane.setAlignment(this, connectorType.pos);
		StackPane.setMargin(this, connectorType.insets);
	}
	
	public ConnectorType getConnectorType() {
		return connectorType;
	}

	private void connectorClicked(MouseEvent event, JDialogueCore core, DialogueNodePane pane) {
		PannablePane pannablePane = core.getPannablePane();
		
		if (core.getSelectedConnector() == null) {
			connector.disconnectAll();
			
			//Select the connector
			core.setSelectedConnector(new ConnectorSelection(pane, this, connector));
			//System.out.println("Selected connector belonging to " + connector.getParent().getName());
			
			//Update old connectors to delete any that may have just been disconnected
			core.updateConnectors(event);
			
			event.consume();
		}else {
			ConnectorSelection selected = core.getSelectedConnector();
			
			DialogueNodeConnectorArc selectedNode = selected.getConnectorNode();
			DialogueNodeConnector selectedConnector = selected.getConnector();
			
			if (selectedConnector != connector) {
				
				//Connect the connectors
				selectedConnector.connect(connector);
			
				//Add UI representation of connection
				BoundLine line = new BoundLine(selectedNode, selectedConnector, this, connector);
				core.addConnectorLine(line);

				line.update(event, pannablePane);
			}
			
			//Reset selection
			core.setSelectedConnector(null);
			
			//System.out.println("Connected " + connector.getParent().getName() + " and " + selected.getConnector().getParent().getName());
		}
	}

	public DialogueNodeConnector getDialogueNodeConnector() {
		return connector;
	}
	
	/**
	 * Convenience function for highlighting nodes when selected
	 */
	private static void connectorHighlightTransition(Scene scene, Arc node, Color originalColor, boolean highlighted) {
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
