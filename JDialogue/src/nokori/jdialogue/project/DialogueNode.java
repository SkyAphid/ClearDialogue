package nokori.jdialogue.project;

import java.io.Serializable;
import java.rmi.server.UID;
import java.util.ArrayList;

/**
 * 
 * This is the skeleton for all nodes, hosting only basic fundamental data.
 *
 */
public abstract class DialogueNode implements Serializable {
	
	private static final long serialVersionUID = 9061233295902786274L;

	//Store the Project object for reference (I.E. seeing if a DialogueNode is a part of Project X when loaded into a game)
	private Project project;
	
	//Give every node a unique ID for potential saving/loading/usage purposes
	private String uid = new UID().toString();
	
	//Basic user-settable information
	private String name;
	private String tag;
	private double x, y;
	
	//Connector so that other nodes can input into this one
	private DialogueNodeConnector inConnector;

	public DialogueNode(Project project, String uid, String name, String tag, double x, double y) {
		this.project = project;
		this.uid = uid;
		this.name = name;
		this.tag = tag;
		this.x = x;
		this.y = y;
	}
	
	public DialogueNode(Project project, String name, double x, double y) {
		this.project = project;
		this.name = name;
		this.x = x;
		this.y = y;
		
		uid = new UID().toString();
		tag = "";
		
		inConnector = new DialogueNodeConnector(project, this);
	}
	
	public Project getProject() {
		return project;
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

	public void setInConnector(DialogueNodeConnector inConnector) {
		this.inConnector = inConnector;
	}

	public DialogueNodeConnector getInConnector() {
		return inConnector;
	}
	
	/**
	 * Utility function for getting all DialogueNodeConnectors that this DialogueNode has.
	 */
	public abstract ArrayList<DialogueNodeConnector> getAllConnectors();
	
	/**
	 * Disconnect all connectors (for when nodes are deleted).
	 * 
	 * If you extend this, remember to call getInConnector().disconnectAll()!!
	 */
	public abstract void disconnectAllConnectors();
}
