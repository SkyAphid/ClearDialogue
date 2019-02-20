package nokori.jdialogue.ui;

import lwjgui.theme.Theme;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.dialogue_nodes.DialogueNode;
import nokori.jdialogue.ui.layout.CanvasPane;
import nokori.jdialogue.ui.theme.JDialogueTheme;

/**
 * This is a pass-around class that allows JDialogue to communicate data around the program, such as the current project, context hints, etc.
 */
public class SharedResources {
	private JDialogueTheme theme;
	private CanvasPane canvasPane;
	private String contextHint;
	
	private Project project = new Project();
	
	public SharedResources() {
		theme = new JDialogueTheme();
		Theme.setTheme(theme);
		
		canvasPane = new CanvasPane(this);
		
		resetContextHint();
	}
	
	/**
	 * Gets the current color theme for LWJGUI.
	 * @return
	 */
	public JDialogueTheme getTheme() {
		return theme;
	}

	/**
	 * Gets the current context hint visible at the bottom of the screen. Context hints give contextual information on how to use the IDE.
	 * @return
	 */
	public String getContextHint() {
		return contextHint;
	}

	/**
	 * Sets the current context hint.
	 * @param contextHint
	 */
	public void setContextHint(String contextHint) {
		this.contextHint = contextHint;
	}
	
	/**
	 * Resets the context hint back to the general controls for navigating the canvas.
	 */
	public void resetContextHint() {
		contextHint = "Drag LMB = Pan Canvas | RMB = Context Menu";
	}

	/**
	 * Gets the currently active JDialogue Project.
	 * @return
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Sets a new JDialogue Project and refreshes the Canvas with its data.
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;
		canvasPane.refresh();
	}
	
	/**
	 * Gets the canvas pane, which contains all of the DialogueNodes.
	 * @return
	 */
	public CanvasPane getCanvasPane() {
		return canvasPane;
	}
	
	/**
	 * Adds a new DialogueNode to the canvas and project.
	 * 
	 * @param node
	 */
	public void addDialogueNode(DialogueNode node) {
		canvasPane.getChildren().add(node);
		project.addDialogue(node.getDialogue());
	}
	
	/**
	 * Removes a DialogueNode from the canvas and project.
	 * 
	 * @param node
	 */
	public void removeDialogueNode(DialogueNode node) {
		canvasPane.getChildren().remove(node);
		project.removeDialogue(node.getDialogue());
	}
}
