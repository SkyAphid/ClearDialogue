package nokori.jdialogue.project;

import java.io.Serializable;
import java.rmi.server.UID;
import java.util.ArrayList;

import nokori.jdialogue.throwable.NullConnectorError;

/**
 * A connector that can be attached to a node.
 * 
 * DialogueNodes can contain multiple connectors, but each connector can only attach to one Node.
 * 
 * This allows nodes like Dialogue Nodes to only have one connection, but allow response nodes to have as many as needed.
 *
 */
public class DialogueNodeConnector implements Serializable {
	
	private static final long serialVersionUID = -1047642658457698535L;
	
	private String uid = new UID().toString();
	
	private DialogueNode parent;
	private ArrayList<DialogueNodeConnector> connections = new ArrayList<DialogueNodeConnector>();
	
	/**
	 * @param parent the DialogueNode that this connector is stored in
	 */
	public DialogueNodeConnector(DialogueNode parent) {
		this.parent = parent;
	}
	
	public String getUID() {
		return uid;
	}

	public DialogueNode getParent() {
		return parent;
	}
	
	public void connect(DialogueNodeConnector dialogueNodeConnector) {
		if (dialogueNodeConnector != null) {
			//System.out.println(parent.getName() + " connected to " + dialogueNodeConnector.getParent().getName());
			
			if (!connections.contains(dialogueNodeConnector)) {
				connections.add(dialogueNodeConnector);
			}
			
			if (!dialogueNodeConnector.connections.contains(this)) {
				dialogueNodeConnector.connections.add(this);
			}
			
		}else {
			throw new NullConnectorError(parent.getName());
		}
	}
	
	public void disconnect(DialogueNodeConnector connector) {
		connector.connections.remove(this);
		connections.remove(connector);
	}
	
	public void disconnectAll() {
		for (int i = 0; i < connections.size(); i++) {
			disconnect(connections.get(i));
			i--;
		}
	}
	
	public boolean isConnected(DialogueNodeConnector dialogueNodeConnector) {
		return (connections.contains(dialogueNodeConnector));
	}
}
