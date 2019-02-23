package nokori.jdialogue.ui.dialogue_nodes;

import java.util.ArrayList;

import lwjgui.scene.Context;
import lwjgui.scene.control.text_input.CodeArea;
import nokori.jdialogue.project.DialogueConnector;
import nokori.jdialogue.project.DialogueResponse;
import nokori.jdialogue.project.DialogueResponse.Response;
import nokori.jdialogue.ui.SharedResources;
import nokori.jdialogue.ui.dialogue_nodes.DialogueConnectorNode.ConnectorType;

public class DialogueResponseNode extends DialogueTextNode {

	private ArrayList<DialogueConnectorNode> outConnectors = new ArrayList<DialogueConnectorNode>();
	
	public DialogueResponseNode(SharedResources sharedResources, DialogueResponse dialogue) {
		super(sharedResources, dialogue);

		/*
		 * 
		 * Code Area (line numbers mark response numbers)
		 * 
		 */

		textArea = new CodeArea(convertResponsesToString(dialogue)) {
			@Override
			public void render(Context context) {
				positionTextArea(this);
				super.render(context);
			}
		};
		
		textArea.setOnDeselected(e -> {
			System.err.println("deselect");
			refreshDialogueResponse(dialogue, textArea.getText());
		});

		styleTextArea(textArea);
		textArea.setWordWrap(false);

		textAreaPane.getChildren().add(textArea);
		getChildren().add(textAreaPane);
		
		/*
		 * 
		 * Out connectors
		 * 
		 */
		
		refreshOutConnectors();
		
	}
	
	/**
	 * Refreshes the Out Connector components after editing is completed.
	 */
	private void refreshOutConnectors() {
		getChildren().removeAll(outConnectors.toArray(new DialogueConnectorNode[outConnectors.size()])); 
		outConnectors.clear();
		
		//Begin building the out connectors
		DialogueResponse d = (DialogueResponse) dialogue;
		
		for (int i = 0; i < d.getResponses().size(); i++) {
			int index = i;
			Response r = d.getResponses().get(index);
			
			DialogueConnectorNode outConnector = new DialogueConnectorNode(this, r.getOutConnector(), ConnectorType.OUT, DialogueConnectorNode.CONNECTOR_RADIUS/2) {
				@Override
				protected void syncPosition() {
					double x = DialogueResponseNode.this.getOutConnectorX();
					double y = (DialogueResponseNode.this.getY() + 50 + ((circle.getHeight() * 1.1f) * index));
					
					setAbsolutePosition(x, y);
				}
			};
			
			outConnectors.add(outConnector);
			getChildren().add(outConnector);
		}
	}
	
	/**
	 * Converts all of the Response objects in the DialogueResponse object into strings we can present in the CodeArea.
	 */
	private String convertResponsesToString(DialogueResponse response) {
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < response.getResponses().size(); i++) {
			s.append("\n").append(response.getResponses().get(i).getText());
		}
		
		return s.toString();
	}

	/**
	 * Updates the DialogueResponse with the input text from the CodeArea.
	 */
	private void refreshDialogueResponse(DialogueResponse dialogue, String text) {
		//Make a copy of the DialogueResponse's responses and clear the actual one (so we can refill it back up)
		ArrayList<Response> oldResponses = new ArrayList<Response>(dialogue.getResponses());
		dialogue.getResponses().clear();
		
		//Split the text with line separators (each new line signifies a new response)
		String[] stringResponses = text.split(System.lineSeparator());
		
		//Begin going through the responses one by one
		for (int i = 0; i < stringResponses.length; i++) {
			String response = stringResponses[i];
			
			System.err.println("Response " + i + ": " + response);
			
			//Only check response if it's not empty
			if (!response.trim().isEmpty()) {
				
				if (i < oldResponses.size()) {
					
					//If the index is within the old responses range, update the old one so that the connection doesn't have to be re-connected
					Response oldResponse = oldResponses.get(i);
					oldResponse.setText(response);
					dialogue.getResponses().add(oldResponse);
					
				} else {
					
					//If the index is out of the range of the old responses, just add a new connection-less response
					dialogue.addResponse(response);
				}
			}
		}
		
		//As a final step, remove all of the responses that were re-added back onto the node, leaving the ones to be deleted. Then disconnect all of their out connectors.
		//This effectively disposes them and leaves them for the Java garbage collector to delete.
		oldResponses.removeAll(dialogue.getResponses());
		
		for (int i = 0; i < oldResponses.size(); i++) {
			oldResponses.get(i).getOutConnector().disconnectAll();
		}
		
		//Refresh the out connectors
		refreshOutConnectors();
	}
	
	@Override
	public DialogueConnectorNode getDialogueNodeConnectorOf(DialogueConnector connector) {
		
		for (int i = 0; i < outConnectors.size(); i++) {
			if (outConnectors.get(i).getConnector() == connector) {
				return outConnectors.get(i);
			}
		}
		
		return super.getDialogueNodeConnectorOf(connector);
	}

}
