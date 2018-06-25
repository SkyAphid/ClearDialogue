package nokori.jdialogue.ui.node;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueResponseNode.Response;
import nokori.jdialogue.ui.editor.DialogueResponseNodeEditor;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueResponseNodePane extends DialogueNodePane{
	
	private Group labelGroup = null;
	private Group connectorGroup = null;
	
	private Font textFont;
	private int incrementH;
	
	public DialogueResponseNodePane(JDialogueCore core, DialogueResponseNode node, DropShadow shadow, Font titleFont, Font textFont, int incrementH) {
		super(core, node, shadow, titleFont);
		
		this.textFont = textFont;
		this.incrementH = incrementH;
		
		//Open Editor
		setOnMouseClicked(event -> {
			if (checkDispose(event, core)) {
				return;
			}
			
			if (event.getClickCount() > 1) {
				core.getUIPane().getChildren().add(new DialogueResponseNodeEditor(core, node, this, titleFont, textFont));
			}
		});
		
		refresh(core);
	}
	
	@Override
	public void refresh(JDialogueCore core) {
		super.refresh(core);
		
		//Remove old connectors and labels to replace with new ones
		if (labelGroup != null) {
			getChildren().remove(labelGroup);
		}
		
		if (connectorGroup != null) {
			getChildren().remove(connectorGroup);
		}
		
		labelGroup = new Group();
		connectorGroup = new Group();
		
		ArrayList<Response> responses = ((DialogueResponseNode) node).getResponses();
		
		//The ten is to give the bottom extra bounding space
		int extendedH = Math.max(TITLE_HEIGHT + 10 + (responses.size() * incrementH), HEIGHT);
		
		for (int i = 0; i < responses.size(); i++) {
			Response response = responses.get(i);
			
			int y = (i * incrementH);
			
			/*
			 * Node Connector
			 */
			
			int connectorRadius = (int) (incrementH * (1.0/3.0));
			
			Arc connector = new Arc();
			connector.setFill(outConnectorColor);
			connector.setRadiusX(connectorRadius);
			connector.setRadiusY(connectorRadius);
			connector.setStartAngle(90);
			connector.setLength(-180);
			connector.setLayoutY(y);
			
			connector.setOnMouseClicked(event -> {
				connectorClicked(event, core, connector, response.getOutConnector());
			});
			
			connector.setOnMouseEntered(event -> {
				connectorHighlightTransition(core.getScene(), connector, outConnectorColor, true);
			});
			
			connector.setOnMouseExited(event -> {
				connectorHighlightTransition(core.getScene(), connector, outConnectorColor, false);
			});
			
			connectorGroup.getChildren().add(connector);
			
			/*
			 * Label
			 */
			
			Label label = new Label(response.getText());
			label.setFont(textFont);
			label.setMaxWidth(WIDTH - 20f);
			label.setTextFill(Color.BLACK);
			label.setMouseTransparent(true);
			
			label.setLayoutY(y);
			
			labelGroup.getChildren().add(label);
		}
		
		labelGroup.setTranslateY(TITLE_HEIGHT/2);
		
		connectorGroup.setTranslateX(WIDTH/2 + 5);
		connectorGroup.setTranslateY(TITLE_HEIGHT/2);
		
		getChildren().add(labelGroup);
		getChildren().add(connectorGroup);
		
		setBackgroundHeight(extendedH);
	}
}
