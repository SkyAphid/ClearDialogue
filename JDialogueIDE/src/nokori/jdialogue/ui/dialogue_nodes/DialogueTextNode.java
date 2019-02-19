package nokori.jdialogue.ui.dialogue_nodes;

import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.control.text_input.TextArea;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.floating.FloatingPane;
import nokori.jdialogue.project.DialogueText;
import nokori.jdialogue.ui.JDUIController;
import nokori.jdialogue.ui.dialogue_nodes.DialogueNodeConnector.ConnectorType;

public class DialogueTextNode extends DialogueNode {

	private FloatingPane textAreaPane;
	private TextArea textArea;
	
	public DialogueTextNode(JDUIController controller, DialogueText dialogue) {
		super(controller, dialogue);
		
		Font serifFont = controller.getTheme().getSerifFont();
		
		int edgePadding = 10;
		int topPadding = 70;
		
		/*
		 * Text Area
		 */
		textAreaPane = new FloatingPane();
		textAreaPane.setAbsolutePosition(LEFT_PADDING, TOP_PADDING + 70);
		textAreaPane.setAlignment(Pos.TOP_LEFT);
		textAreaPane.setMouseTransparent(true);
		
		textArea = new TextArea() {
			@Override
			public void render(Context context) {
				setMaxWidth(background.getWidth()- edgePadding);
				setPrefWidth(getMaxWidth());
				
				setMaxHeight(background.getHeight() - topPadding - edgePadding);
				setPrefHeight(getMaxHeight());
				
				super.render(context);
			}
		};
		textArea.setText(dialogue.getText());
		textArea.setWordWrap(true);
		
		textArea.setFont(serifFont);
		textArea.setFontSize(16);

		textArea.setBackground(null);
		textArea.setDecorated(false);
		textArea.setSelectionOutlineEnabled(false);
		
		textArea.setContextMenu(contextMenu);
		
		textAreaPane.getChildren().add(textArea);
		
		getChildren().add(textAreaPane);
		
		/*
		 * 
		 * Connectors
		 * 
		 */
		
		int connectorOffset = 1;
		
		DialogueNodeConnector inConnector = new DialogueNodeConnector(dialogue.getInConnector(), ConnectorType.IN) {
			@Override
			public void render(Context context) {
				double parentX = DialogueTextNode.this.getX();
				double parentY = DialogueTextNode.this.getY();
				double parentH = background.getHeight();
				
				double w = getWidth();
				double h = getHeight();
				
				setAbsolutePosition(parentX - w + connectorOffset, parentY + parentH/2 - h/2);
				
				super.render(context);
			}
		};
		getChildren().add(0, inConnector);
		
		DialogueNodeConnector outConnector = new DialogueNodeConnector(dialogue.getOutConnector(), ConnectorType.OUT) {
			@Override
			public void render(Context context) {
				double parentX = DialogueTextNode.this.getX();
				double parentY = DialogueTextNode.this.getY();
				double parentW = background.getWidth();
				double parentH = background.getHeight();
				
				double h = getHeight();
				
				setAbsolutePosition(parentX + parentW - connectorOffset, parentY + parentH/2 - h/2);
				
				super.render(context);
			}
		};
		getChildren().add(1, outConnector);
	}
	
	@Override
	protected void toggleEditing() {
		super.toggleEditing();
		textAreaPane.setMouseTransparent(!editing);
	}
}
