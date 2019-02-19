package nokori.jdialogue.ui;

import lwjgui.theme.Theme;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.components.CanvasPane;
import nokori.jdialogue.ui.dialogue_nodes.DialogueNode;
import nokori.jdialogue.ui.theme.JDialogueTheme;

/**
 * This is a pass-around class that allows JDialogue to communicate data around the program, such as the current project, context hints, etc.
 * 
 */
public class JDUIController {
	private JDialogueTheme theme;
	private CanvasPane canvasPane;
	private String contextHint;
	
	private Project project = new Project();
	
	public JDUIController() {
		theme = new JDialogueTheme();
		Theme.setTheme(theme);
		
		canvasPane = new CanvasPane(this, theme.getSansFont(), theme.getSerifFont());
		
		resetContextHint();
	}
	
	public JDialogueTheme getTheme() {
		return theme;
	}

	public String getContextHint() {
		return contextHint;
	}

	public void setContextHint(String contextHint) {
		this.contextHint = contextHint;
	}
	
	public void resetContextHint() {
		contextHint = "Drag LMB = Pan Canvas | RMB = Context Menu";
	}

	public Project getProject() {
		return project;
	}
	
	public CanvasPane getCanvasPane() {
		return canvasPane;
	}
	
	public void addDialogueNode(DialogueNode node) {
		canvasPane.getChildren().add(node);
		project.addDialogue(node.getDialogue());
	}
	
	public void removeDialogueNode(DialogueNode node) {
		canvasPane.getChildren().remove(node);
		project.removeDialogue(node.getDialogue());
	}
}
