package nokori.clear_dialogue.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * This class represents the Project, containing a list of all the nodes that the user has made, along with the project name.
 *
 */
public class Project implements Serializable {
	
	private static final long serialVersionUID = -8369866434879310100L;
	
	//Version information, this is to help bring old files up to date in the future if needed.
	public static final int CURRENT_VERSION = 1;
	private int version;
	
	//The name of the project
	private String name;

	//Remember viewport data for next use
	private float viewportX, viewportY, viewportScale;
	
	//All nodes contained by this project
	private ArrayList<Dialogue> dialogueList = new ArrayList<Dialogue>();
	
	//All connections between the various nodes are stored here
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	
	public Project(int version, String name, float viewportX, float viewportY, float viewportScale) {
		this.version = version;
		this.name = name;
		this.viewportX = viewportX;
		this.viewportY = viewportY;
		this.viewportScale = viewportScale;
	}
	
	public Project() {
		this(CURRENT_VERSION, "Default Project", 0.0f, 0.0f, 1.0f);
	}
	
	public int getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setViewportPosition(float viewportX, float viewportY) {
		setViewportX(viewportX);
		setViewportY(viewportY);
	}

	public float getViewportX() {
		return viewportX;
	}

	public void setViewportX(float viewportX) {
		this.viewportX = viewportX;
	}

	public float getViewportY() {
		return viewportY;
	}

	public void setViewportY(float viewportY) {
		this.viewportY = viewportY;
	}
	
	public float getViewportScale() {
		return viewportScale;
	}

	public void setViewportScale(float viewportScale) {
		this.viewportScale = viewportScale;
	}
	
	/*
	 * We restrict access to the ArrayList because we don't want the viewport to
	 * ever get out of sync with the actual data By handling data this way, it'll be
	 * easy to find out how if that does happen since you can check the function
	 * references
	 */
	
	public void addDialogue(Dialogue dialogue) {
		dialogueList.add(dialogue);
	}
	
	public void removeDialogue(Dialogue dialogue) {
		dialogue.disconnectAllConnectors();
		dialogueList.remove(dialogue);
	}
	
	public Dialogue getDialogue(int index) {
		return dialogueList.get(index);
	}
	
	public int getNumDialogue() {
		return dialogueList.size();
	}
	
	public List<Dialogue> getUnmodifiableDialogueList() {
		return Collections.unmodifiableList(dialogueList);
	}
	
	/**
	 * Adds all nodes from the inputted project into this one.
	 */
	public void mergeProject(Project project) {
		boolean init = false;
		float minX = 0f, minY = 0f;
		
		for (Dialogue d : dialogueList) {
			if (!init) {
				minX = d.getX();
				minY = d.getY();
				init = true;
			} else {
				if (d.getX() < minX) {
					minX = d.getX();
				}
				
				if (d.getY() < minY) {
					minY = d.getY();
				}
			}
		}
		
		for (Dialogue d : project.dialogueList) {
			d.setX(minX);
			d.setY(minY);
		}
		
		dialogueList.addAll(project.dialogueList);
		connections.addAll(project.connections);
	}

	/**
	 * Find Dialogues in the Project with the given tag.
	 * 
	 * @param tag - the tag to search for
	 * @param exactMatch - if true, it will only return a node that equals() the input. Otherwise, contains() will be used.
	 * @return the Dialogue that meets the criteria, returns null if a match is not found
	 */
	public Stack<Dialogue> findNodeWithTag(String tag, boolean exactMatch) {
		return findDialogue(new SearchRule() {

			@Override
			public boolean check(Dialogue node) {
				String nodeTag = node.getTags();
				
				boolean hasTag = (exactMatch ? nodeTag.equals(tag) : nodeTag.contains(tag));
				
				if (hasTag) {
					return true;
				} else {
					return false;
				}
			}
		});
	}
	
	/**
	 * Find a Dialogue in the Project with the given name.
	 * 
	 * @param name - the name to search for
	 * @param exactMatch - if true, it will only return a node that equals() the input. Otherwise, contains() will be used.
	 * @return the Dialogue that meets the criteria, returns null if a match is not found
	 */
	public Stack<Dialogue> findNodeWithTitle(String name, boolean exactMatch) {
		return findDialogue(new SearchRule() {

			@Override
			public boolean check(Dialogue node) {
				String nodeName = node.getTitle();
				
				boolean hasName = (exactMatch ? nodeName.equals(name) : nodeName.contains(name));
				
				if (hasName) {
					return true;
				} else {
					return false;
				}
			}
		});
	}
	
	/**
	 * This a general function for finding Nodes with specific conditions, used by findNodeWithTag() and findNodeWithName(). 
	 * To make your own, simply pass in a custom SearchRule. 
	 * 
	 * @param rule
	 * @return
	 */
	public Stack<Dialogue> findDialogue(SearchRule rule) {
		
		Stack<Dialogue> found = new Stack<Dialogue>();
		
		for (int i = 0; i < dialogueList.size(); i++) {
			Dialogue node = dialogueList.get(i);
			
			if (rule.check(node)) {
				found.push(node);
			}
		}
		
		return found;
	}
	
	public interface SearchRule {
		/**
		 * This Dialogue is passed in by findNode as it iterates through all of the nodes in the Project.
		 * 
		 * Return true to add the Dialogue to the list of Dialogue that pass your particular test. 
		 * Return false to ignore it and continue.
		 * 
		 * @param dialogue
		 * @return
		 */
		public boolean check(Dialogue dialogue);
	}
	
	/*
	 * 
	 * Connections are managed by the Project so that we have a singular location for
	 * all of the various node relationships. The alternative (which I tried first) was 
	 * having the nodes themselves manage the connections, but that ended up being very
	 * messy due to the fact that each node can have numerous connections.
	 * 
	 */
	
	public void addConnection(Connection connection) {
		connections.add(connection);
	}
	
	public Connection getConnection(int index) {
		return connections.get(index);
	}

	public int getNumConnections() {
		return connections.size();
	}
	
	/**
	 * Disconnects all connections between the two connectors.
	 */
	public void disconnect(DialogueConnector connector1, DialogueConnector connector2) {
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			
			if (c.represents(connector1, connector2)) {
				connections.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Delete all Connections to this connector.
	 */
	public void disconnectAll(DialogueConnector connector) {
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			
			if (c.contains(connector)) {
				connections.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Returns an ArrayList of all the Connections containing the given connector.
	 * 
	 * @param connector
	 */
	public ArrayList<Connection> getAllConnections(DialogueConnector connector){
		ArrayList<Connection> found = new ArrayList<>();
		
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			
			if (c.contains(connector)) {
				found.add(c);
			}
		}
		
		return found;
	}
	
	/**
	 * Adds all of the connections to this Project's registry.
	 * 
	 * @param connections
	 */
	public void connectAll(ArrayList<Connection> connections) {
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			
			if (!connectionExists(c.getConnector1(), c.getConnector2())) {
				connections.add(c);
			}
		}
	}
	
	/**
	 * Checks if the two connectors have a Connection.
	 */
	public boolean isConnected(DialogueConnector connector1, DialogueConnector connector2) {
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			
			if (c.represents(connector1, connector2)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if a similar Connection already exists.
	 */
	public boolean connectionExists(DialogueConnector connector1, DialogueConnector connector2) {
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			
			if (c.represents(connector1, connector2)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Utility function for fetching a DialogueConnector with its UID.
	 */
	public DialogueConnector getDialogueConnector(String uid) {
		for (int i = 0; i < dialogueList.size(); i++) {
			ArrayList<DialogueConnector> connectors = dialogueList.get(i).getAllConnectors();
			
			for (int j = 0; j < connectors.size(); j++) {
				DialogueConnector connector = connectors.get(j);
				
				if (connector.getUID().equals(uid)) {
					return connector;
				}
			}
		}
		
		return null;
	}
}
