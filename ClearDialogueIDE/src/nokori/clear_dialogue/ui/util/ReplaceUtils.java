package nokori.clear_dialogue.ui.util;

import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueResponse;
import nokori.clear_dialogue.project.DialogueResponse.Response;
import nokori.clear_dialogue.project.DialogueText;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.SharedResources;

/**
 * All tools relating to mass-string replacing in dialogue projects.
 */
public class ReplaceUtils {
	public static void runReplaceTool(SharedResources sharedResources) {
		String[] findReplace = getFindReplaceInputs();
		replace(sharedResources.getProject(), findReplace[0], findReplace[1]);
	}
	
	private static String[] getFindReplaceInputs() {
		String find = TinyFileDialog.showInputDialog("Replace Tool", "Please input the string you want to find:", "");
		String replace = TinyFileDialog.showInputDialog("Replace Tool", "Find: " + find + "\nPlease input the replacement string:", find);
		return new String[] { find, replace };
	}
	
	private static void replace(Project project, String find, String replace) {
		for (int i = 0; i < project.getNumDialogue(); i++) {
			Dialogue dialogue = project.getDialogue(i);
			
			dialogue.setTitle(dialogue.getTitle().replace(find, replace));
			dialogue.setTags(dialogue.getTags().replace(find, replace));
			
			if (dialogue instanceof DialogueText) {
				DialogueText dialogueText = (DialogueText) dialogue;
				dialogueText.setText(dialogueText.getText().replace(find, replace));
			}
			
			if (dialogue instanceof DialogueResponse) {
				DialogueResponse dialogueResponse = (DialogueResponse) dialogue;
				
				for (int j = 0; j < dialogueResponse.getResponses().size(); j++) {
					Response response = dialogueResponse.getResponses().get(j);
					
					response.setText(response.getText().replace(find, replace));
				}
			}
		}
	}
}
