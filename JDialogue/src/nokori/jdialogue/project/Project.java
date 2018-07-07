package nokori.jdialogue.project;

import java.io.Serializable;
import java.util.ArrayList;
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
	private double viewportX, viewportY, viewportScale;
	
	//All nodes contained by this project
	private ArrayList<DialogueNode> nodes = new ArrayList<DialogueNode>();
	
	//All connections between the various nodes are stored here
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	
	public Project(int version, String name, double viewportX, double viewportY, double viewportScale) {
		this.version = version;
		this.name = name;
		this.viewportX = viewportX;
		this.viewportY = viewportY;
		this.viewportScale = viewportScale;
	}
	
	public Project(double viewportX, double viewportY, double viewportScale) {
		this(CURRENT_VERSION, "Default Project", viewportX, viewportY, viewportScale);
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
	
	public double getViewportX() {
		return viewportX;
	}

	public void setViewportX(double viewportX) {
		this.viewportX = viewportX;
	}

	public double getViewportY() {
		return viewportY;
	}

	public void setViewportY(double viewportY) {
		this.viewportY = viewportY;
	}

	public double getViewportScale() {
		return viewportScale;
	}

	public void setViewportScale(double viewportScale) {
		this.viewportScale = viewportScale;
	}
	
	/*
	 * We restrict access to the ArrayList because we don't want the viewport to
	 * ever get out of sync with the actual data By handling data this way, it'll be
	 * easy to find out how if that does happen since you can check the function
	 * references
	 */
	
	public void addNode(DialogueNode node) {
		nodes.add(node);
	}
	
	public void removeNode(DialogueNode node) {
		
		nodes.remove(node);
	}
	
	public DialogueNode getNode(int index) {
		return nodes.get(index);
	}
	
	public int getNumNodes() {
		return nodes.size();
	}
	

	/**
	 * Find DialogueNodes in the Project with the given tag.
	 * 
	 * @param tag - the tag to search for
	 * @param exactMatch - if true, it will only return a node that equals() the input. Otherwise, contains() will be used.
	 * @return the DialogueNode that meets the criteria, returns null if a match is not found
	 */
	public Stack<DialogueNode> findNodeWithTag(String tag, boolean exactMatch) {
		return findNode(new SearchRule() {

			@Override
			public boolean check(DialogueNode node) {
				String nodeTag = node.getTag();
				
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
	 * Find a DialogueNode in the Project with the given name.
	 * 
	 * @param name - the name to search for
	 * @param exactMatch - if true, it will only return a node that equals() the input. Otherwise, contains() will be used.
	 * @return the DialogueNode that meets the criteria, returns null if a match is not found
	 */
	public Stack<DialogueNode> findNodeWithName(String name, boolean exactMatch) {
		return findNode(new SearchRule() {

			@Override
			public boolean check(DialogueNode node) {
				String nodeName = node.getName();
				
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
	public Stack<DialogueNode> findNode(SearchRule rule) {
		
		Stack<DialogueNode> found = new Stack<DialogueNode>();
		
		for (int i = 0; i < nodes.size(); i++) {
			DialogueNode node = nodes.get(i);
			
			if (rule.check(node)) {
				found.push(node);
			}
		}
		
		return found;
	}
	
	public interface SearchRule {
		/**
		 * This DialogueNode is passed in by findNode as it iterates through all of the nodes in the Project.
		 * 
		 * Return true to add the DialogueNode to the list of DialogueNodes that pass your particular test. 
		 * Return false to ignore it and continue.
		 * 
		 * @param node
		 * @return
		 */
		public boolean check(DialogueNode node);
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
	public void disconnect(DialogueNodeConnector connector1, DialogueNodeConnector connector2) {
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
	public void disconnectAll(DialogueNodeConnector connector) {
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			
			if (c.contains(connector)) {
				connections.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Checks if the two connectors have a Connection.
	 */
	public boolean isConnected(DialogueNodeConnector connector1, DialogueNodeConnector connector2) {
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
	public boolean connectionExists(DialogueNodeConnector connector1, DialogueNodeConnector connector2) {
		for (int i = 0; i < connections.size(); i++) {
			Connection c = connections.get(i);
			
			if (c.represents(connector1, connector2)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Utility function for fetching a DialogueNodeConnector with its UID.
	 */
	public DialogueNodeConnector getDialogueNodeConnector(String uid) {
		for (int i = 0; i < nodes.size(); i++) {
			ArrayList<DialogueNodeConnector> connectors = nodes.get(i).getAllConnectors();
			
			for (int j = 0; j < connectors.size(); j++) {
				DialogueNodeConnector connector = connectors.get(j);
				
				if (connector.getUID().equals(uid)) {
					return connector;
				}
			}
		}
		
		return null;
	}
}
