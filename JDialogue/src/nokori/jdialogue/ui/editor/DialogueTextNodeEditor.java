package nokori.jdialogue.ui.editor;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.reactfx.Subscription;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.ui.node.DialogueNodePane;
import nokori.jdialogue.ui.util.JDialogueUtils;

public class DialogueTextNodeEditor extends DialogueNodeEditor {

	private Subscription sub;
	
	public DialogueTextNodeEditor(JDialogueCore core, DialogueTextNode node, DialogueNodePane pane, Font titleFont, Font textFont) {
		super(core, node, pane, titleFont);
		
		InlineCssTextArea textArea = new InlineCssTextArea();
		textArea.setWrapText(true);
		
		//Update body text
		textArea.textProperty().addListener((o, oldText, newText) -> {
			node.setText(newText);
		});
		
		textArea.setStyle("-fx-font-family: '" + textFont.getFamily() + "'; -fx-font-size: " + textFont.getSize() + ";");
		
		//Syntax
		sub = JDialogueUtils.addSyntaxSubscription(textArea, core.getSyntax(), JDialogueCore.SYNTAX_HIGHLIGHT_COLOR);
		
		//Set text
		textArea.replaceText(node.getText());
		
		VirtualizedScrollPane<InlineCssTextArea> scrollPane = new VirtualizedScrollPane<InlineCssTextArea>(textArea);
		
		StackPane.setAlignment(scrollPane, Pos.CENTER);
		StackPane.setMargin(scrollPane, new Insets(START_BODY_Y, 20, 20, 20));
		
		getChildren().add(scrollPane);
	}

	protected void dispose(JDialogueCore core, DialogueNode node, DialogueNodePane dialogueNodePane, Rectangle background) {
		sub.unsubscribe();
		
		super.dispose(core, node, dialogueNodePane, background);
	}
}
