package nokori.clear_dialogue.ui.util;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.apps.ClearInputApp;
import nokori.clear.windows.GLFWException;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueResponse;
import nokori.clear_dialogue.project.DialogueResponse.Response;
import nokori.clear_dialogue.project.DialogueText;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget;

import java.io.File;

/**
 * All tools relating to mass-string replacing in dialogue projects.
 */
public class ReplaceUtils {
	private static final int INPUT_WIDTH = 400;
	private static final int INPUT_HEIGHT = 300;

	private static final File FONT_LOCATION = new File("res/fonts/NotoSans/");
	private static final ClearColor BUTTON_OUTLINE_FILL = ClearColor.CORAL;

	public static void runReplaceTool(SharedResources sharedResources) {
		ClearInputApp findDialog = new ClearInputApp(sharedResources.getIDECore(), INPUT_WIDTH, INPUT_HEIGHT, BUTTON_OUTLINE_FILL, FONT_LOCATION,
				"Find & Replace",
				"Input text content to find in nodes for replacement. The text must be formatted like this:" +
						"\n\n$FIND->$REPLACE\n\nTo escape the -> sign (in cases where the find case contains that), use the escape sequence \\ like so: \\->.",
				"") {

			@Override
			protected void confirmButtonPressed(String input) {
				String[] output = input.split("->");
				String find = "";
				String replace = "";
				boolean breakPoint = false;

				for (int i = 0; i < output.length; i++){
					System.out.println(output[i]);

					if (!breakPoint){
						find += output[i];
					} else{
						replace += output[i];
					}

					if (!output[i].endsWith("\\")){
						breakPoint = true;
					}
				}

				if (TinyFileDialog.showConfirmDialog("Confirm Find & Replace", find + "→" + replace,
						TinyFileDialog.InputType.OK_CANCEL, TinyFileDialog.Icon.QUESTION, false)){

					replace(sharedResources.getProject(), find, replace);
					sharedResources.refreshCanvas();
				}
			}
		};

		try{
			findDialog.show();
		} catch (GLFWException e){
			e.printStackTrace();
		}
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
