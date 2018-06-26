package nokori.jdialogue.project;

import java.io.Serializable;
import java.rmi.server.UID;

/**
 * 
 * This is the skeleton for all nodes, hosting only basic fundamental data.
 *
 */
public abstract class DialogueNode implements Serializable {
	
	private static final long serialVersionUID = 9061233295902786274L;

	//Give every node a unique ID for potential saving/loading/usage purposes
	private String uid = new UID().toString();
	
	//Basic user-settable information
	private String name;
	private String tag = "No Tag";
	private double x, y;
	
	//Connector so that other nodes can input into this one
	private DialogueNodeConnector inConnector;

	public DialogueNode(String name, double x, double y) {
		this.name = name;
		this.x = x;
		this.y = y;

		inConnector = new DialogueNodeConnector(this);
	}

	public String getUID() {
		return uid;
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

	public DialogueNodeConnector getInConnector() {
		return inConnector;
	}

	public void setInConnector(DialogueNodeConnector inConnector) {
		this.inConnector = inConnector;
	}
}
