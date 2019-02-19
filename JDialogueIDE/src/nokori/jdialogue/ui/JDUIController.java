package nokori.jdialogue.ui;

import lwjgui.theme.Theme;
import lwjgui.util.Cursor;
import lwjgui.util.Cursor.CursorType;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.dialogue_nodes.DialogueNode;
import nokori.jdialogue.ui.layout.CanvasPane;
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
	
	public static final Cursor ARROW_CURSOR = new Cursor(CursorType.ARROW);
	public static final Cursor HAND_CURSOR = new Cursor(CursorType.HAND);
	
	public JDUIController() {
		theme = new JDialogueTheme();
		Theme.setTheme(theme);
		
		canvasPane = new CanvasPane(this);
		
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
	
	public void disposeCursors() {
		
	}
}
