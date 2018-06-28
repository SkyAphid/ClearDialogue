package nokori.jdialogue.io;

import java.io.File;

import javafx.stage.FileChooser.ExtensionFilter;
import nokori.jdialogue.project.Project;

public interface JDialogueIO {
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
	 * File extension filter for the dialogs.
	 */
	public ExtensionFilter getExtensionFilter();
	
	/**
	 * The name of the file type of file this exporter/importer manages.
	 */
	public String getTypeName();
}
