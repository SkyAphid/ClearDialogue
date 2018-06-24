package nokori.jdialogue.project;

/**
 * A connector that can be attached to a node.
 * 
 * DialogueNodes can contain multiple connectors, but each connector can only attach to one Node.
 * 
 * This allows nodes like Dialogue Nodes to only have one connection, but allow response nodes to have as many as needed.
 *
 */
public class DialogueNodeConnector {
	private DialogueNode connectedTo = null;
	
	public void setConnectedTo(DialogueNode dialogueNode) {
		connectedTo = dialogueNode;
	}
	
	public DialogueNode getConnectedTo() {
		return connectedTo;
	}
}
