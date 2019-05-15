package nokori.clear_dialogue.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.apps.ClearInputApp;
import nokori.clear.vg.util.NanoVGScaler;
import nokori.clear.windows.GLFWException;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.ui.ClearDialogueCanvas;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget.Mode;

public class MultiEditUtils {
	
	private static final int INPUT_WIDTH = 300;
	private static final int INPUT_HEIGHT = 200;
	
	private static final File FONT_LOCATION = new File("res/fonts/NotoSans/");
	private static final ClearColor BUTTON_OUTLINE_FILL = ClearColor.CORAL;
	
	public static void deleteAll(SharedResources sharedResources, ArrayList<DraggableDialogueWidget> nodes) {
		if (TinyFileDialog.showConfirmDialog("Delete All", "Are you sure you want to delete " + nodes.size() + " nodes?", 
				TinyFileDialog.InputType.YES_NO, TinyFileDialog.Icon.QUESTION, false)) {
			
			for (int i = 0; i < nodes.size(); i++) {
				nodes.get(i).requestRemoval(true);
			}
			
		}
	}
	
	public static void addTagsToAll(SharedResources sharedResources, ArrayList<DraggableDialogueWidget> nodes) {
		
		String title = "Multi-Tag (Insertion)";
		
		ClearInputApp input = new ClearInputApp(sharedResources.getIDECore(), INPUT_WIDTH, INPUT_HEIGHT, BUTTON_OUTLINE_FILL, FONT_LOCATION, 
				title,
				"Input a tag to insert into all of the selected DialogueNodes.\n\n*Note: Spaces must be added manually.",
				"New Tag") {

			@Override
			protected void confirmButtonPressed(String tags) {
				if (tags != null) {
					int additions = 0;
					
					for (int i = 0; i < nodes.size(); i++) {
						DraggableDialogueWidget w = nodes.get(i);
						Dialogue d = w.getDialogue();
						d.setTags(d.getTags() + tags);
						additions++;
					}
					
					TinyFileDialog.showMessageDialog(title, tags + " was inserted successfully into " + additions + " nodes.", TinyFileDialog.Icon.INFORMATION);
					
					sharedResources.refreshCanvas();
				} else {
					TinyFileDialog.showMessageDialog(title, "No tag was inputted.", TinyFileDialog.Icon.ERROR);
				}
			}
		};
		
		try {
			input.show();
		} catch (GLFWException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeTagsFromAll(SharedResources sharedResources, ArrayList<DraggableDialogueWidget> nodes) {
		String title = "Multi-Tag (Removal)";
		
		ClearInputApp input = new ClearInputApp(sharedResources.getIDECore(), INPUT_WIDTH, INPUT_HEIGHT, BUTTON_OUTLINE_FILL, FONT_LOCATION, 
				title,
				"Input a tag to remove from all of the selected DialogueNodes.\n\n*Note: Spaces must be added manually.",
				"") {

			@Override
			protected void confirmButtonPressed(String tags) {
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
						TinyFileDialog.showMessageDialog(title, "\"" + tags + "\" was removed successfully from " + removals + " nodes.", TinyFileDialog.Icon.INFORMATION);
						sharedResources.refreshCanvas();
					} else {
						TinyFileDialog.showMessageDialog(title, "\"" + tags + "\" wasn't found in any nodes.", TinyFileDialog.Icon.INFORMATION);
					}

				} else {
					TinyFileDialog.showMessageDialog(title, "No tag was inputted.", TinyFileDialog.Icon.ERROR);
				}
			}
		};
		
		try {
			input.show();
		} catch (GLFWException e) {
			e.printStackTrace();
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
		
		String windowTitle = "Multi-Title";
		String autoNumTag = "[[#NUM]]";
		
		ClearInputApp input = new ClearInputApp(sharedResources.getIDECore(), INPUT_WIDTH + 100, INPUT_HEIGHT + 50, BUTTON_OUTLINE_FILL, FONT_LOCATION, 
				windowTitle,
				"Input a title for the selected dialogue nodes."
						+ "\n\n*Note: You can add " + autoNumTag + " to the name to automatically add the node's number to the name."
						+ "\n\nFor example, Node " + autoNumTag + " would result in Node 1, Node 2, so on.",
				"New Title [[#NUM]]") {

			@Override
			protected void confirmButtonPressed(String title) {
				int numNames = 0;
						
				for (int i = 0; i < nodes.size(); i++) {
					DraggableDialogueWidget w = nodes.get(i);
					Dialogue d = w.getDialogue();
						
					d.setTitle(title.replace(autoNumTag, Integer.toString(numNames + 1)));
					numNames++;
				}
					
				TinyFileDialog.showMessageDialog(windowTitle, "\"" + title + "\" was set on " + numNames + " nodes.", TinyFileDialog.Icon.INFORMATION);
					
				sharedResources.refreshCanvas();
			}
			
		};
		
		try {
			input.show();
		} catch (GLFWException e) {
			e.printStackTrace();
		}
	}

	public static void autoSnap(float mouseX, float mouseY, ClearDialogueCanvas canvas, ArrayList<DraggableDialogueWidget> nodes) {

		/*
		 * Sort by title. First: sort alphabetically. Second: check if the title has a number in it, and sort by number if applicable.
		 */
		
		Collections.sort(nodes, new Comparator<DraggableDialogueWidget>() {
			@Override
			public int compare(DraggableDialogueWidget w1, DraggableDialogueWidget w2) {
				String title1 = w1.getDialogue().getTitle();
				String title2 = w2.getDialogue().getTitle();
				
				String[] split1 = title1.split(" ");
				String[] split2 = title2.split(" ");
				
				//Matching names with different number labels
				if (split1.length == split2.length) {
					int length = split1.length;
					int matchingIndices = 0;
					
					for (int i = 0; i < length; i++) {
						if (split1[i].contentEquals(split2[i])) {
							matchingIndices++;
						}
					}
					
					//System.err.println(split1[0] + " " + matchingIndices + " " + length);
					
					//Only one differing section (likely the numerical value)
					if (matchingIndices == length-1) {
						for (int i = 0; i < length; i++) {
							try {
								int num1 = Integer.parseInt(split1[i]);
								int num2 = Integer.parseInt(split2[i]);
								
								return Integer.compare(num1, num2);
								
							} catch (NumberFormatException e) {}
						}
					}
				}
				
				//Names don't match, sort alphabetically
				return w1.getDialogue().getTitle().compareTo(w2.getDialogue().getTitle());
			}
		});

		/*
		 * Set all nodes to the same mode
		 */
		
		DraggableDialogueWidget leaderNode = nodes.get(0);
		Mode mode = leaderNode.getMode();
		
		for (DraggableDialogueWidget w : nodes) {
			w.transitionMode(mode);
		}
		
		/*
		 * Auto-Snap
		 */
		
		NanoVGScaler scaler = canvas.getScaler();
		
		float x = leaderNode.snapValue(-canvas.getX() + mouseX);
		float y = leaderNode.snapValue(-canvas.getY() + mouseY);
		float width = scaler.applyScale(canvas.getWidth());
		
		float increment = (Math.max(mode.getWidth(), mode.getHeight()) * DraggableDialogueWidget.SNAP_SIZE_MULTIPLIER);
		int elementsPerRow = (int) (width / increment);
		
		for (int i = 0; i < nodes.size(); i++) {
			DraggableDialogueWidget w = nodes.get(i);
			
			float snapX = x + (increment * (i % elementsPerRow));
			float snapY = y + (increment * (i / elementsPerRow));
			
			w.setGridSnappingEnabled(true);
			w.move(snapX, snapY);
			w.setGridSnappingEnabled(false);
		}
	}
}
