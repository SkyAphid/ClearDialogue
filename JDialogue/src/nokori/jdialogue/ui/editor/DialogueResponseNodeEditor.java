package nokori.jdialogue.ui.editor;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleClassedTextArea;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.ui.node.DialogueNodePane;

public class DialogueResponseNodeEditor extends DialogueNodeEditor {

	private StyleClassedTextArea textArea;

	public DialogueResponseNodeEditor(JDialogueCore core, DialogueResponseNode node, DialogueNodePane pane, Font titleFont, Font textFont) {
		super(core, node, pane, titleFont);
		
		String defaultText = "";
		
		//Organize responses as individual lines
		for (int i = 0; i < node.getResponses().size(); i++) {
			defaultText += node.getResponses().get(i).getText() + "\n";
		}

		//Make a TextArea that's numbered to show the response num
		textArea = new StyleClassedTextArea();
		textArea.insertText(0, defaultText);
		textArea.setWrapText(false);
		textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
		textArea.setStyle("-fx-font-family: " + textFont.getName() + "; -fx-font-size: " + textFont.getSize() + "pt;");
		
		//Virtual scroll pane
		VirtualizedScrollPane<StyleClassedTextArea> scrollPane = new VirtualizedScrollPane<StyleClassedTextArea>(textArea);
		
		StackPane.setAlignment(scrollPane, Pos.CENTER);
		StackPane.setMargin(scrollPane, new Insets(START_BODY_Y, 20, 20, 20));

		getChildren().add(scrollPane);
	}

	protected void dispose(JDialogueCore core, DialogueNode node, DialogueNodePane dialogueNodePane, Rectangle background) {
		//On dispose, sync the dialogueNode with the new data
		DialogueResponseNode dialogueNode = ((DialogueResponseNode) node);
		dialogueNode.clearResponses();
		
		for (int i = 0; i < textArea.getParagraphs().size(); i++) {
			String paragraph = textArea.getParagraph(i).getText();
			
			if (!paragraph.trim().isEmpty()) {
				dialogueNode.addResponse(paragraph);
			}
		}
		
		super.dispose(core, node, dialogueNodePane, background);
	}
}
