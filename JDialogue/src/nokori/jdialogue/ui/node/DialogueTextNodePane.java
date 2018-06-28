package nokori.jdialogue.ui.node;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.ui.editor.DialogueTextNodeEditor;
import nokori.jdialogue.ui.node.DialogueNodeConnectorArc.ConnectorType;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueTextNodePane extends DialogueNodePane{
	
	private Label body;
	
	public DialogueTextNodePane(JDialogueCore core, DialogueTextNode node, DropShadow shadow, Font titleFont, Font textFont) {
		super(core, node, shadow, titleFont);
		
		//Out-Connector
		DialogueNodeConnectorArc outConnector = new DialogueNodeConnectorArc(core, this, ConnectorType.OUT, node.getOutConnector());

		//Body Text Viewer
		body = new Label(node.getText());
		body.setMaxWidth(WIDTH - 20f);
		body.setMaxHeight(HEIGHT - TITLE_HEIGHT - 20f); 
		body.setFont(textFont);
		body.setTextFill(Color.BLACK);
		body.setWrapText(true);
		body.setMouseTransparent(true);
		
		StackPane.setAlignment(body, Pos.BOTTOM_CENTER);
		StackPane.setMargin(body, new Insets(0, 10, 10, 10));
		
		getChildren().add(outConnector);
		getChildren().add(body);
		
		//Open Editor
		setOnMouseClicked(event -> {
			if (checkDispose(event, core)) {
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
		body.setText(((DialogueTextNode) node).getText());
	}
}