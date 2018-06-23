package nokori.jdialogue;

import java.util.ArrayList;

/**
 * This class represents the Project, containing a list of all the nodes that the user has made, along with the project name.
 *
 */
public class Project {

	//The name of the project
	private String name = "Default Project";
	
	//All nodes contained by this project
	private ArrayList<DialogueNode> nodes = new ArrayList<DialogueNode>();
	
	//Viewport coordinates, used for dragging around the editor
	private int viewportX = 0, viewportY = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<DialogueNode> getNodes() {
		return nodes;
	}
	
	public void addNode(DialogueNode node) {
		nodes.add(node);
	}
	
	public void removeNode(DialogueNode node) {
		nodes.remove(node);
	}
	
	public int getNumNodes() {
		return nodes.size();
	}

	public int getViewportX() {
		return viewportX;
	}
	
	public void addToViewportX(int num) {
		viewportX += num;
	}

	public void setViewportX(int viewportX) {
		this.viewportX = viewportX;
	}

	public int getViewportY() {
		return viewportY;
	}
	
	public void addToViewportY(int num) {
		viewportY += num;
	}

	public void setViewportY(int viewportY) {
		this.viewportY = viewportY;
	}
}
