package nokori.jdialogue.ui.dialogue_nodes;

import lwjgui.Color;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.control.text_input.TextArea;
import lwjgui.scene.control.text_input.TextInputScrollPane;
import lwjgui.scene.layout.floating.FloatingPane;
import nokori.jdialogue.project.Dialogue;
import nokori.jdialogue.project.DialogueConnector;
import nokori.jdialogue.project.DialogueText;
import nokori.jdialogue.ui.SharedResources;
import nokori.jdialogue.ui.dialogue_nodes.DialogueConnectorNode.ConnectorType;

public class DialogueTextNode extends DialogueNode {

	protected FloatingPane textAreaPane;
	protected TextArea textArea;
	
	private DialogueConnectorNode outConnector = null;
	
	public DialogueTextNode(SharedResources sharedResources, Dialogue dialogue) {
		super(sharedResources, dialogue);
		
		textAreaPane = new FloatingPane();
		textAreaPane.setAbsolutePosition(LEFT_PADDING, TOP_PADDING + TEXT_AREA_TOP_PADDING);
		textAreaPane.setAlignment(Pos.TOP_LEFT);
		
		//The constructor is generic so that it can be easily extended by similar DialogueNodes
		if (dialogue instanceof DialogueText) {
			DialogueText t = (DialogueText) dialogue;
			
			/*
			 * 
			 * Text Area
			 * 
			 */
			
			textArea = new TextArea(t.getText()) {
				@Override
				public void render(Context context) {
					positionTextArea(this);
					super.render(context);
				}
			};
			
			textArea.setOnDeselected(e -> {
				System.err.println("Deselected");
				t.setText(textArea.getText());
			});

			styleTextArea(textArea);
			textArea.setWordWrap(true);
			
			//Finalize text area
			textAreaPane.getChildren().add(textArea);
			getChildren().add(textAreaPane);
			
			/*
			 * 
			 * Out Connector
			 * 
			 */
			
			outConnector = new DialogueConnectorNode(this, t.getOutConnector(), ConnectorType.OUT);
			getChildren().add(0, outConnector);
		}
	}
	
	@Override
	protected void setEditing(boolean editing) {
		super.setEditing(editing);
		textArea.getInternalScrollPane().setVisible(editing);
		textArea.setMouseTransparent(!editing);
	}

	@Override
	public DialogueConnectorNode getDialogueNodeConnectorOf(DialogueConnector connector) {
		if (outConnector != null && outConnector.getConnector() == connector) {
			return outConnector;
		}
		
		return super.getDialogueNodeConnectorOf(connector);
	}
	
	/*
	 * Scrollbar visibility
	 */
	
	@Override
	public void setExpanded(boolean expanded) {
		super.setExpanded(expanded);
		
		//Set visibility to false as soon as the node starts to resize so that we don't get weird artifacts as it contracts
		if (!expanded) {
			textArea.getInternalScrollPane().setVisible(expanded);
		}
	}
	
	@Override
	protected void resizeCompleteCallback(boolean expanded) {
		//Only set it to visible AFTER its expanded to prevent graphical artifacts
		textArea.getInternalScrollPane().setVisible(expanded);
	}
	
	
	/*
	 * 
	 * TextArea Management
	 * 
	 */
	
	/**
	 * Call this from position() of the TextArea to keep its position in sync with its parent.
	 */
	protected void positionTextArea(TextArea textArea) {
		int edgePadding = 25;
		
		textArea.setMaxWidth(background.getWidth() - (edgePadding - 5));
		textArea.setPrefWidth(getMaxWidth());
		
		textArea.setMaxHeight(background.getHeight() - TEXT_AREA_TOP_PADDING - edgePadding);
		textArea.setPrefHeight(getMaxHeight());
	}
	
	/**
	 * Styles a given TextArea so that every DialogueNode will have a consistent look and feel.
	 */
	protected void styleTextArea(TextArea textArea) {
		textArea.setMouseTransparent(!editing);
		
		//Font
		textArea.setFont(sharedResources.getTheme().getSerifFont());
		textArea.setFontSize(18);

		//Background/Decoration
		textArea.setBackground(null);
		textArea.setDecorated(false);
		textArea.setSelectionOutlineEnabled(false);
		
		//Scrollbar customization
		TextInputScrollPane s = textArea.getInternalScrollPane();
		s.setControlFill(Color.TRANSPARENT);
		s.setControlOutlineFill(Color.GRAY);
		s.setSelectionFill(Color.TRANSPARENT);
		s.setSelectionPassiveFill(Color.TRANSPARENT);
		s.setVisible(expanded);
	}
}
