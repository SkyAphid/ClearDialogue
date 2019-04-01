package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;

import nokori.clear_dialogue.ui.SharedResources;

public class DropdownMenuWidgetTool extends DropdownMenuWidget {

	private static final String LABEL = "TOOL";
	
	private static final String OPTION_REPLACE = "REPLACE...";
	private static final String OPTION_MULTI_REPLACE = "MULTI-REPLACE...";
	private static final String OPTION_VIEW_SYNTAX = "VIEW SYNTAX";
	private static final String OPTION_REFRESH_SYNTAX = "REFRESH SYNTAX";
	private static final String OPTION_SET_SYNTAX = "SET SYNTAX...";
	
	private static final String[] OPTIONS = {
		OPTION_REPLACE,
		OPTION_MULTI_REPLACE,
		OPTION_VIEW_SYNTAX,
		OPTION_REFRESH_SYNTAX,
		OPTION_SET_SYNTAX
	};
	
	private SharedResources sharedResources;
	
	public DropdownMenuWidgetTool(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(1), WIDGET_PADDING, sharedResources.getNotoSans(), LABEL, OPTIONS);
		this.sharedResources = sharedResources;
	}

	@Override
	protected void optionSelected(String option, int index) {
		switch(option) {
		case OPTION_VIEW_SYNTAX:
			new PopupMessageWidget(sharedResources, "This is a test.").show();
			break;
		}
	}

}
