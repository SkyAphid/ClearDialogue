package nokori.clear_dialogue.ui;

import java.util.ArrayList;
import java.util.Comparator;

import org.lwjgl.glfw.GLFW;

import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.transition.Transition;
import nokori.clear.vg.util.NanoVGScaler;
import nokori.clear.vg.widget.assembly.DraggableWidgetAssembly;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetSynch;
import nokori.clear.vg.widget.assembly.WidgetUtils;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueResponse;
import nokori.clear_dialogue.project.DialogueText;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.util.MultiEditUtils;
import nokori.clear_dialogue.ui.widget.HighlightWidget;
import nokori.clear_dialogue.ui.widget.node.ConnectionRendererWidget;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueResponseWidget;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueTextWidget;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget;

public class ClearDialogueCanvas extends DraggableWidgetAssembly {
	
	private SharedResources sharedResources;

	private ArrayList<DraggableDialogueWidget> highlightedNodes = new ArrayList<>();
	
	public ClearDialogueCanvas(SharedResources sharedResources) {
		this.sharedResources = sharedResources;
		
		setScaler(sharedResources.getScaler());
		
		//Settings unique to this canvas. Check the function descriptions for more information.
		setRequiresMouseToBeWithinWidgetToDrag(false);
		setInvertInputOrder(true);
		
		//Synchronize the canvas size with the window size
		WidgetSynch synch = new WidgetSynch(WidgetSynch.Mode.WITH_FRAMEBUFFER);
		synch.setSynchXEnabled(false);
		synch.setSynchYEnabled(false);
		addChild(synch);
		
		//Canvas mouse inputs (node highlighting)
		setOnMouseButtonEvent(e -> {
			if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && e.isPressed()) {
				HighlightWidget highlighter = new HighlightWidget(sharedResources, (float) e.getScaledMouseX(scaler.getScale()), (float) e.getScaledMouseY(scaler.getScale()));
				addChild(highlighter);
			}
		});
		
		//Canvas zooming
		setOnMouseScrollEvent(e -> {
			NanoVGScaler scaler = sharedResources.getScaler();
			
			scaler.offsetScale((float) (e.getYOffset() * 0.10f));
			sharedResources.getProject().setViewportScale(scaler.getScale());
			sharedResources.refreshContextHint();
		});
		
		//Canvas shortcut keys (multi-node editing)
		setOnKeyEvent(e -> {
			
			if (!e.isPressed()) {
				return;
			}
			
			if (!highlightedNodes.isEmpty() && !ClearStaticResources.isFocused()) {
				
				if (e.getKey() == GLFW.GLFW_KEY_D) {
					MultiEditUtils.deleteAll(sharedResources, highlightedNodes);
				}
				
				if (e.getKey() == GLFW.GLFW_KEY_T) {
					MultiEditUtils.addTagsToAll(sharedResources, highlightedNodes);
				}
				
				if (e.getKey() == GLFW.GLFW_KEY_R) {
					MultiEditUtils.removeTagsFromAll(sharedResources, highlightedNodes);
				}
				
				if (e.getKey() == GLFW.GLFW_KEY_N) {
					MultiEditUtils.multiTitle(sharedResources, highlightedNodes);
				}
				
				if (e.getKey() == GLFW.GLFW_KEY_S) {
					MultiEditUtils.autoSnap((float) e.getWindow().getScaledMouseX(scaler.getScale()), (float) e.getWindow().getScaledMouseY(scaler.getScale()), this, highlightedNodes);
				}
			}

			if (e.getWindow().isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) && e.getKey() == GLFW.GLFW_KEY_A) {
				highlightAll();
			}
		});
		
		//Render connections between nodes
		ConnectionRendererWidget connectionRenderer = new ConnectionRendererWidget(sharedResources);
		addChild(connectionRenderer);
	}
	
	@Override
	public void renderChildren(NanoVGContext context, WidgetAssembly rootWidgetAssembly) {
		NanoVGScaler scaler = sharedResources.getScaler();
		
		scaler.pushScale(context);
		super.renderChildren(context, rootWidgetAssembly);
		scaler.popScale(context);
	}
	
	@Override
	public void move(float newX, float newY) {
		super.move(newX, newY);
		sharedResources.getProject().setViewportPosition(newX, newY);
	}
	
	/**
	 * This function will refresh the canvas and synchronize it with the given Project.
	 */
	public void refresh(Project project) {
		
		resetHighlighted(false);
		
		/*
		 * Remove existing nodes
		 */
		
		for (int i = 0; i < getNumChildren(); i++) {
			Widget w = getChild(i);
			
			if (w instanceof DraggableDialogueWidget) {
				((DraggableDialogueWidget) w).requestRemoval(false);
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
		
		/*
		 * Rendering order Sort
		 */
		
		sortNodes();
	}
	
	/**
	 * Sort wrapper that should be called every time a change is applied to the canvas to keep the rendering order correct.
	 */
	public void sortNodes() {
		sortChildren(new Comparator<Widget>() {
			public int compare(Widget x1, Widget x2) {
				int result = Float.compare(x1.getClippedX(), x2.getClippedX());
				
				if (x1 instanceof ConnectionRendererWidget) {
					result = Float.compare(x2.getClippedX() + 1, x2.getClippedX());
				} else if (x2 instanceof ConnectionRendererWidget) {
					result = Float.compare(x1.getClippedX(), x1.getClippedX() + 1);
				} else if (result == 0) {
					// both X are equal -> compare Y too
					result = Float.compare(x1.getClippedY(), x2.getClippedY());
				}
				
				return result;
			}
		});
		
		reverseChildren();
	}
	
	private void addNode(DraggableDialogueWidget w) {
		addChild(w);
		sortNodes();
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
		addNode(widget);
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
		addNode(widget);
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
		sharedResources.refreshContextHint();
	}
	
	public void resetHighlighted(boolean onlyResetHighlighterHighlighted) {
		for (int i = 0; i < highlightedNodes.size(); i++) {
			DraggableDialogueWidget w = highlightedNodes.get(i);
			
			if (w.wasHighlightedWithHighlighter() || !onlyResetHighlighterHighlighted) {
				highlightedNodes.get(i).setHighlighted(false, false);
				i--;
			}
		}
	}
	
	public void highlightAll() {
		for (int i = 0; i < getNumChildren(); i++) {
			if (getChild(i) instanceof DraggableDialogueWidget) {
				DraggableDialogueWidget n = (DraggableDialogueWidget) getChild(i);
				n.setHighlighted(true, true);
				notifyDialogueNodeHighlighted(n);
			}
		}
	}
	
	public int getNumHighlightedNodes() {
		return highlightedNodes.size();
	}
	
	public DraggableDialogueWidget getHighlightedNode(int index) {
		return highlightedNodes.get(index);
	}
	
	/*
	 * 
	 * 
	 * Utilities
	 * 
	 * 
	 */
	
	/**
	 * Centers the canvas on the given coordinates. Make sure they're the clipped coordinates and not the parent relative ones.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void centerOn(float x, float y, float width, float height) {
		float targetX = (getX() - x) + scaler.applyScale(getWidth())/2 - width/2;
		float targetY = (getY() - y) + scaler.applyScale(getHeight())/2 - height/2;

		new CenterTransition(200, this, getX(), getY(), targetX, targetY).play();
	}
	
	public void removeDialogueNode(DraggableDialogueWidget widget) {
		if (widget.isDataFlaggedForDeletion()) {
			sharedResources.getProject().removeDialogue(widget.getDialogue());
		}
		
		removeChild(widget);
	}
	
	private float getNewDialogueX() {
		return -getX() + parent.getWidth()/2 - DraggableDialogueWidget.DEFAULT_MODE.getWidth()/2;
	}
	
	private float getNewDialogueY() {
		return -getY() + parent.getHeight()/2 - DraggableDialogueWidget.DEFAULT_MODE.getHeight()/2;
	}
	
	private class CenterTransition extends Transition {

		private ClearDialogueCanvas canvas;
		private float startX, startY, targetX, targetY;
		
		public CenterTransition(long durationInMillis, ClearDialogueCanvas canvas, float startX, float startY, float targetX, float targetY) {
			super(durationInMillis);
			this.canvas = canvas;
			this.startX = startX;
			this.startY = startY;
			this.targetX = targetX;
			this.targetY = targetY;
			
			setLinkedObject(canvas);
		}

		@Override
		public void tick(float progress) {
			float x = WidgetUtils.smoothermix(startX, targetX, progress);
			float y = WidgetUtils.smoothermix(startY, targetY, progress);
			
			canvas.setX(x);
			canvas.setY(y);
		}
		
	};
}
