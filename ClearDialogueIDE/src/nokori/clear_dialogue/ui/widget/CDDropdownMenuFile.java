package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueWidgetAssembly.*;

import nokori.clear_dialogue.ui.SharedResources;

public class CDDropdownMenuFile extends CDDropdownMenu {

	private static final String LABEL = "FILE";
	
	private static final String[] OPTIONS = {
		"NEW PROJECT",
		"PROJECT DIR...",
		"MERGE PROJECT...",
		"EXPORT JSON...",
		"IMPORT JSON..."
	};
	
	public CDDropdownMenuFile(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(0), WIDGET_PADDING, sharedResources.getNotoSans(), LABEL, OPTIONS);
	}

	@Override
	protected void optionSelected(String option, int index) {
		System.out.println(option + " " + index);
	}

}
