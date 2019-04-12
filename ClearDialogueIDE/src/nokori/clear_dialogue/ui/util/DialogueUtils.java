package nokori.clear_dialogue.ui.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Properties;

import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear.windows.util.TinyFileDialog.Icon;
import nokori.clear_dialogue.io.ClearDialogueIO;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.SharedResources;

/**
 * General utilities for Dialogue project management.
 */
public class DialogueUtils {
	
	/**
	 * Opens a project import dialogue using the given JDialogue I/O system and returns the loaded project. If the project fails to load, null will be returned instead.
	 */
	public static Project showImportProjectDialog(String title, ClearDialogueIO io) {
		//Support multiple filetypes
		String filetypes[] = io.getTypeName().split(", ");
		
		//Add all filetypes to the description
		String filterDescription = "";
		
		for (int i = 0; i < filetypes.length; i++) {
			if (i > 0) {
				filterDescription += ", ";
			}
			
			filterDescription += filetypes[i];
		}
		
		filterDescription += " Files";

		//Finally open the TinyFileDialog for the support files
		File f = TinyFileDialog.showOpenFileDialog(title, getProjectDirectory(), filterDescription, filetypes[0], filetypes);
		
		//Import the file as a Project
		if (f != null) {
			try {
				return io.importProject(f);
			} catch(Exception e) {
				e.printStackTrace();
				TinyFileDialog.showMessageDialog("Caught " + e.getClass().getName(), e.getMessage(), Icon.ERROR);
			}
		} else {
			TinyFileDialog.showMessageDialog("ClearDialogue", "Project import cancelled.", Icon.INFORMATION);
		}
		
		return null;
	}
	
	public static void showExportProjectDialog(Project project, ClearDialogueIO io) {
		String filetype = io.getTypeName();
		
		String title = "Export " + io.getTypeName() + " Project";
		String filterDescription = filetype + " Files";
		
		File f = TinyFileDialog.showSaveFileDialog(title, getProjectDirectory(), filterDescription, filetype, false);
		
		if (f != null) {
			try {
				io.exportProject(project, f);
			} catch (Exception e) {
				e.printStackTrace();
				TinyFileDialog.showMessageDialog("Caught " + e.getClass().getName(), e.getMessage(), Icon.ERROR);
			}
		} else {
			TinyFileDialog.showMessageDialog("ClearDialogue", "Project export cancelled.", Icon.INFORMATION);
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
	 * Set the location of the syntax file to load at startup and subsequent refreshes
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
		}
	}
	
	/**
	 * @return the syntax file configured in the syntax file directory settings
	 */
	private static File getSyntaxFile() {
		/*
		 * Get user-set syntax location (if it exists)
		 */
		
		File f = new File(SharedResources.SYNTAX_FILE_LOCATION);

		Properties props = new Properties();
		
		if(f.exists()){
			try {
				props.load(new FileReader(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String projectDirectory = props.getProperty("syntaxFile");
		
		return (projectDirectory != null ? new File(projectDirectory) : null);
	}

	/**
	 * @return the syntax file using the current syntax directory settings.
	 */
	public static String loadSyntax() {
		
		File syntaxFile = getSyntaxFile();
		
		if (syntaxFile == null) {
			syntaxFile = new File("example_syntax.txt");
		}
		
		if (syntaxFile != null && syntaxFile.exists()) {
			try {
				
				//Read the syntax file
				return new String(Files.readAllBytes(syntaxFile.toPath()));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			System.err.println("loadSyntax() failed: Syntax file not found.");
		}
		
		return null;
	}
	
	public static void saveSyntax(String content) {
		File syntaxFile = getSyntaxFile();
		
		if (syntaxFile != null) {
			
			try (PrintWriter out = new PrintWriter(syntaxFile)){
				
				out.println(content);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.err.println("saveSyntax() failed: Syntax file not found.");
		}
	}
}
