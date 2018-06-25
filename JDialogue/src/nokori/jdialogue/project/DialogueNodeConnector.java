package nokori.jdialogue.project;

import nokori.jdialogue.throwable.NullConnectorError;

/**
 * A connector that can be attached to a node.
 * 
 * DialogueNodes can contain multiple connectors, but each connector can only attach to one Node.
 * 
 * This allows nodes like Dialogue Nodes to only have one connection, but allow response nodes to have as many as needed.
 *
 */
public class DialogueNodeConnector {
	
	private DialogueNode parent;
	private DialogueNodeConnector connectedTo = null;
	
	/**
	 * @param parent the DialogueNode that this connector is stored in
	 */
	public DialogueNodeConnector(DialogueNode parent) {
		this.parent = parent;
	}
	
	public DialogueNode getParent() {
		return parent;
	}

	public void connect(DialogueNodeConnector dialogueNodeConnector) {
		if (dialogueNodeConnector != null) {
			//System.out.println(parent.getName() + " connected to " + dialogueNodeConnector.getParent().getName());
			
			connectedTo = dialogueNodeConnector;
			dialogueNodeConnector.connectedTo = this;
		}else {
			throw new NullConnectorError(parent.getName());
		}
	}
	
	public void disconnect() {
		if (connectedTo != null) {
			//System.out.println(parent.getName() + " disconnected from " + connectedTo.getParent().getName());
			
			connectedTo.connectedTo = null;
			connectedTo = null;
		}
	}
	
	public DialogueNodeConnector getConnectedTo() {
		return connectedTo;
	}
	
	public boolean isConnected(DialogueNodeConnector dialogueNodeConnector) {
		return (connectedTo == dialogueNodeConnector);
	}
}
