package nokori.clear_dialogue.io;

import java.io.File;

import nokori.clear_dialogue.project.Project;

public interface ClearDialogueIO {
	/**
	 * Export functionality.
	 */
	public void exportProject(Project project, File f) throws Exception;
	
	/**
	 * Import functionality.
	 * 
	 * If the import fails, return null. JDialogueCore will automatically handle it correctly in that case.
	 */
	public Project importProject(File f) throws Exception;
	
	/**
	 * The name of the file type of file this exporter/importer manages.
	 */
	public String getTypeName();
}
