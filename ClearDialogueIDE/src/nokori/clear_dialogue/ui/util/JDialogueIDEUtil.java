package nokori.clear_dialogue.ui.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear.windows.util.TinyFileDialog.Icon;
import nokori.clear_dialogue.io.JDialogueIO;
import nokori.clear_dialogue.project.Project;

public class JDialogueIDEUtil {
	
	/**
	 * Opens a project import dialogue using the given JDialogue I/O system and returns the loaded project. If the project fails to load, null will be returned instead.
	 */
	public static Project showImportProjectDialog(String title, JDialogueIO io) {
		String filetype = io.getTypeName();
		
		String filterDescription = filetype + " Files";

		File f = TinyFileDialog.showOpenFileDialog(title, getProjectDirectory(), filterDescription, filetype, "");
		
		if (f != null) {
			try {
				return io.importProject(f);
			} catch(Exception e) {
				e.printStackTrace();
				TinyFileDialog.showMessageDialog("Caught " + e.getClass().getName(), e.getMessage(), Icon.ERROR);
			}
		} 
		
		return null;
	}
	
	public static void showExportProjectDialog(Project project, JDialogueIO io) {
		String filetype = io.getTypeName();
		
		String title = "Export " + io.getTypeName() + " Project";
		String filterDescription = filetype + " Files";
		
		File f = TinyFileDialog.showSaveFileDialog(title, getProjectDirectory(), filterDescription, filetype, false);
		
		try {
			io.exportProject(project, f);
			TinyFileDialog.showMessageDialog("Export Success", project.getName() + " was successfully exported.", Icon.INFORMATION);
		} catch (Exception e) {
			e.printStackTrace();
			TinyFileDialog.showMessageDialog(e.getClass().getName() + " Caught", e.getMessage(), Icon.ERROR);
		}
	}
	
	/**
	 * Set the Project directory (where the FileChoosers will open to by default)
	 */
	public static void showProjectDirectorySelectDialog() {
		File f = new File("project_directory.ini");
		
		Properties props = new Properties();
		
		if(f.exists()){
			try {
				props.load(new FileReader(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File defaultLocation = new File(".");
		
		String projectDirectory = props.getProperty("projectDir");
		
		if (projectDirectory != null) {
			File location = new File(projectDirectory);
			
			if (location.exists()) {
				defaultLocation = location;
			}
		}

		File dir = TinyFileDialog.showOpenFolderDialog("Select Project Directory", defaultLocation.exists() ? defaultLocation : new File(""));

		if (dir != null) {
			props.setProperty("projectDir", dir.getPath());
			
			try{
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);
				props.store(fos, "");
				fos.flush();
				fos.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Get the set directory for saving projects (convenience)
	 */
	public static File getProjectDirectory() {
		File f = new File("project_directory.ini");
		
		Properties props = new Properties();
		
		if(f.exists()){
			try {
				props.load(new FileReader(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String projectDirectory = props.getProperty("projectDir");
		
		if (projectDirectory != null) {
			File dir = new File(projectDirectory);
			
			if (dir.exists()) {
				return dir;
			}
		}
		
		return new File(".");
	}
	
	/**
	 * Set the location of the syntax file to load at startup
	 * @param stage
	 */
	public static void showSyntaxFileSelectDialog() {
		//Try to get the current directory first so that the filechooser will open in that location
		File f = new File("syntax_directory.ini");
		
		Properties props = new Properties();
		
		if(f.exists()){
			try {
				props.load(new FileReader(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File defaultLocation = new File(".");
		
		String syntaxLocation = props.getProperty("syntaxFile");
		
		if (syntaxLocation != null) {
			File location = new File(syntaxLocation).getParentFile();
			
			if (location.exists()) {
				defaultLocation = location;
			}
		}

		//Select the syntax file
		File file = TinyFileDialog.showOpenFileDialog("Select Syntax File", defaultLocation.exists() ? defaultLocation : new File(""), "Syntax Text Files", "txt", "");

		if (file != null && file.exists()) {
			//If the file is valid, record the location
			props.setProperty("syntaxFile", file.getAbsolutePath());
			
			//Save the location
			try{
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);
				props.store(fos, "");
				fos.flush();
				fos.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			//Reload the current syntax
			//TODO: loadSyntax();
		}
	}
}
