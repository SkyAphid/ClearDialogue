package nokori.jdialogue.project;

import java.util.ArrayList;

/**
 * 
 * This dialogue node is customized for handling player responses.
 * 
 */
public class DialogueResponseNode extends DialogueNode {

	/*
	 * You can have as many responses as you want, along with corresponding
	 * connectors so that each response can connect to a different dialogue text
	 * node.
	 */
	private ArrayList<Response> responses = new ArrayList<Response>();

	public DialogueResponseNode(String name, double x, double y) {
		super(name, x, y);

		for (int i = 0; i < 3; i++) {
			addResponse(i + " Response Test");
		}
	}

	/**
	 * Shortcut function for adding a new response to this node
	 */
	public void addResponse(String text) {
		responses.add(new Response(text, new DialogueNodeConnector(this)));
	}

	public ArrayList<Response> getResponses() {
		return responses;
	}

	public class Response {
		private String text;
		private DialogueNodeConnector outConnector;

		public Response(String text, DialogueNodeConnector outConnector) {
			this.text = text;
			this.outConnector = outConnector;
		}

		public String getText() {
			return text;
		}

		public DialogueNodeConnector getOutConnector() {
			return outConnector;
		}
	};
}