package nokori.jdialogue.project;

import java.io.Serializable;
import java.rmi.server.UID;

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
	
	private Project project;
	private DialogueNode parent;
	
	/**
	 * @param parent the DialogueNode that this connector is stored in
	 */
	public DialogueNodeConnector(Project project, DialogueNode parent) {
		this.project = project;
		this.parent = parent;
	}
	
	public String getUID() {
		return uid;
	}

	public DialogueNode getParent() {
		return parent;
	}
	
	/*
	 * 
	 * Various shortcut functions for managing connections so that the user
	 * doesn't have to use the Project itself
	 * 
	 */
	
	public void connect(DialogueNodeConnector connector) {
		if (connector != null) {
			if (!project.connectionExists(this, connector)) {
				project.addConnection(new Connection(this, connector));
			}
		}else {
			throw new NullConnectorError(parent.getName());
		}
	}
	
	public void disconnect(DialogueNodeConnector connector) {
		project.disconnect(this, connector);
	}
	
	public void disconnectAll() {
		project.disconnectAll(this);
	}
	
	public boolean isConnected(DialogueNodeConnector connector) {
		return project.isConnected(this, connector);
	}
}
