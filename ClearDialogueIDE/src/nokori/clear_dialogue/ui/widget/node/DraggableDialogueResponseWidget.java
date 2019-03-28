package nokori.clear_dialogue.ui.widget.node;

import nokori.clear.vg.ClearColor;
import nokori.clear_dialogue.project.DialogueResponse;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget.Mode;

public class DraggableDialogueResponseWidget extends DraggableDialogueWidget {

	private DialogueResponse dialogue;
	
	public DraggableDialogueResponseWidget(SharedResources sharedResources, DialogueResponse dialogue) {
		super(sharedResources, dialogue);
		this.dialogue = dialogue;
		
		content.setWordWrappingEnabled(false);
		content.setLineNumbersEnabled(true);
		content.setLineNumberRightPadding(10);
		content.setLineNumberLeftPadding(10);
		content.setLineNumberBackgroundFill(ClearColor.WHITE);
	}

	@Override
	protected float getMinHeight() {
		if (content != null && dialogue != null) {
			return (content.getY() + (content.getFontHeight() * (dialogue.getResponses().size() + 1)));
		} else {
			return super.getMinHeight();
		}
	}
}
