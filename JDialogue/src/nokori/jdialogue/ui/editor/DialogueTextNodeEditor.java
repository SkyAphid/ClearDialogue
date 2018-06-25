package nokori.jdialogue.ui.editor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.ui.node.DialogueNodePane;

public class DialogueTextNodeEditor extends DialogueNodeEditor {

	public DialogueTextNodeEditor(JDialogueCore core, DialogueTextNode node, DialogueNodePane pane, Font titleFont, Font textFont) {
		super(core, node, pane, titleFont);
		
		TextArea textArea = new TextArea(node.getText());
		textArea.setFont(textFont);
		textArea.setWrapText(true);
		
		//Update body text
		textArea.textProperty().addListener((o, oldText, newText) -> {
			node.setText(newText);
		});
		
		StackPane.setAlignment(textArea, Pos.CENTER);
		StackPane.setMargin(textArea, new Insets(START_BODY_Y, 20, 20, 20));
		
		getChildren().add(textArea);
	}

}
