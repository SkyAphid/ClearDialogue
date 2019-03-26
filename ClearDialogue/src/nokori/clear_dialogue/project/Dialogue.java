package nokori.clear_dialogue.project;

import java.io.Serializable;
import java.rmi.server.UID;
import java.util.ArrayList;

/**
 * 
 * This is the skeleton for all nodes, hosting only basic fundamental data.
 *
 */
public abstract class Dialogue implements Serializable {
	
	private static final long serialVersionUID = 9061233295902786274L;

	//Store the Project object for reference (I.E. seeing if a DialogueNode is a part of Project X when loaded into a game)
	private Project project;
	
	//Give every node a unique ID for potential saving/loading/usage purposes
	private String uid = new UID().toString();
	
	//Basic user-settable information
	private String name;
	private String tag;
	private float x, y;
	private boolean expanded = false;
	
	//Connector so that other nodes can input into this one
	private DialogueConnector inConnector;

	/**
	 * This constructor allows you to input the UID as well, for in cases where you're trying to load a copy of a dialogue node from a file.
	 * 
	 */
	public Dialogue(Project project, String uid, String name, String tag, float x, float y, boolean expanded) {
		this.project = project;
		this.uid = uid;
		this.name = name;
		this.tag = tag;
		this.x = x;
		this.y = y;
		this.expanded = expanded;
	}

	/**
	 * This constructor is used for making a brand new dialogue node. A UID is generated automatically.
	 */
	public Dialogue(Project project, String name, String tag, float x, float y) {
		this.project = project;
		this.name = name;
		this.tag = tag;
		this.x = x;
		this.y = y;
		
		uid = new UID().toString();
		
		inConnector = new DialogueConnector(project, this);
	}
	
	/**
	 * @return the text content of this Dialogue in a single string format that can be easily rendered.
	 */
	public abstract String getRenderableContent();
	
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
	
	public void setPosition(float x, float y) {
		setX(x);
		setY(y);
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public void setInConnector(DialogueConnector inConnector) {
		this.inConnector = inConnector;
	}

	public DialogueConnector getInConnector() {
		return inConnector;
	}
	
	/**
	 * Utility function for getting all DialogueNodeConnectors that this DialogueNode has.
	 */
	public abstract ArrayList<DialogueConnector> getAllConnectors();
	
	/**
	 * Disconnect all connectors (for when nodes are deleted).
	 * 
	 * If you extend this, remember to call getInConnector().disconnectAll()!!
	 */
	public abstract void disconnectAllConnectors();
	
	/**
	 * Duplicate this DialogueNode. Keep in mind that connections generally aren't copied, just the contents of the node itself.
	 * 
	 * @return the duplicated node.
	 */
	public abstract Dialogue duplicate();
}
