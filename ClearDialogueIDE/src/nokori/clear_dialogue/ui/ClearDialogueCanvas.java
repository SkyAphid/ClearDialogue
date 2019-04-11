package nokori.clear_dialogue.ui;

import nokori.clear.vg.widget.assembly.DraggableWidgetAssembly;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear.vg.widget.assembly.WidgetSynch;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueResponse;
import nokori.clear_dialogue.project.DialogueText;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueResponseWidget;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueTextWidget;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget;

public class ClearDialogueCanvas extends DraggableWidgetAssembly {
	
	private SharedResources sharedResources;
	
	public ClearDialogueCanvas(SharedResources sharedResources) {
		this.sharedResources = sharedResources;
		
		WidgetSynch synch = new WidgetSynch(WidgetSynch.Mode.WITH_PARENT);
		synch.setSynchXEnabled(false);
		synch.setSynchYEnabled(false);
		addChild(synch);
	}
	
	/**
	 * This function will refresh the canvas and synchronize it with the given Project.
	 */
	public void refresh(Project project) {
		/*
		 * Remove existing children
		 */
		for (int i = 0; i < getNumChildren(); i++) {
			Widget w = getChild(i);
			
			if (w instanceof DraggableDialogueWidget) {
				((DraggableDialogueWidget) w).requestRemoval();
			} else {
				removeChild(i);
				i--;
			}
		}
		
		/*
		 * Add children from given project
		 */
		for (int i = 0; i < project.getNumDialogue(); i++) {
			Dialogue d = project.getDialogue(i);
			
			//Add widget for DialogueText
			if (d instanceof DialogueText) {
				addDialogueTextNode((DialogueText) d);
			}
			
			//Add widget for DialogueResponse
			if (d instanceof DialogueResponse) {
				addDialogueResponseNode((DialogueResponse) d);
			}
		}
		
		/*
		 * Synchronize the viewport with the project
		 */
		
		setPosition(project.getViewportX(), project.getViewportY());
	}
	
	/*
	 * 
	 * 
	 * Dialogue Text
	 * 
	 * 
	 */
	
	/**
	 * This function will create a centered dialogue text node with default settings.
	 */
	public void addDialogueTextNode() {
		Project project = sharedResources.getProject();

		DialogueText dialogue = new DialogueText(project, "New Dialogue " + project.getNumDialogue(), "", getNewDialogueX(), getNewDialogueY());
		project.addDialogue(dialogue);
		
		addDialogueTextNode(dialogue);
	}
	
	/**
	 * This function will create a DraggableDialogueTextWidget for the given DialogueText data.
	 * 
	 * @param dialogue - the dialogue data to be used for the widget
	 */
	public void addDialogueTextNode(DialogueText dialogue) {
		DraggableDialogueTextWidget widget = new DraggableDialogueTextWidget(sharedResources, dialogue);
		addChild(widget);
	}
	
	/*
	 * 
	 * 
	 * Response text
	 * 
	 * 
	 */
	
	/**
	 * This function will create a centered dialogue response node with default settings.
	 */
	
	public void addDialogueResponseNode() {
		Project project = sharedResources.getProject();
		
		DialogueResponse response = new DialogueResponse(project, "New Response " + project.getNumDialogue(), "", getNewDialogueX(), getNewDialogueY());
		project.addDialogue(response);
		
		addDialogueResponseNode(response);
	}
	
	/**
	 * This function will create a DraggableDialogueResponseWidget for the given DialogueResponse data.
	 * 
	 * @param response - the response data to be used for the widget
	 */
	
	public void addDialogueResponseNode(DialogueResponse response) {
		DraggableDialogueResponseWidget widget = new DraggableDialogueResponseWidget(sharedResources, response);
		addChild(widget);
	}
	
	/*
	 * 
	 * 
	 * Utilities
	 * 
	 * 
	 */
	
	public void deleteDialogueNode(DraggableDialogueWidget widget) {
		sharedResources.getProject().removeDialogue(widget.getDialogue());
		removeChild(widget);
	}
	
	private float getNewDialogueX() {
		return parent.getWidth()/2 - DraggableDialogueWidget.DEFAULT_MODE.getWidth()/2;
	}
	
	private float getNewDialogueY() {
		return parent.getHeight()/2 - DraggableDialogueWidget.DEFAULT_MODE.getHeight()/2;
	}
}
