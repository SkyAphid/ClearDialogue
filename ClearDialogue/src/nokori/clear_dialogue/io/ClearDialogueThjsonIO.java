package nokori.clear_dialogue.io;

import java.io.File;

import nokori.clear_dialogue.project.Project;

/**
 * This is a THJSON implementation for dialogue I/O. THJSON is a custom human-readable format created by Puppygames:
 * https://github.com/Puppygames/thjson
 * 
 * A compiled version is included with ClearDialogue as a jar.
 */
public class ClearDialogueThjsonIO implements ClearDialogueIO {

	@Override
	public void exportProject(Project project, File f) throws Exception {
		
	}

	@Override
	public Project importProject(File f) throws Exception {
		return null;
	}

	@Override
	public String getTypeName() {
		return "THJSON";
	}

}
