package nokori.jdialogue.ui.util;

import java.io.File;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.io.JDialogueJsonIO;
import nokori.jdialogue.project.Project;

/**
 * Opens a dialogue so that the user can access the merging tools for projects
 */
public class MergeTool {

	public static void openMergeToolDialog(Stage stage, JDialogueCore core, File projectDir, Project project) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select JSON Project File to Merge");
		fileChooser.setInitialDirectory(projectDir);
		
		File file = fileChooser.showOpenDialog(stage);

		if (file != null) {
			try {
				project.mergeProject(new JDialogueJsonIO().importProject(file));
				core.refreshUI();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
