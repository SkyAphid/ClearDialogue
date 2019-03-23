package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueWidgetAssembly.*;

import nokori.clear_dialogue.ui.SharedResources;

public class CDDropdownMenuTool extends CDDropdownMenu {

	private static final String LABEL = "TOOL";
	
	private static final String[] OPTIONS = {
		"REPLACE...",
		"MULTI-REPLACE...",
		"VIEW SYNTAX",
		"REFRESH SYNTAX",
		"SET SYNTAX..."
	};
	
	public CDDropdownMenuTool(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(1), WIDGET_PADDING, sharedResources.getNotoSans(), LABEL, OPTIONS);
	}

	@Override
	protected void optionSelected(String option, int index) {
		System.out.println(option + " " + index);
	}

}
