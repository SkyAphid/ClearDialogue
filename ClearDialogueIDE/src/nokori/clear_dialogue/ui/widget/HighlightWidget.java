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
	
	public HighlightWidget(SharedResources sharedResources, float mouseX, float mouseY) {
		super(0.0f, ClearColor.CORAL.alpha(ALPHA), ClearColor.CORAL.multiply(1.2f).alpha(ALPHA), false);
		
		setScaler(sharedResources.getScaler());
		
		ClearDialogueCanvas canvas = sharedResources.getCanvas();
		
		float clampX = -canvas.getX() + mouseX;
		float clampY = -canvas.getY() + mouseY;
		
		setPosition(clampX, clampY);
		setWidth(0f);
		setHeight(0f);
		
		canvas.resetHighlighted();

		setOnInternalMouseMotionEvent(e -> {
			float dragX = (float) (-canvas.getX() + e.getScaledMouseX(scaler.getScale()));
			float dragY = (float) (-canvas.getY() + e.getScaledMouseY(scaler.getScale()));
			
			/*
			 * If mouse coordinates < clamped coordinates
			 */
			if (dragX < clampX) {
				setX(dragX);
				setWidth(clampX - dragX);
			}
			
			if (dragY < clampY) {
				setY(dragY);
				setHeight(clampY - dragY);
			}
			
			/*
			 * If mouse coordinates > clamped coordinates
			 */
			
			if (dragX > clampX) {
				setX(clampX);
				setWidth(dragX - clampX);
			}
			
			if (dragY > clampY) {
				setY(clampY);
				setHeight(dragY - clampY);
			}
			
			/*
			 * Apply the highlighting
			 */
			
			applyHighlighting(sharedResources.getCanvas());
		});
		
		//Highlight all of the selected nodes and then delete the highlighting widget.
		setOnInternalMouseButtonEvent(e -> {
			if (!e.isPressed()) {
				applyHighlighting(canvas);
				parent.removeChild(this);
			}
		});
	}

	private void applyHighlighting(ClearDialogueCanvas canvas) {
		canvas.resetHighlighted();
		
		Stack<DraggableDialogueWidget> highlighted = getHighlightedNodes();
		
		while(!highlighted.isEmpty()) {
			highlighted.pop().setHighlighted(true, true);
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
