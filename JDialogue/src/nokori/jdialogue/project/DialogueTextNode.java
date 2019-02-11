package nokori.jdialogue.project;

import java.util.ArrayList;

/**
 * 
 * This dialogue node is customized for handling basic text.
 * 
 */
public class DialogueTextNode extends DialogueNode {

	private static final long serialVersionUID = -3049363958493031040L;

	private String text;
	
	//Out connector for connecting to other nodes
	private DialogueNodeConnector outConnector;
	
	public DialogueTextNode(Project project, String uid, String name, String tag, double x, double y, String text) {
		super(project, uid, name, tag, x, y);
		this.text = text;
	}
	
	public DialogueTextNode(Project project, String name, String tag, double x, double y) {
		super(project, name, tag, x, y);
		text = "Default Text";
		outConnector = new DialogueNodeConnector(project, this);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setOutConnector(DialogueNodeConnector outConnector) {
		this.outConnector = outConnector;
	}

	public DialogueNodeConnector getOutConnector() {
		return outConnector;
	}
	
	@Override
	public ArrayList<DialogueNodeConnector> getAllConnectors() {
		ArrayList<DialogueNodeConnector> connectors = new ArrayList<DialogueNodeConnector>();
		connectors.add(getInConnector());
		connectors.add(outConnector);
		
		return connectors;
	}
	
	@Override
	public void disconnectAllConnectors() {
		getInConnector().disconnectAll();
		outConnector.disconnectAll();
	}

	@Override
	public DialogueNode duplicate() {
		DialogueTextNode node = new DialogueTextNode(getProject(), getName(), getTag(), getX(), getY());
		node.text = text;
		return node;
	}
}
