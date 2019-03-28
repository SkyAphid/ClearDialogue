package nokori.clear_dialogue.project;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * This dialogue node is customized for handling player responses.
 * 
 */
public class DialogueResponse extends Dialogue {

	private static final long serialVersionUID = -6673674509676451177L;
	
	/*
	 * You can have as many responses as you want, along with corresponding
	 * connectors so that each response can connect to a different dialogue text
	 * node.
	 */
	private ArrayList<Response> responses = new ArrayList<Response>();
	
	public DialogueResponse(Project project, String uid, String name, String tag, float x, float y, boolean expanded) {
		super(project, uid, name, tag, x, y, expanded);
	}
	
	public DialogueResponse(Project project, String name, String tag, float x, float y) {
		super(project, name, tag, x, y);

		addResponse("Default Response");
	}
	
	@Override
	public String getRenderableContent() {
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < responses.size(); i++) {
			if (i > 0) {
				s.append("\n");
			}
			
			s.append(responses.get(i).getText());
		}
		
		return s.toString();
	}
	
	@Override
	public void parseAndSetContent(String content) {
		String[] s = content.split("\n");
		
		//Add the new content (overwriting text on existing responses in matching indices so that connections can be reused)
		for (int i = 0; i < s.length; i++) {
			if (responses.size() < i) {
				responses.get(i).setText(s[i]);
			} else {
				addResponse(s[i]);
			}
		}
		
		//Remove responses outside the range of the new content
		if (s.length < responses.size()) {
			for (int i = s.length; i < responses.size(); i++) {
				responses.get(i).getOutConnector().disconnectAll();
				responses.remove(i);
				i--;
			}
		}
	}
	
	@Override
	public ArrayList<DialogueConnector> getAllConnectors() {
		ArrayList<DialogueConnector> connectors = new ArrayList<DialogueConnector>();
		connectors.add(getInConnector());
		
		for (int i = 0; i < responses.size(); i++) {
			connectors.add(responses.get(i).getOutConnector());
		}
		
		return connectors;
	}
	
	@Override
	public void disconnectAllConnectors() {
		getInConnector().disconnectAll();
		
		for (int i = 0; i < responses.size(); i++) {
			responses.get(i).getOutConnector().disconnectAll();
		}
	}

	/**
	 * Shortcut function for adding a new response to this node.
	 */
	public void addResponse(String text) {
		responses.add(new Response(text, new DialogueConnector(getProject(), this)));
	}
	
	public void addResponse(String text, String outConnectorUID) {
		responses.add(new Response(text, new DialogueConnector(getProject(), this, outConnectorUID)));
	}

	public void addResponses(String[] text) {
		for (int i = 0; i < text.length; i++) {
			addResponse(text[i]);
		}
	}
	
	public ArrayList<Response> getResponses() {
		return responses;
	}

	public class Response implements Serializable {
		
		private static final long serialVersionUID = 7097284104250711558L;
		
		private String text;
		private DialogueConnector outConnector;

		public Response(String text, DialogueConnector outConnector) {
			this.text = text;
			this.outConnector = outConnector;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public DialogueConnector getOutConnector() {
			return outConnector;
		}

		public void setOutConnector(DialogueConnector outConnector) {
			this.outConnector = outConnector;
		}
	}

	@Override
	public Dialogue duplicate() {
		DialogueResponse node = new DialogueResponse(getProject(), getTitle(), getTag(), getX(), getY());
		node.responses.addAll(responses);
		return node;
	}
}