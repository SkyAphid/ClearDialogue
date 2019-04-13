package nokori.clear_dialogue.ui.widget;

import java.util.Stack;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.widget.RectangleWidget;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear_dialogue.ui.ClearDialogueCanvas;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.DraggableDialogueWidget;

/**
 * A highlighting widget that will allow the user to click and drag a rectangle to highlighting a group of nodes. 
 * <br><br>
 * This node checks its parent to scan for highlighted nodes, so make sure that this widget is added to whichever WidgetContainer holds all of the dialogue nodes.
 */
public class HighlightWidget extends RectangleWidget {
	
	private static final float ALPHA = 0.5f;
	
	private Stack<DraggableDialogueWidget> highlighting = new Stack<>();
	
	public HighlightWidget(SharedResources sharedResources, float clampX, float clampY) {
		super(0.0f, ClearColor.CORAL.alpha(ALPHA), ClearColor.CORAL.multiply(1.2f).alpha(ALPHA), false);
		setPosition(clampX, clampY);
		setWidth(0f);
		setHeight(0f);

		setOnInternalMouseMotionEvent(e -> {
			float mX = (float) e.getMouseX();
			float mY = (float) e.getMouseY();
			
			/*
			 * If mouse coordinates < clamped coordinates
			 */
			if (mX < clampX) {
				setX(mX);
				setWidth(clampX - mX);
			}
			
			if (mY < clampY) {
				setY(mY);
				setHeight(clampY - mY);
			}
			
			/*
			 * If mouse coordinates > clamped coordinates
			 */
			
			if (mX > clampX) {
				setX(clampX);
				setWidth(mX - clampX);
			}
			
			if (mY > clampY) {
				setY(clampY);
				setHeight(mY - clampY);
			}
			
			/*
			 * Apply the highlighting
			 */
			
			applyHighlighting(sharedResources.getCanvas());
		});
		
		//Highlight all of the selected nodes and then delete the highlighting widget.
		setOnInternalMouseButtonEvent(e -> {
			if (!e.isPressed()) {
				applyHighlighting(sharedResources.getCanvas());
				parent.removeChild(this);
			}
		});
	}
	
	private void applyHighlighting(ClearDialogueCanvas canvas) {
		Stack<DraggableDialogueWidget> highlighted = getHighlightedNodes();
		
		while(!highlighted.isEmpty()) {
			highlighted.pop().setHighlighted(true);
		}
	}
	
	/**
	 * @return - all DraggableDialogueWidgets highlighted by this Widget
	 */
	public Stack<DraggableDialogueWidget> getHighlightedNodes() {
		highlighting.clear();
		
		for (int i = 0; i < parent.getNumChildren(); i++) {
			Widget w = parent.getChild(i);
			
			if (w instanceof DraggableDialogueWidget && intersects(w)) {
				highlighting.push((DraggableDialogueWidget) w);
			}
		}
		
		return highlighting;
	}
}
