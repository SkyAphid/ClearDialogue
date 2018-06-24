package nokori.jdialogue.ui;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueResponseNode;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueResponseNodePane extends DialogueNodePane{
	
	public DialogueResponseNodePane(JDialogueCore core, DialogueResponseNode node, DropShadow shadow, Font titleFont, Font textFont, int incrementH) {
		super(core, node, shadow, titleFont);
		
		ArrayList<String> responses = node.getResponses();
		
		int extendedH = Math.max((responses.size() * incrementH), HEIGHT);
		
		Group group = new Group();
		
		for (int i = 0; i < responses.size(); i++) {
			String response = responses.get(i);
			
			Label label = new Label(response);
			label.setFont(textFont);
			label.setMaxWidth(WIDTH - 20f);
			label.setTextFill(Color.BLACK);
			label.setMouseTransparent(true);
			
			label.setLayoutY((i * incrementH));
			
			StackPane.setMargin(label, new Insets(0, 10, 10, 10));
			
			group.getChildren().add(label);
		}
		
		getChildren().add(group);
		
		setHeight(extendedH);
	}
}
