package nokori.jdialogue.project;

import java.util.ArrayList;

/**
 * 
 * This dialogue node is customized for handling player responses.
 * 
 */
public class DialogueResponseNode extends DialogueNode {

	private ArrayList<String> responses = new ArrayList<String>();
	
	public DialogueResponseNode(String name, double x, double y) {
		super(name, x, y);
		
		for (int i = 0; i < 10; i++) {
			responses.add(i + " Response Test Response Test Response Test Response Test Response Test Response Test Response Test Response Test ");
		}
	}

	public ArrayList<String> getResponses() {
		return responses;
	}
}
