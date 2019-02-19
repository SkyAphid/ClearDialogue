package nokori.jdialogue.ui.dialogue_nodes;

import nokori.jdialogue.project.DialogueResponse;
import nokori.jdialogue.ui.JDUIController;

public class DialogueResponseNode extends DialogueNode {

	public DialogueResponseNode(JDUIController controller, DialogueResponse dialogue) {
		super(controller, dialogue);
		
		heightResizable = false;
		
		
	}

}
