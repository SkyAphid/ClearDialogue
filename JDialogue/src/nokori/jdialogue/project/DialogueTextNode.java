package nokori.jdialogue.project;

/**
 * 
 * This dialogue node is customized for handling basic text.
 * 
 */
public class DialogueTextNode extends DialogueNode {

	private String text = "Default Text";
	
	public DialogueTextNode(String name, double x, double y) {
		super(name, x, y);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
