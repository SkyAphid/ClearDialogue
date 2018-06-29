package nokori.jdialogue.ui.node;

import nokori.jdialogue.project.DialogueNodeConnector;

/**
 * A basic wrapper class for containing all the necessary parts to make dialogue node connections
 *
 */
public class ConnectorSelection {
	
	private DialogueNodePane parent;
	private DialogueNodeConnectorArc connectorNode;
	private DialogueNodeConnector connector;
	private BoundLine followingConnectorLine;
	
	public ConnectorSelection(DialogueNodePane parent, DialogueNodeConnectorArc connectorNode, DialogueNodeConnector connector) {
		this.parent = parent;
		this.connectorNode = connectorNode;
		this.connector = connector;
		
		followingConnectorLine = new BoundLine(connectorNode, connector);
	}

	public DialogueNodePane getParent() {
		return parent;
	}

	public DialogueNodeConnectorArc getConnectorNode() {
		return connectorNode;
	}

	public DialogueNodeConnector getConnector() {
		return connector;
	}

	/**
	 * When you select a connector, a line will follow the mouse to indicate its selection
	 */
	public BoundLine getFollowingConnectorLine() {
		return followingConnectorLine;
	}
}