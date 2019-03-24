package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueWidgetAssembly.*;

import nokori.clear_dialogue.ui.SharedResources;

public class DropdownMenuWidgetNode extends DropdownMenuWidget {

	private static final String LABEL = "+NODE";
	
	private static final String[] OPTIONS = {
		"DIALOGUE",
		"RESPONSE",
	};
	
	public DropdownMenuWidgetNode(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(2), WIDGET_PADDING, sharedResources.getNotoSans(), LABEL, OPTIONS);
	}

	@Override
	protected void optionSelected(String option, int index) {
		System.out.println(option + " " + index);
	}

}
