package nokori.jdialogue.ui.util;

import java.util.Collections;
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
public class MultiEditTool {
	
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
						String newTag = JDialogueUtils.showInputDialog(stage, "Multi-Tag Insertion", "Input a tag to insert into all of the selected DialogueNodes."
								+ "\n\n*Note: spaces must be added manually", "New Tag", "Please input the new tag:");
						
						if (newTag != null) {
							int insertions = 0;
								
							while (newTag != null && !selected.isEmpty()) {
								DialogueNode node = selected.pop().getDialogueNode();
								node.setTag(node.getTag() + newTag);
								insertions++;
							}
							
							JDialogueUtils.showAlert(stage, AlertType.INFORMATION, "Tag Insertion Success", "\"" + newTag + "\" was inserted successfully into " + insertions + " nodes.");
						} else {
							JDialogueUtils.showAlert(stage, AlertType.INFORMATION, "Tag Insertion Cancelled", "No tag was inputted.");
						}
					}
				}
				
				/*
				 * Remove tag from all selected nodes
				 */
				if (event.getCode() == KeyCode.R) {
					Stack<DialogueNodePane> selected = core.getAllMultiSelected();
					
					if (!selected.isEmpty()) {
						String searchTag = JDialogueUtils.showInputDialog(stage, "Multi-Tag Removal", 
								"Input a tag to remove from all of the selected DialogueNodes.", "", "Please input the tag to remove:");
						
						if (searchTag != null) {
							int removals = 0;
								
							while (searchTag != null && !selected.isEmpty()) {
								DialogueNode node = selected.pop().getDialogueNode();
								
								if (node.getTag().contains(searchTag)) {
									node.setTag(node.getTag().replace(searchTag, ""));
									removals++;
								}
							}
							
							if (removals > 0) {
								JDialogueUtils.showAlert(stage, AlertType.INFORMATION, "Tag Removal Success", "\"" + searchTag + "\" was removed successfully from " + removals + " nodes.");
							} else {
								JDialogueUtils.showAlert(stage, AlertType.ERROR, "Tag Removal Failure", "\"" + searchTag + "\" wasn't found in any nodes.");
							}
						} else {
							JDialogueUtils.showAlert(stage, AlertType.INFORMATION, "Removal Cancelled", "No name was inputted.");
						}
					}
				}
				
				/*
				 * Set name for all nodes
				 */
				if (event.getCode() == KeyCode.N) {
					Stack<DialogueNodePane> selected = core.getAllMultiSelected();
					
					if (!selected.isEmpty()) {
						Collections.reverse(selected);
						String autoNumTag = "[[#NUM]]";
						
						String name = JDialogueUtils.showInputDialog(stage, "Multi-Name", 
								"Input a name for the selected DialogueNodes."
								+ "\n\n*Note: you can add " +autoNumTag + " to the name to automatically add the node's number to the name."
								+ "\n*e.g. Node " + autoNumTag + " would be Node 1, Node 2, etc", 
								
								"", "Please input the new node names:");
						
						if (name != null) {
							int numNames = 0;
								
							while (!selected.isEmpty()) {
								DialogueNode node = selected.pop().getDialogueNode();
								node.setName(name.replace(autoNumTag, Integer.toString(numNames + 1)));
								numNames++;
							}
							
							JDialogueUtils.showAlert(stage, AlertType.INFORMATION, "Rename Success", "\"" + name + "\" was set on " + numNames + " nodes.");
							
							core.refreshUI(true);
						} else {
							JDialogueUtils.showAlert(stage, AlertType.ERROR, "Rename Cancelled", "No name was inputted.");
						}
					}
				}
			}
		};
	}
}
