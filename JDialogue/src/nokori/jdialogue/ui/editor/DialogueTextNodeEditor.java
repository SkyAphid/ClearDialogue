package nokori.jdialogue.ui.editor;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.ui.node.DialogueNodePane;

public class DialogueTextNodeEditor extends DialogueNodeEditor {

	public DialogueTextNodeEditor(JDialogueCore core, DialogueTextNode node, DialogueNodePane pane, Font titleFont, Font textFont) {
		super(core, node, pane, titleFont);
		
		InlineCssTextArea textArea = new InlineCssTextArea();
		textArea.replaceText(node.getText());
		textArea.setStyle("-fx-font-family: '" + textFont.getFamily() + "'; -fx-font-size: " + textFont.getSize());
		textArea.setWrapText(true);
		
		//Update body text
		textArea.textProperty().addListener((o, oldText, newText) -> {
			node.setText(newText);
		});
		
		VirtualizedScrollPane<InlineCssTextArea> scrollPane = new VirtualizedScrollPane<InlineCssTextArea>(textArea);
		
		StackPane.setAlignment(scrollPane, Pos.CENTER);
		StackPane.setMargin(scrollPane, new Insets(START_BODY_Y, 20, 20, 20));
		
		getChildren().add(scrollPane);
	}

}
