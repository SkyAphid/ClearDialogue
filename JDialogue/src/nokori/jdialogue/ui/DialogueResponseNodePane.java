package nokori.jdialogue.ui;

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

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueResponseNodePane extends DialogueNodePane{
	
	public DialogueResponseNodePane(JDialogueCore core, DialogueResponseNode node, DropShadow shadow, Font titleFont, Font textFont, int incrementH) {
		super(core, node, shadow, titleFont);
		
		ArrayList<Response> responses = node.getResponses();
		
		//The ten is to give the bottom extra bounding space
		int extendedH = Math.max(TITLE_HEIGHT + 10 + (responses.size() * incrementH), HEIGHT);
		
		Group labelGroup = new Group();
		Group connectorGroup = new Group();
		
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
