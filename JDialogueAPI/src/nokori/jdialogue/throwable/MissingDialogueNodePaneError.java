package nokori.jdialogue.throwable;

import nokori.jdialogue.project.DialogueNode;

public class MissingDialogueNodePaneError extends Error {

	private static final long serialVersionUID = 5658027467367739969L;

	public MissingDialogueNodePaneError(DialogueNode node){
        super("Unable to find DialogueNodePane class that corresponds to " + node.getClass().getName() + "!"
        		+ "\nBe sure to complete your implementation of your custom DialogueNode type by creating both a corresponding DialogueNode (data) AND DialogueNodePane (ui) class.");
    }
}
