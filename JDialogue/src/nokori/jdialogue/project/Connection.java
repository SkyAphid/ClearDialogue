package nokori.jdialogue.project;

import java.io.Serializable;

/**
 * Stores a connection between two DialogueNodeConnectors.
 */
public class Connection implements Serializable {
	
	private static final long serialVersionUID = 8239753346623708868L;
	
	private DialogueConnector connector1, connector2;
	
	public Connection(DialogueConnector connector1, DialogueConnector connector2) {
		this.connector1 = connector1;
		this.connector2 = connector2;
	}

	public DialogueConnector getConnector1() {
		return connector1;
	}

	public DialogueConnector getConnector2() {
		return connector2;
	}
	
	/**
	 * Returns the other connector in this Connection. I.E. if you pass in connector1, it'll return connector2, and vice versa.
	 * 
	 * @param connector
	 * @return
	 */
	public DialogueConnector getOther(DialogueConnector connector) {
		if (connector == connector1) {
			return connector2;
		}
		
		if (connector == connector2) {
			return connector1;
		}
		
		return null;
	}
	
	/**
	 * Checks if this Connection contains the connector.
	 */
	public boolean contains(DialogueConnector connector) {
		return (connector1 == connector || connector2 == connector);
	}
	
	/**
	 * Checks if this Connection is a representation of a connection between the two connectors.
	 */
	public boolean represents(DialogueConnector connector1, DialogueConnector connector2) {
		return (this.connector1 == connector1 && this.connector2 == connector2 || this.connector1 == connector2 && this.connector2 == connector1);
	}
	
	/**
	 * Checks if this Connection is a match of the inputted one.
	 */
	public boolean matches(Connection connection) {
		return (connection.connector1 == connector1 && connection.connector2 == connector2 || connection.connector1 == connector2 && connection.connector2 == connector1);
	}
}
