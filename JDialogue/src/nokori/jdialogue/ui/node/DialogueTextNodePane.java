package nokori.jdialogue.ui.node;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.ui.editor.DialogueTextNodeEditor;
import nokori.jdialogue.ui.node.DialogueNodeConnectorArc.ConnectorType;
import nokori.jdialogue.ui.util.JDialogueUtils;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueTextNodePane extends DialogueNodePane{
	
	private InlineCssTextArea body;
	
	public DialogueTextNodePane(JDialogueCore core, DialogueTextNode node, DropShadow shadow, Font titleFont, Font textFont) {
		super(core, node, shadow, titleFont);
		
		//Out-Connector
		DialogueNodeConnectorArc outConnector = new DialogueNodeConnectorArc(core, this, ConnectorType.OUT, node.getOutConnector());

		//Body Text Viewer
		body = new InlineCssTextArea();
		body.replaceText(node.getText());
		body.setMaxWidth(WIDTH - 20f);
		body.setMaxHeight(HEIGHT - TITLE_HEIGHT - 20f); 
		body.setWrapText(true);
		body.setEditable(false);
		body.setMouseTransparent(true);

		body.setStyle("-fx-font-family: '" + textFont.getFamily() + "'; -fx-font-size: " + textFont.getSize() + ";");

		//Finalize
		JDialogueUtils.computeHighlighting(body, core.getSyntax(), JDialogueCore.SYNTAX_HIGHLIGHT_COLOR);
		
		StackPane.setAlignment(body, Pos.BOTTOM_CENTER);
		StackPane.setMargin(body, new Insets(0, 10, 10, 10));
		
		getChildren().add(outConnector);
		getChildren().add(body);
		
		//Open Editor
		setOnMouseClicked(event -> {
			if (checkDispose(event)) {
				return;
			}
			
			if (event.getClickCount() > 1) {
				core.getUIPane().getChildren().add(new DialogueTextNodeEditor(core, node, this, titleFont, textFont));
			}
		});
	}
	
	@Override
	public void refresh(JDialogueCore core) {
		super.refresh(core);
		body.replaceText(((DialogueTextNode) node).getText());
		JDialogueUtils.computeHighlighting(body, core.getSyntax(), JDialogueCore.SYNTAX_HIGHLIGHT_COLOR);
	}
}
