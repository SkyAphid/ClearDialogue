package nokori.clear_dialogue.project;

import java.util.ArrayList;

/**
 * 
 * This dialogue node is customized for handling basic text.
 * 
 */
public class DialogueText extends Dialogue {

	private static final long serialVersionUID = -3049363958493031040L;

	private String text;
	
	//Out connector for connecting to other nodes
	private DialogueConnector outConnector;
	
	public DialogueText(Project project, String uid, String name, String tags, float x, float y, boolean expanded, String text) {
		super(project, uid, name, tags, x, y, expanded);
		this.text = text;
	}
	
	public DialogueText(Project project, String name, String tags, float x, float y) {
		super(project, name, tags, x, y);
		text = "Default Text";
		outConnector = new DialogueConnector(project, this);
	}
	
	@Override
	public String getRenderableContent() {
		return text;
	}

	@Override
	public void parseAndSetContent(String content) {
		text = content;
	}
	
	@Override
	public void parseAndAddContent(String content) {
		text += content;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setOutConnector(DialogueConnector outConnector) {
		this.outConnector = outConnector;
	}

	public DialogueConnector getOutConnector() {
		return outConnector;
	}
	
	@Override
	public ArrayList<DialogueConnector> getAllConnectors() {
		ArrayList<DialogueConnector> connectors = new ArrayList<DialogueConnector>();
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
	public Dialogue duplicate() {
		DialogueText node = new DialogueText(getProject(), getTitle(), getTags(), getX(), getY());
		node.text = text;
		return node;
	}
}
