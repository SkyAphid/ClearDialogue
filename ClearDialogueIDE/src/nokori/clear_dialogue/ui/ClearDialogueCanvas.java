package nokori.clear_dialogue.ui;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import nokori.clear.vg.widget.assembly.DraggableWidgetAssembly;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear.vg.widget.assembly.WidgetSynch;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueResponse;
import nokori.clear_dialogue.project.DialogueText;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.util.MultiEditUtils;
import nokori.clear_dialogue.ui.widget.HighlightWidget;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueResponseWidget;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueTextWidget;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget;

public class ClearDialogueCanvas extends DraggableWidgetAssembly {
	
	private SharedResources sharedResources;

	private ArrayList<DraggableDialogueWidget> highlightedNodes = new ArrayList<>();
	
	public ClearDialogueCanvas(SharedResources sharedResources) {
		this.sharedResources = sharedResources;
		
		WidgetSynch synch = new WidgetSynch(WidgetSynch.Mode.WITH_PARENT);
		synch.setSynchXEnabled(false);
		synch.setSynchYEnabled(false);
		addChild(synch);
		
		setOnMouseButtonEvent(e -> {
			if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && e.isPressed()) {
				HighlightWidget highlighter = new HighlightWidget(sharedResources, (float) e.getMouseX(), (float) e.getMouseY());
				addChild(highlighter);
			}
		});
		
		setOnKeyEvent(e -> {
			
			if (!e.isPressed()) {
				return;
			}
			
			if (!highlightedNodes.isEmpty()) {
				if (e.getKey() == GLFW.GLFW_KEY_T) {
					MultiEditUtils.addTagsToAll(sharedResources, highlightedNodes);
				}
				
				if (e.getKey() == GLFW.GLFW_KEY_R) {
					MultiEditUtils.removeTagsFromAll(sharedResources, highlightedNodes);
				}
				
				if (e.getKey() == GLFW.GLFW_KEY_N) {
					MultiEditUtils.multiTitle(sharedResources, highlightedNodes);
				}
			}
			
		});
	}
	
	@Override
	protected void move(float newX, float newY) {
		super.move(newX, newY);
		sharedResources.getProject().setViewportPosition(newX, newY);
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
				((DraggableDialogueWidget) w).requestRemoval(false);
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

		DialogueText dialogue = new DialogueText(project, "Dialogue " + project.getNumDialogue(), "", getNewDialogueX(), getNewDialogueY());
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
		
		DialogueResponse response = new DialogueResponse(project, "Response " + project.getNumDialogue(), "", getNewDialogueX(), getNewDialogueY());
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
	 * Highlighting tools
	 * 
	 * 
	 */
	
	/**
	 * Notify this canvas that the dialogue node is no longer highlighted
	 */
	public void notifyDialogueNodeUnhighlighted(DraggableDialogueWidget node) {
		highlightedNodes.remove(node);
	}
	
	public void notifyDialogueNodeHighlighted(DraggableDialogueWidget node) {
		if (!highlightedNodes.contains(node)) {
			highlightedNodes.add(node);
		}
		
		//This will change the context hint to include controls for multiple highlighted nodes
		sharedResources.resetContextHint();
	}
	
	/**
	 * Checks whether or not the given node can unhighlight itself via the mouse leaving its bounding. 
	 * This is only true if the given dialogue node is the only currently highlighted node. The purpose of this function is to prevent nodes from unhighlighting themselves after 
	 * the multi-select is used.
	 */
	public boolean canMouseMotionUnhighlightDialogueNode(DraggableDialogueWidget node) {
		return (highlightedNodes.contains(node) && highlightedNodes.size() == 1);
	}
	
	public int getNumHighlightedNodes() {
		return highlightedNodes.size();
	}
	
	/*
	 * 
	 * 
	 * Utilities
	 * 
	 * 
	 */
	
	public void removeDialogueNode(DraggableDialogueWidget widget) {
		if (widget.isDataFlaggedForDeletion()) {
			sharedResources.getProject().removeDialogue(widget.getDialogue());
		}
		
		removeChild(widget);
	}
	
	private float getNewDialogueX() {
		return parent.getWidth()/2 - DraggableDialogueWidget.DEFAULT_MODE.getWidth()/2;
	}
	
	private float getNewDialogueY() {
		return parent.getHeight()/2 - DraggableDialogueWidget.DEFAULT_MODE.getHeight()/2;
	}
}
