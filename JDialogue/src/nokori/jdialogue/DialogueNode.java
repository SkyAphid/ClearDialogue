package nokori.jdialogue;

import java.util.ArrayList;

/**
 * 
 * This is a node, containing dialogue data.
 * 
 * @author Brayden
 *
 */
public class DialogueNode {
	
	private double x, y;
	private String name;
	private String tag = "";
	private String text = "Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text Test Text ";
	
	private ArrayList<DialogueNode> connections = new ArrayList<DialogueNode>();
	
	public DialogueNode(String name, double x, double y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public ArrayList<DialogueNode> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<DialogueNode> connections) {
		this.connections = connections;
	}
}
