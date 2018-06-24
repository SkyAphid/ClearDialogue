package nokori.jdialogue.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueTextNode;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueTextNodePane extends DialogueNodePane{
	
	public DialogueTextNodePane(JDialogueCore core, DialogueTextNode node, DropShadow shadow, Font titleFont, Font textFont) {
		super(core, node, shadow, titleFont);
		
		//Out-Connector
		Arc connector = new Arc();
		connector.setFill(outConnectorColor);
		connector.setRadiusX(CONNECTOR_RADIUS);
		connector.setRadiusY(CONNECTOR_RADIUS);
		connector.setStartAngle(90);
		connector.setLength(-180);
		
		connector.setOnMouseClicked(event -> {
			connectorClicked(core, connector, node.getInConnector());
		});
		
		StackPane.setAlignment(connector, Pos.CENTER_RIGHT);
		StackPane.setMargin(connector, new Insets(0, -CONNECTOR_RADIUS, 0, 0));

		//Body Text Viewer
		Label label = new Label(node.getText());
		label.setMaxWidth(WIDTH - 20f);
		label.setMaxHeight(HEIGHT - TITLE_HEIGHT - 20f); 
		label.setFont(textFont);
		label.setTextFill(Color.BLACK);
		label.setWrapText(true);
		label.setMouseTransparent(true);
		
		StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
		StackPane.setMargin(label, new Insets(0, 10, 10, 10));
		
		getChildren().add(connector);
		getChildren().add(label);
	}
}
