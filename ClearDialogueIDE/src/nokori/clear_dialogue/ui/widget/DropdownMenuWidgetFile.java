package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;

import java.io.File;

import nokori.clear.windows.Window;
import nokori.clear_dialogue.io.ClearDialogueAutoIO;
import nokori.clear_dialogue.io.ClearDialogueJsonIO;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.ClearDialogueCanvas;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.util.FileUtils;

public class DropdownMenuWidgetFile extends DropdownMenuWidget {

	private static final String LABEL = "FILE";
	
	private static final String OPTION_NEW_PROJECT = "NEW PROJECT";
	private static final String OPTION_PROJECT_DIR = "PROJECT DIR...";
	private static final String OPTION_MERGE_PROJECT = "MERGE PROJECT...";
	private static final String OPTION_EXPORT_JSON = "EXPORT JSON...";
	private static final String OPTION_IMPORT_JSON = "IMPORT JSON...";
	
	private static final String[] OPTIONS = {
		OPTION_NEW_PROJECT,
		OPTION_PROJECT_DIR,
		OPTION_MERGE_PROJECT,
		OPTION_EXPORT_JSON,
		OPTION_IMPORT_JSON
	};
	
	private SharedResources sharedResources;
	
	public DropdownMenuWidgetFile(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(0), WIDGET_PADDING, sharedResources.getNotoSans(), LABEL, OPTIONS);
		this.sharedResources = sharedResources;
	}

	@Override
	protected void optionSelected(Window window, String option, int index) {
		Project project = sharedResources.getProject();
		File projectFileLocation = sharedResources.getProjectFileLocation();
		
		ClearDialogueCanvas canvas = sharedResources.getCanvas();

		collapse(window);
		
		switch(option) {
		case OPTION_NEW_PROJECT:
			canvas.refresh(new Project());
			break;
		case OPTION_PROJECT_DIR:
			FileUtils.showProjectDirectorySelectDialog();
			break;
		case OPTION_MERGE_PROJECT:
			Project merge = FileUtils.showImportProjectDialog("Merge Project", new ClearDialogueAutoIO(), null);
			
			if (merge != null) {
				project.mergeProject(merge);
			}

			sharedResources.getCanvas().refresh(project);
			
			break;
		case OPTION_EXPORT_JSON:
			FileUtils.showExportProjectDialog(project, projectFileLocation, new ClearDialogueJsonIO());
			break;
		case OPTION_IMPORT_JSON:
			FileUtils.showImportProjectDialog("Import JSON Dialogue", new ClearDialogueJsonIO(), sharedResources);
			break;
		}
	}

}
