package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;

import nokori.clear.windows.Window;
import nokori.clear_dialogue.ui.SharedResources;

public class DropdownMenuWidgetNode extends DropdownMenuWidget {

	private static final String LABEL = "+NODE";
	
	private static final String OPTION_DIALOGUE = "+DIALOGUE";
	private static final String OPTION_RESPONSE = "+RESPONSE";
	
	private static final String[] OPTIONS = {
		OPTION_DIALOGUE,
		OPTION_RESPONSE
	};
	
	private SharedResources sharedResources;
	
	public DropdownMenuWidgetNode(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(2), WIDGET_PADDING, sharedResources.getNotoSans(), LABEL, OPTIONS);
		this.sharedResources = sharedResources;
	}

	@Override
	protected void optionSelected(Window window, String option, int index) {
		switch(option) {
		case OPTION_DIALOGUE:
			sharedResources.getCanvas().addDialogueTextNode();
			break;
		case OPTION_RESPONSE:
			sharedResources.getCanvas().addDialogueResponseNode();
			break;
		}
	}

}
