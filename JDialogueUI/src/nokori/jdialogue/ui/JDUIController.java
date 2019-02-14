package nokori.jdialogue.ui;

import nokori.jdialogue.project.Project;

/**
 * This is a pass-around class that allows JDialogue to communicate data around the program, such as the current project, context hints, etc.
 * 
 */
public class JDUIController {
	private Project project = new Project();
	
	private String contextHint = "Context Hint Test";

	public String getContextHint() {
		return contextHint;
	}

	public void setContextHint(String contextHint) {
		this.contextHint = contextHint;
	}

	public Project getProject() {
		return project;
	}
}
