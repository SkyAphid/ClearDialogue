package nokori.clear_dialogue.ui.widget.node;

import nokori.clear_dialogue.project.DialogueText;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.ConnectorWidget.ConnectorType;

public class DraggableDialogueTextWidget extends DraggableDialogueWidget {
	
	private ConnectorWidget outConnector;

	public DraggableDialogueTextWidget(SharedResources sharedResources, DialogueText dialogue) {
		super(sharedResources, dialogue);
		
		outConnector = new ConnectorWidget(dialogue.getOutConnector(), ConnectorType.OUT);
		addChildInFrontOf(inConnector, outConnector);
	}
	
	@Override
	public void requestRemoval(boolean flagForDeletion) {
		super.requestRemoval(flagForDeletion);	
		fadeOutConnector(outConnector);
	}
}
