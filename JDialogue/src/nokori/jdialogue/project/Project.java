package nokori.jdialogue.project;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents the Project, containing a list of all the nodes that the user has made, along with the project name.
 *
 */
public class Project implements Serializable {
	
	private static final long serialVersionUID = -8369866434879310100L;
	
	//The name of the project
	private String name = "Default Project";
	
	//Remember viewport data for next use
	private double viewportX, viewportY, viewportScale;
	
	//All nodes contained by this project
	private ArrayList<DialogueNode> nodes = new ArrayList<DialogueNode>();
	
	//All connections between the various nodes are stored here
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	
	public Project(double viewportX, double viewportY, double viewportScale) {
		this.viewportX = viewportX;
		this.viewportY = viewportY;
		this.viewportScale = viewportScale;
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
	 * Delete all Connections to this connector
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
	 * Checks if the two connectors have a Connection
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
	 * Checks if a similar Connection already exists
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
}
