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
}
