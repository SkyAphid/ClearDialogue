package nokori.clear_dialogue.io;

import java.io.File;
import java.util.Locale;

import nokori.clear_dialogue.project.Project;

/**
 * This class compiles all of the available default ClearDialogue IO systems into one class and will auto-detect which one to use based on the file.
 */
public class ClearDialogueAutoIO implements ClearDialogueIO {

	@Override
	public void exportProject(Project project, File f) throws Exception {
		//JSON Export
		if (isJSONFile(f)) {
			new ClearDialogueJsonIO().exportProject(project, f);
		}
		
		//THJSON Export
		if (isTHJSONFile(f)) {
			new ClearDialogueThjsonIO().exportProject(project, f);
		}
	}

	@Override
	public Project importProject(File f) throws Exception {
		//JSON Import
		if (isJSONFile(f)) {
			return new ClearDialogueJsonIO().importProject(f);
		}
		
		//THJSON Import
		if (isTHJSONFile(f)) {
			return new ClearDialogueThjsonIO().importProject(f);
		}
		
		return null;
	}
	
	private static boolean isTHJSONFile(File f) {
		return isFile(f, ".thjson");
	}
	
	private static boolean isJSONFile(File f) {
		return isFile(f, ".json");
	}
	
	private static boolean isFile(File f, String extension) {
		return f.getName().toLowerCase(Locale.ENGLISH).endsWith(extension);
	}

	@Override
	public String getTypeName() {
		return "JSON, THJSON";
	}

}
