package nokori.jdialogue.ui.util;

import java.util.Stack;

import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.ui.node.DialogueNodePane;

/**
 * This class contains the controls for when multiple DialogueNodes are selected. It allows you to add or remove multiple tags from each of the nodes at once.
 * @author Brayden
 *
 */
public class MultiTagTool {
	
	public static EventHandler<KeyEvent> getKeyPressEventHandler(JDialogueCore core) {
		return new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				Stage stage = core.getStage();
				
				/*
				 * Add tag to all selected nodes
				 */
				if (event.getCode() == KeyCode.T) {
					Stack<DialogueNodePane> selected = core.getAllMultiSelected();
					
					if (!selected.isEmpty()) {
						String newTag = UIUtil.showInputDialog(stage, "Multi-Tag Insertion", "Input a tag to insert into all of the selected DialogueNodes."
								+ "\n\n*Note: spaces must be added manually", "New Tag", "Please input the new tag:");
						
						int insertions = 0;
							
						while (newTag != null && !selected.isEmpty()) {
							DialogueNode node = selected.pop().getDialogueNode();
							node.setTag(node.getTag() + newTag);
							insertions++;
						}
						
						UIUtil.showAlert(stage, AlertType.INFORMATION, "Tag Insertion Success", "\"" + newTag + "\" was inserted successfully into " + insertions + " nodes.");
					}
				}
				
				/*
				 * Remove tag from all selected nodes
				 */
				if (event.getCode() == KeyCode.R) {
					Stack<DialogueNodePane> selected = core.getAllMultiSelected();
					
					if (!selected.isEmpty()) {
						String searchTag = UIUtil.showInputDialog(stage, "Multi-Tag Removal", 
								"Input a tag to remove from all of the selected DialogueNodes.", "", "Please input the tag to remove:");
						
						int removals = 0;
							
						while (searchTag != null && !selected.isEmpty()) {
							DialogueNode node = selected.pop().getDialogueNode();
							
							if (node.getTag().contains(searchTag)) {
								node.setTag(node.getTag().replace(searchTag, ""));
								removals++;
							}
						}
						
						if (removals > 0) {
							UIUtil.showAlert(stage, AlertType.INFORMATION, "Tag Removal Success", "\"" + searchTag + "\" was removed successfully from " + removals + " nodes.");
						} else {
							UIUtil.showAlert(stage, AlertType.ERROR, "Tag Removal Failure", "\"" + searchTag + "\" wasn't found in any nodes.");
						}
					}
				}
			}
		};
	}
}
