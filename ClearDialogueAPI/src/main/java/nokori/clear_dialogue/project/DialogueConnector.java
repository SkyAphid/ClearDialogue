package nokori.clear_dialogue.project;

import java.io.Serializable;
import java.rmi.server.UID;

import nokori.clear_dialogue.throwable.NullConnectorError;

/**
 * A connector that can be attached to a node.
 * 
 * DialogueNodes can contain multiple connectors, but each connector can only attach to one Node.
 * 
 * This allows nodes like Dialogue Nodes to only have one connection, but allow response nodes to have as many as needed.
 *
 */
public class DialogueConnector implements Serializable {
	
	private static final long serialVersionUID = -1047642658457698535L;
	
	private String uid;
	
	private Project project;
	private Dialogue parent;
	
	/**
	 * @param parent the DialogueNode that this connector is stored in
	 */
	public DialogueConnector(Project project, Dialogue parent, String uid) {
		this.project = project;
		this.parent = parent;
		this.uid = uid;
	}
	
	/**
	 * @param parent the DialogueNode that this connector is stored in
	 */
	public DialogueConnector(Project project, Dialogue parent) {
		this(project, parent, new UID().toString());
	}
	
	public String getUID() {
		return uid;
	}

	public Dialogue getParent() {
		return parent;
	}
	
	/*
	 * 
	 * Various shortcut functions for managing connections so that the user
	 * doesn't have to use the Project itself
	 * 
	 */
	
	public void connect(DialogueConnector connector) {
		if (connector != null) {
			if (!project.connectionExists(this, connector)) {
				project.addConnection(new Connection(this, connector));
			}
		}else {
			throw new NullConnectorError(parent.getTitle());
		}
	}
	
	public void disconnect(DialogueConnector connector) {
		project.disconnect(this, connector);
	}
	
	public void disconnectAll() {
		project.disconnectAll(this);
	}
	
	public boolean isConnected(DialogueConnector connector) {
		return project.isConnected(this, connector);
	}
	
	/**
	 * Get the DialogueNodeConnector that this one is connected to. 
	 * 
	 * Note: it finds the first instance of a connection with this connector and returns that. 
	 * If you're scanning an In-Connector (can have multiple connections), then use the Project and 
	 * iterate through the Connections manually.
	 * 
	 * @return
	 */
	public DialogueConnector getConnectedTo() {
		for (int i = 0; i < project.getNumConnections(); i++) {
			Connection c = project.getConnection(i);
			
			if (c.contains(this)) {
				return c.getOther(this);
			}
		}
		
		return null;
	}
	
	/**
	 * Shortcut version of getConnectedTo(), where the equivalent is getConnectedTo().getParent(). 
	 * 
	 * Returns null if not connected to anything.
	 */
	public Dialogue getNodeConnectedTo() {
		DialogueConnector connector = getConnectedTo();
		return (connector != null ? connector.getParent() : null);
	} 
}
