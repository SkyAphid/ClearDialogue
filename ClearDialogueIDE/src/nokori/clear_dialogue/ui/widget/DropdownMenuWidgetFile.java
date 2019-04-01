package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;

import nokori.clear_dialogue.ui.SharedResources;

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
	
	public DropdownMenuWidgetFile(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(0), WIDGET_PADDING, sharedResources.getNotoSans(), LABEL, OPTIONS);
	}

	@Override
	protected void optionSelected(String option, int index) {
		switch(option) {
		
		}
	}

}
