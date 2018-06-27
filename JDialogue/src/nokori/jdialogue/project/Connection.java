package nokori.jdialogue.project;

import java.io.Serializable;

/**
 * Stores a connection between two DialogueNodeConnectors.
 */
public class Connection implements Serializable {
	
	private static final long serialVersionUID = 8239753346623708868L;
	
	private DialogueNodeConnector connector1, connector2;
	
	public Connection(DialogueNodeConnector connector1, DialogueNodeConnector connector2) {
		this.connector1 = connector1;
		this.connector2 = connector2;
	}

	public DialogueNodeConnector getConnector1() {
		return connector1;
	}

	public DialogueNodeConnector getConnector2() {
		return connector2;
	}
	
	public boolean contains(DialogueNodeConnector connector) {
		return (connector1 == connector || connector2 == connector);
	}
	
	public boolean represents(DialogueNodeConnector connector1, DialogueNodeConnector connector2) {
		return (this.connector1 == connector1 && this.connector2 == connector2 || this.connector1 == connector2 && this.connector2 == connector1);
	}
	
	public boolean matches(Connection connection) {
		return (connection.connector1 == connector1 && connection.connector2 == connector2 || connection.connector1 == connector2 && connection.connector2 == connector1);
	}
}
