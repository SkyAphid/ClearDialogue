package nokori.jdialogue.project;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * This dialogue node is customized for handling player responses.
 * 
 */
public class DialogueResponseNode extends DialogueNode {

	private static final long serialVersionUID = -6673674509676451177L;
	
	/*
	 * You can have as many responses as you want, along with corresponding
	 * connectors so that each response can connect to a different dialogue text
	 * node.
	 */
	private ArrayList<Response> responses = new ArrayList<Response>();
	
	public DialogueResponseNode(Project project, String uid, String name, String tag, double x, double y) {
		super(project, uid, name, tag, x, y);
	}
	
	public DialogueResponseNode(Project project, String name, double x, double y) {
		super(project, name, x, y);

		addResponse("Default Response");
	}
	
	@Override
	public ArrayList<DialogueNodeConnector> getAllConnectors() {
		ArrayList<DialogueNodeConnector> connectors = new ArrayList<DialogueNodeConnector>();
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
		responses.add(new Response(text, new DialogueNodeConnector(getProject(), this)));
	}
	
	public void addResponse(String text, String outConnectorUID) {
		responses.add(new Response(text, new DialogueNodeConnector(getProject(), this, outConnectorUID)));
	}

	public ArrayList<Response> getResponses() {
		return responses;
	}

	public class Response implements Serializable {
		
		private static final long serialVersionUID = 7097284104250711558L;
		
		private String text;
		private DialogueNodeConnector outConnector;

		public Response(String text, DialogueNodeConnector outConnector) {
			this.text = text;
			this.outConnector = outConnector;
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

		public void setOutConnector(DialogueNodeConnector outConnector) {
			this.outConnector = outConnector;
		}
	}
}