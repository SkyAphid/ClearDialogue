package nokori.clear_dialogue.ui.widget.node;

import org.lwjgl.glfw.GLFW;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.transition.SizeTransition;
import nokori.clear.vg.widget.DropShadowWidget;
import nokori.clear.vg.widget.RectangleWidget;
import nokori.clear.vg.widget.assembly.DraggableWidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.vg.widget.assembly.WidgetSynch;
import nokori.clear.vg.widget.text.TextAreaAutoFormatterWidget;
import nokori.clear.vg.widget.text.TextAreaWidget;
import nokori.clear.vg.widget.text.TextFieldWidget;
import nokori.clear.windows.Window;
import nokori.clear.windows.event.Event;
import nokori.clear.windows.event.MouseButtonEvent;
import nokori.clear.windows.event.MouseMotionEvent;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueConnector;
import nokori.clear_dialogue.ui.ClearDialogueCanvas;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.ConnectorWidget.ConnectorType;

import static nokori.clear_dialogue.ui.ClearDialogueTheme.*;

import java.util.concurrent.TimeUnit;

public abstract class DraggableDialogueWidget extends DraggableWidgetAssembly {
	
	public static final int COLLAPSED_WIDTH = 200;
	public static final int COLLAPSED_HEIGHT = 200;
	
	public static final int EXPANDED_WIDTH = 600;
	public static final int EXPANDED_HEIGHT = 300;
	
	public static final int EDITING_WIDTH = 1200;
	public static final int EDITING_HEIGHT = 600;
	
	public static final float CORNER_RADIUS = 1.0f;

	protected static final int X_PADDING = 10;
	protected static final int Y_PADDING = 10;
	
	protected static final int TOP_PADDING = 20;
	protected static final int LEFT_PADDING = 15;
	
	public static final float SNAP_SIZE_MULTIPLIER = 1.5f;
	
	public static final String CONTEXT_HINT = "LMBx2 = Expand/Collapse | RMBx2 = Toggle Editing Mode | Shift + RMBx2 = Delete";
	
	public enum Mode {
		DELETION,
		COLLAPSED,
		EXPANDED,
		EDITING;
		
		public static final Mode[] values = values();
		
		public int getWidth() {
			switch(this) {
			case DELETION:
				return 0;
			case EDITING:
				return EDITING_WIDTH;
			case EXPANDED:
				return EXPANDED_WIDTH;
			case COLLAPSED:
			default:
				return COLLAPSED_WIDTH;
			}
		}
		
		public int getHeight() {
			switch(this) {
			case DELETION:
				return 0;
			case EDITING:
				return EDITING_HEIGHT;
			case EXPANDED:
				return EXPANDED_HEIGHT;
			case COLLAPSED:
			default:
				return COLLAPSED_HEIGHT;
			}
		}
	};
	
	public static final Mode DEFAULT_MODE = Mode.COLLAPSED;
	
	private boolean deleteFlag = false;
	
	protected SharedResources sharedResources;
	protected Dialogue dialogue;
	
	protected Mode mode = DEFAULT_MODE;
	private Mode bMode = mode;
	
	private RectangleWidget background, highlight;
	private DropShadowWidget dropShadow;
	
	private TextFieldWidget title, tags;
	protected TextAreaWidget content;
	
	protected ConnectorWidget inConnector;
	
	private static final long CLICK_TIME = 250;
	private long lastLeftClickTime = -1L;
	private long lastRightClickTime = -1L;
	
	private boolean highlighted = false;
	private boolean highlightedWithHighlighter = false;
	private boolean hovering = false;
	private boolean gridSnappingEnabled = false;
	
	public DraggableDialogueWidget(SharedResources sharedResources, Dialogue dialogue) {
		super(dialogue.getX(), dialogue.getY(), 0f, 0f);
		this.sharedResources = sharedResources;
		this.dialogue = dialogue;
		this.mode = dialogue.isExpanded() ? Mode.EXPANDED : Mode.COLLAPSED;
		
		setScaler(sharedResources.getScaler());
		
		/*
		 * Widget Container
		 */
		
		dropShadow = new DropShadowWidget(CORNER_RADIUS);
		dropShadow.setInputEnabled(false);
		
		background = new RectangleWidget(CORNER_RADIUS, BACKGROUND_COLOR, BACKGROUND_STROKE_COLOR, true);
		background.setInputEnabled(false);
		
		highlight = new RectangleWidget(CORNER_RADIUS, null, ClearColor.CORAL.alpha(0f), true);
		highlight.setInputEnabled(false);
		
		/*
		 * Text fields
		 */
		
		NanoVGContext context = sharedResources.getNanoVGContext();
		
		float widgetPadding = (Y_PADDING/2f);
		float textWidth = mode.getWidth() - (X_PADDING * 2f);
		
		//Title
		title = new TextFieldWidget(context, textWidth, sharedResources.getScaler(), TEXT_COLOR, dialogue.getTitle(), sharedResources.getNotoSans(), 24);
		title.setUnderlineFill(UNDERLINE_COLOR);
		title.addChild(new WidgetClip(WidgetClip.Alignment.TOP_LEFT, X_PADDING, Y_PADDING));
		title.addChild(new TextFieldSynch(this, false, X_PADDING, Y_PADDING));
		title.addChild(new TextAreaAutoFormatterWidget(sharedResources.getSyntaxSettings()));
		
		title.setOnKeyEvent(e -> {
			dialogue.setTitle(title.getTextBuilder().toString());
		});
		
		//Tags
		tags = new TextFieldWidget(context, textWidth, sharedResources.getScaler(), TEXT_COLOR, dialogue.getTags(), sharedResources.getNotoSans(), 20);
		tags.setUnderlineFill(UNDERLINE_COLOR);
		tags.addChild(new WidgetClip(WidgetClip.Alignment.TOP_LEFT, X_PADDING, title.getHeight() + Y_PADDING + widgetPadding));
		tags.addChild(new TextFieldSynch(this, false, X_PADDING, Y_PADDING));
		tags.addChild(new TextAreaAutoFormatterWidget(sharedResources.getSyntaxSettings()));
		
		tags.setOnKeyEvent(e -> {
			dialogue.setTags(tags.getTextBuilder().toString());
		});
		
		//tags.addChild(new TextAreaAutoFormatterWidget(sharedResources.getSyntaxSettings()));

		//Content
		float contentY = title.getHeight() + tags.getHeight() + Y_PADDING + (widgetPadding * 2f);

		content = new TextAreaWidget(X_PADDING, contentY, textWidth, 0f, sharedResources.getScaler(), TEXT_COLOR, dialogue.getRenderableContent(), sharedResources.getNotoSerif(), 22);
		content.setLineNumbersEnabled(false);
		content.setLineSplitOverrideEnabled(true);
		content.addChild(new TextFieldSynch(this, true, X_PADDING, Y_PADDING));
		content.addChild(new TextAreaAutoFormatterWidget(sharedResources.getSyntaxSettings()));
		
		content.setOnKeyEvent(e -> {
			keyEventCallback();
		});
		
		/*
		 * In-Connector
		 */
		
		inConnector = new ConnectorWidget(sharedResources.getScaler(), dialogue.getInConnector(), ConnectorType.IN);
		
		/*
		 * Finalize
		 */
		
		addChild(dropShadow, inConnector, background, highlight, title, tags, content);
		
		/*
		 * Input callbacks
		 */
		
		setOnMouseMotionEvent(e -> {
			highlightingCommands(e);
		});
		
		setOnMouseButtonEvent(e -> {
			if (e.isConsumed() || !highlighted) {
				return;
			}
			
			//TODO: Right click code not being called because something is turning off the highlighting when the node is right clicked
			
			if (isMouseWithin() && !e.isPressed()) {
				leftClickCommands(e);
				rightClickCommands(e);
			}
			
			//This seems to cause issues with highlighting
			//highlightingCommands(e);
		});
		
		setOnKeyEvent(e -> {
			//Snap the node to the grid when dragging if left shift is held down
			if (e.getKey() == GLFW.GLFW_KEY_LEFT_SHIFT) {
				gridSnappingEnabled = e.isPressed();
			}
		});
		
		/*
		 * Apply
		 */
		
		transitionMode(mode);
	}
	
	/*
	 * 
	 * 
	 * 
	 * Input
	 * 
	 * 
	 * 
	 */

	@Override
	public void move(float newX, float newY) {
		super.move((gridSnappingEnabled ? snapValue(newX) : newX), (gridSnappingEnabled ? snapValue(newY) : newY));
		
		dialogue.setX(getX());
		dialogue.setY(getY());
		
		sharedResources.getCanvas().sortNodes();
	}
	
	public float snapValue(float value) {
		float snapSize = Math.max(getWidth(), getHeight()) * SNAP_SIZE_MULTIPLIER;
		
		int grid = (int) (value / snapSize);
		float snap = grid  * snapSize;
		
		return snap;
	}
	
	@Override
	protected void draggingAnchorCallback(MouseButtonEvent e) {
		super.draggingAnchorCallback(e);
		
		/*
		 * Multi-dragging anchor
		 */
		
		ClearDialogueCanvas canvas = sharedResources.getCanvas();
		
		for (int i = 0; i < canvas.getNumHighlightedNodes(); i++) {
			DraggableDialogueWidget w = canvas.getHighlightedNode(i);
			w.clipDraggingAnchor((float) e.getScaledMouseX(scaler.getScale()), (float) e.getScaledMouseY(scaler.getScale()));
		}
		
		/*
		 * Consume event
		 */
		
		e.setConsumed(true);
	}
	
	@Override
	protected void draggingCallback(MouseMotionEvent e) {
		super.draggingCallback(e);
		
		/*
		 * Multi-dragging
		 */
		
		ClearDialogueCanvas canvas = sharedResources.getCanvas();
		
		for (int i = 0; i < canvas.getNumHighlightedNodes(); i++) {
			DraggableDialogueWidget w = canvas.getHighlightedNode(i);
			w.move(w.getDragX(e.getScaledMouseX(scaler.getScale())), w.getDragY(e.getScaledMouseY(scaler.getScale())));
		}
	}
	
	@Override
	protected void draggingReleaseCallback(MouseButtonEvent e, boolean wasMoved) {
		e.setConsumed(wasMoved);
	}
	
	@Override
	protected boolean canDrag(Window window, float scale) {
		return (highlighted && super.canDrag(window, sharedResources.getScaler().getScale()));
	}
	
	protected void keyEventCallback() {
		dialogue.parseAndSetContent(content.getTextBuilder().toString());
	}
	
	private void highlightingCommands(Event e) {
		if (highlightedWithHighlighter) {
			return;
		}
		
		if (e.isConsumed() || sharedResources.getToolbar().isMouseIntersectingChild(e.getWindow())) {
			hovering = false;
		} else {
			hovering = (isMouseWithin() && ClearStaticResources.isFocusedOrCanFocus(this)) || isDragging();
		}
		
		setHighlighted(hovering || this.mode == Mode.EDITING, false);
		
		if (hovering && e instanceof MouseMotionEvent) {
			e.setConsumed(true);
		}
	}
	
	public void leftClickCommands(MouseButtonEvent e) {
		long clickTime = e.getTimestamp();
	
		if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (lastLeftClickTime != -1 && TimeUnit.NANOSECONDS.toMillis(clickTime - lastLeftClickTime) <= CLICK_TIME) {
				
				
				if (mode == Mode.EDITING) {
					endEditing();
				} else if (mode == Mode.COLLAPSED) {
					transitionMode(Mode.EXPANDED);
				} else if (mode == Mode.EXPANDED) {
					transitionMode(Mode.COLLAPSED);
				}

				e.setConsumed(true);
				lastLeftClickTime = -1L;
				return;
			}
			
			lastLeftClickTime = clickTime;
		}
	}
	
	public void rightClickCommands(MouseButtonEvent e) {
		long clickTime = e.getTimestamp();
		
		if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
			if (lastRightClickTime != -1 && TimeUnit.NANOSECONDS.toMillis(clickTime - lastRightClickTime) <= CLICK_TIME) {

				if (sharedResources.getWindow().isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
					if (TinyFileDialog.showConfirmDialog("Delete Dialogue", 
							"Are you sure you want to delete " + dialogue.getTitle() + "?\nThis cannot be undone.",
							TinyFileDialog.InputType.YES_NO, TinyFileDialog.Icon.QUESTION, false)) {
						
						requestRemoval(true);
						
					}
				} else {
					//Switch between editing and the last selected mode
					if (mode != Mode.EDITING) {
						sharedResources.getCanvas().centerOn(getClippedX(), getClippedY(), EDITING_WIDTH, EDITING_HEIGHT);
						transitionMode(Mode.EDITING);
					} else {
						endEditing();
						sharedResources.getCanvas().resetHighlighted(false);
					}
				}
				
				e.setConsumed(true);
			}
			
			lastRightClickTime = clickTime;
		}
	}
	
	/*
	 * 
	 * 
	 * 
	 * Menu controls & animation
	 * 
	 * 
	 * 
	 */
	
	public Dialogue getDialogue() {
		return dialogue;
	}
	
	/**
	 * Gets the ConnectorWidget attached to the DraggableDialogueWidget (if applicable) for the given connector
	 * 
	 * @param connector
	 * @return
	 */
	public ConnectorWidget findConnectorWidget(DialogueConnector connector) {
		if (mode == Mode.DELETION) {
			return null;
		}
		
		for (int i = 0; i < getNumChildren(); i++) {
			if (getChild(i) instanceof ConnectorWidget) {
				ConnectorWidget w = (ConnectorWidget) getChild(i);
				
				if (w.getConnector() == connector) {
					return w;
				}
			}
		}
		
		return null;
	}
	
	protected float getMinWidth() {
		return (mode != Mode.DELETION ? Mode.COLLAPSED.getWidth() : 0f);
	}
	
	protected float getMinHeight() {
		return (mode != Mode.DELETION ? Mode.COLLAPSED.getHeight() : 0f);
	}
	
	/**
	 * Sets the highlighted boolean and triggers animations if necessary.
	 * 
	 * @param highlighted
	 */
	public void setHighlighted(boolean highlighted, boolean highlightedWithHighlighter) {
		boolean bHighlighted = this.highlighted;
		this.highlighted = highlighted;
		this.highlightedWithHighlighter = highlightedWithHighlighter;
		
		//Register as highlighted to canvas
		ClearDialogueCanvas canvas = sharedResources.getCanvas();
		
		if (highlighted) {
			canvas.notifyDialogueNodeHighlighted(this);
		} else {
			canvas.notifyDialogueNodeUnhighlighted(this);
		}
		
		//Fade in
		if (!bHighlighted && highlighted) {
			FillTransition fadeIn = new FillTransition(TRANSITION_DURATION, highlight.getStrokeFill(), ClearColor.CORAL);
			fadeIn.setLinkedObject(DraggableDialogueWidget.this);
			fadeIn.play();
		}
		
		//Fade out
		if (bHighlighted && !highlighted) {
			FillTransition fadeOut = new FillTransition(TRANSITION_DURATION, highlight.getStrokeFill(), ClearColor.CORAL.alpha(0f));
			fadeOut.setLinkedObject(DraggableDialogueWidget.this);
			fadeOut.play();
				
			sharedResources.refreshContextHint();
		}
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}

	public boolean wasHighlightedWithHighlighter() {
		return highlightedWithHighlighter;
	}
	

	/**
	 * Ends editing mode and transforms the Widget back into it's non-editable form.
	 */
	private void endEditing() {
		transitionMode(bMode);
	}
	
	/**
	 * Requests this DraggableDialogueWidget to close and remove itself from the canvas. This is essentially an animated version of removing the widget.
	 * 
	 * @param flagForDeletion - if true, the contents of this node will also be deleted from the project once the animation is complete.
	 */
	public void requestRemoval(boolean flagForDeletion) {
		deleteFlag = flagForDeletion;
		transitionMode(Mode.DELETION);
		fadeOutConnector(inConnector);
	}
	
	public boolean isDataFlaggedForDeletion() {
		return deleteFlag;
	}
	
	protected void fadeOutConnector(ConnectorWidget connector) {
		new FillTransition(TRANSITION_DURATION, connector.getFill(), connector.getFill().copy().alpha(0f)).play();
	}

	public boolean isGridSnappingEnabled() {
		return gridSnappingEnabled;
	}
	
	public void setGridSnappingEnabled(boolean gridSnappingEnabled) {
		this.gridSnappingEnabled = gridSnappingEnabled;
	}
	

	/**
	 * Applies the target dimensions for the current widget mode.
	 */
	public void transitionMode(Mode mode) {
		bMode = this.mode;
		this.mode = mode;

		new DialogueWidgetSizeTransition().play();
		
		if (mode == Mode.EXPANDED || mode == Mode.COLLAPSED) {
			dialogue.setExpanded(mode == Mode.EXPANDED);
		}
		
		content.setLineSplitOverrideWidth(mode.getWidth() - (X_PADDING * 2.25f));
		
		/*
		 * Set input settings based on mode
		 */
		
		setInputEnabled(mode != Mode.DELETION);
		
		title.endEditing();
		tags.endEditing();
		content.endEditing();
		
		setIgnoreChildrenWidgets(mode != Mode.EDITING);
		
		title.getInputSettings().setInputEnabled(mode == Mode.EDITING);
		tags.getInputSettings().setInputEnabled(mode == Mode.EDITING);
		content.getInputSettings().setInputEnabled(mode == Mode.EDITING);
	}
	
	public Mode getMode() {
		return mode;
	}
	
	/*
	 * 
	 * 
	 * 
	 * Custom classes
	 * 
	 * 
	 * 
	 */

	private class TextFieldSynch extends WidgetSynch {
		
		private float xPadding, yPadding;
		
		public TextFieldSynch(DraggableDialogueWidget parent, boolean synchHeight, float xPadding, float yPadding) {
			super(parent, false, false, true, synchHeight);
			this.xPadding = xPadding;
			this.yPadding = yPadding;
		}
		
		@Override
		public void synch(NanoVGContext context) {
			setXOffset(xPadding);
			setWOffset(-(xPadding * 2f));
			super.synch(context);
		}
		
		@Override
		protected void synchHeight(NanoVGContext context, Mode mode) {
			TextAreaWidget textArea = (TextAreaWidget) parent;
			textArea.setHeight(DraggableDialogueWidget.this.getHeight() - textArea.getY() - yPadding);
		}
	};
	
	private class DialogueWidgetSizeTransition extends SizeTransition {

		public DialogueWidgetSizeTransition() {
			super(TRANSITION_DURATION, Math.max(mode.getWidth(), getMinWidth()), Math.max(mode.getHeight(), getMinHeight()));
			
			setLinkedObject(DraggableDialogueWidget.this);
			
			setOnCompleted(e -> {
				if (mode == Mode.DELETION) {
					sharedResources.getCanvas().removeDialogueNode(DraggableDialogueWidget.this);
				} else {
					title.requestRefresh();
					tags.requestRefresh();
					content.requestRefresh();
				}
			});
		}

		@Override
		protected float getCurrentWidth() {
			return getWidth();
		}

		@Override
		protected float getCurrentHeight() {
			return getHeight();
		}

		@Override
		protected void setWidth(float width) {
			DraggableDialogueWidget.this.setWidth(width);
		}

		@Override
		protected void setHeight(float height) {
			DraggableDialogueWidget.this.setHeight(height);
		}
	};

}
