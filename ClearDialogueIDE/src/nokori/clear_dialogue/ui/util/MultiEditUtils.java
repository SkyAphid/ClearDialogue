package nokori.clear_dialogue.ui.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget;

public class MultiEditUtils {
	public static void addTagsToAll(SharedResources sharedResources, ArrayList<DraggableDialogueWidget> nodes) {
		String tags = TinyFileDialog.showInputDialog("Multi-Tag", "Input a tag to insert into all of the selected DialogueNodes.", "");
		
		if (tags != null) {
			int additions = 0;
			
			for (int i = 0; i < nodes.size(); i++) {
				DraggableDialogueWidget w = nodes.get(i);
				Dialogue d = w.getDialogue();
				d.setTags(d.getTags() + tags);
				additions++;
			}
			
			TinyFileDialog.showMessageDialog("Multi-Tag", tags + " was inserted successfully into " + additions + " nodes.", TinyFileDialog.Icon.INFORMATION);
			
			sharedResources.refreshCanvas();
		} else {
			TinyFileDialog.showMessageDialog("Multi-Tag", "No tag was inputted.", TinyFileDialog.Icon.ERROR);
		}
	}
	
	public static void removeTagsFromAll(SharedResources sharedResources, ArrayList<DraggableDialogueWidget> nodes) {
		String tags = TinyFileDialog.showInputDialog("Multi-Tag Removal", "Input a tag to remove from all of the selected DialogueNodes.", "");
		
		if (tags != null) {
			int removals = 0;
			
			for (int i = 0; i < nodes.size(); i++) {
				DraggableDialogueWidget w = nodes.get(i);
				Dialogue d = w.getDialogue();
				
				if (d.getTags().contains(tags)) {
					d.setTags(d.getTags().replace(tags, ""));
					removals++;
				}
			}
			
			if (removals > 0) {
				TinyFileDialog.showMessageDialog("Multi-Tag", "\"" + tags + "\" was removed successfully from " + removals + " nodes.", TinyFileDialog.Icon.INFORMATION);
				sharedResources.refreshCanvas();
			} else {
				TinyFileDialog.showMessageDialog("Multi-Tag", "\"" + tags + "\" wasn't found in any nodes.", TinyFileDialog.Icon.INFORMATION);
			}

		} else {
			TinyFileDialog.showMessageDialog("Multi-Tag", "No tag was inputted.", TinyFileDialog.Icon.ERROR);
		}
	}
	
	public static void multiTitle(SharedResources sharedResources, ArrayList<DraggableDialogueWidget> nodes) {
		/*
		 * Sort the list based on X/Y coordinates so that if the #NUM tag is used, the numbers appear in descending order
		 */
		
		Collections.sort(nodes, new Comparator<DraggableDialogueWidget>() {
			public int compare(DraggableDialogueWidget w1, DraggableDialogueWidget w2) {
				Dialogue n1 = w1.getDialogue();
				Dialogue n2 = w2.getDialogue();

				int result = Float.compare(n1.getY(), n2.getY());
				
				if (result == 0) {
					result = Float.compare(n1.getX(), n2.getX());
				}
				
				return result;
			}
		});

		/*
		 * Begin renaming process
		 */
		
		String autoNumTag = "[[#NUM]]";
		
		String title = TinyFileDialog.showInputDialog("Multi-Title", 
				
				"Input a title for the selected dialogue nodes. "
				+ "You can add " + autoNumTag + " to the name to automatically add the node's number to the name. "
				+ "For example, Node " + autoNumTag + " would result in Node 1, Node 2, so on.", 
				
				"");
		
		if (title != null) {
			int numNames = 0;
				
			for (int i = 0; i < nodes.size(); i++) {
				DraggableDialogueWidget w = nodes.get(i);
				Dialogue d = w.getDialogue();
				
				d.setTitle(title.replace(autoNumTag, Integer.toString(numNames + 1)));
				numNames++;
			}
			
			TinyFileDialog.showMessageDialog("Multi-Title", "\"" + title + "\" was set on " + numNames + " nodes.", TinyFileDialog.Icon.INFORMATION);
			
			sharedResources.refreshCanvas();
		} else {
			TinyFileDialog.showMessageDialog("Multi-Title", "No name was inputted.", TinyFileDialog.Icon.ERROR);
		}
	}
}
