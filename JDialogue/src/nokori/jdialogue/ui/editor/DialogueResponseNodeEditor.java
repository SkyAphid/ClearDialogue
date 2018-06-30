package nokori.jdialogue.ui.editor;

import java.util.ArrayList;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueResponseNode.Response;
import nokori.jdialogue.ui.node.DialogueNodePane;

public class DialogueResponseNodeEditor extends DialogueNodeEditor {

	private InlineCssTextArea textArea;

	public DialogueResponseNodeEditor(JDialogueCore core, DialogueResponseNode node, DialogueNodePane pane, Font titleFont, Font textFont) {
		super(core, node, pane, titleFont);
		
		String defaultText = "";
		
		//Organize responses as individual lines
		for (int i = 0; i < node.getResponses().size(); i++) {
			defaultText += node.getResponses().get(i).getText() + "\n";
		}

		//Make a TextArea that's numbered to show the response num
		textArea = new InlineCssTextArea();
		textArea.replaceText(defaultText);
		textArea.setWrapText(false);
		textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
		textArea.setStyle("-fx-font-family: '" + textFont.getFamily() + "'; -fx-font-size: " + textFont.getSize());
		
		//Virtual scroll pane
		VirtualizedScrollPane<InlineCssTextArea> scrollPane = new VirtualizedScrollPane<InlineCssTextArea>(textArea);
		
		StackPane.setAlignment(scrollPane, Pos.CENTER);
		StackPane.setMargin(scrollPane, new Insets(START_BODY_Y, 20, 20, 20));

		getChildren().add(scrollPane);
	}

	protected void dispose(JDialogueCore core, DialogueNode node, DialogueNodePane dialogueNodePane, Rectangle background) {
		//On dispose, sync the dialogueNode with the new data
		DialogueResponseNode dialogueNode = ((DialogueResponseNode) node);
		
		ArrayList<Response> oldResponses = new ArrayList<Response>(dialogueNode.getResponses());
		dialogueNode.getResponses().clear();
		
		for (int i = 0; i < textArea.getParagraphs().size(); i++) {
			String paragraph = textArea.getParagraph(i).getText();
			
			if (!paragraph.trim().isEmpty()) {
				if (i < oldResponses.size()) {
					Response oldResponse = oldResponses.get(i);
					
					if (!paragraph.equals(oldResponse.getText())) {
						oldResponse.setText(paragraph);
					}
					
					dialogueNode.getResponses().add(oldResponse);
				}else {
					dialogueNode.addResponse(paragraph);
				}
			}
		}
		
		oldResponses.removeAll(dialogueNode.getResponses());
		
		for (int i = 0; i < oldResponses.size(); i++) {
			oldResponses.get(i).getOutConnector().disconnectAll();
		}
		
		super.dispose(core, node, dialogueNodePane, background);
	}
}
