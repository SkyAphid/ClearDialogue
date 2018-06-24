package nokori.jdialogue.ui;

import javafx.scene.shape.Arc;
import nokori.jdialogue.project.DialogueNodeConnector;

/**
 * A basic wrapper class for containing all the necessary parts to make dialogue node connections
 *
 */
public class ConnectorSelection {
	private Arc connectorNode;
	private DialogueNodeConnector connector;
	
	public ConnectorSelection(Arc connectorNode, DialogueNodeConnector connector) {
		this.connectorNode = connectorNode;
		this.connector = connector;
	}

	public Arc getConnectorNode() {
		return connectorNode;
	}

	public DialogueNodeConnector getConnector() {
		return connector;
	}
}