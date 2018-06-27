package nokori.jdialogue.project;

/**
 * 
 * This dialogue node is customized for handling basic text.
 * 
 */
public class DialogueTextNode extends DialogueNode {

	private static final long serialVersionUID = -3049363958493031040L;

	private String text = "Default Text";
	
	//Out connector for connecting to other nodes
	private DialogueNodeConnector outConnector;
	
	public DialogueTextNode(Project project, String name, double x, double y) {
		super(project, name, x, y);
		outConnector = new DialogueNodeConnector(project, this);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public DialogueNodeConnector getOutConnector() {
		return outConnector;
	}
	
	@Override
	public void disconnectAllConnectors() {
		super.disconnectAllConnectors();
		outConnector.disconnectAll();
	}
}
