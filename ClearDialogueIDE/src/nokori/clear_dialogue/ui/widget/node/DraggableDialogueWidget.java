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
import nokori.clear.windows.event.MouseButtonEvent;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueConnector;
import nokori.clear_dialogue.ui.ClearDialogueCanvas;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.ConnectorWidget.ConnectorType;

import static nokori.clear_dialogue.ui.ClearDialogueTheme.*;

import java.util.concurrent.TimeUnit;

public abstract class DraggableDialogueWidget extends DraggableWidgetAssembly {
	
	public static final int COLLAPSED_WIDTH = 100;
	public static final int COLLAPSED_HEIGHT = 100;
	
	public static final int EXPANDED_WIDTH = COLLAPSED_WIDTH * 2;
	public static final int EXPANDED_HEIGHT = COLLAPSED_HEIGHT * 2;
	
	public static final int EDITING_WIDTH = EXPANDED_WIDTH * 6;
	public static final int EDITING_HEIGHT = EXPANDED_HEIGHT * 3;
	
	public static final float CORNER_RADIUS = 1.0f;

	protected static final int TOP_PADDING = 20;
	protected static final int LEFT_PADDING = 15;
	
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
	private boolean hovering = false;
	private boolean gridSnappingEnabled = false;
	
	public DraggableDialogueWidget(SharedResources sharedResources, Dialogue dialogue) {
		super(dialogue.getX(), dialogue.getY(), 0f, 0f);
		this.sharedResources = sharedResources;
		this.dialogue = dialogue;
		this.mode = dialogue.isExpanded() ? Mode.EXPANDED : Mode.COLLAPSED;
		
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
		
		float xPadding = 10f;
		float yPadding = 10f;
		float widgetPadding = (yPadding/2f);
		float textWidth = mode.getWidth() - (xPadding * 2f);
		
		//Title
		title = new TextFieldWidget(context, textWidth, TEXT_COLOR, dialogue.getTitle(), sharedResources.getNotoSans(), 24);
		title.setUnderlineFill(UNDERLINE_COLOR);
		title.addChild(new WidgetClip(WidgetClip.Alignment.TOP_LEFT, xPadding, yPadding));
		title.addChild(new TextFieldSynch(this, false, xPadding, yPadding));
		title.addChild(new TextAreaAutoFormatterWidget(sharedResources.getSyntaxSettings()));
		
		title.setOnKeyEvent(e -> {
			dialogue.setTitle(title.getTextBuilder().toString());
		});
		
		title.setOnMouseEnteredEvent(e -> {
			if (mode == Mode.EDITING) {
				sharedResources.setContextHint("LMB = Edit title");
			}
		});
		
		//Tags
		tags = new TextFieldWidget(context, textWidth, TEXT_COLOR, dialogue.getTags(), sharedResources.getNotoSans(), 20);
		tags.setUnderlineFill(UNDERLINE_COLOR);
		tags.addChild(new WidgetClip(WidgetClip.Alignment.TOP_LEFT, xPadding, title.getHeight() + yPadding + widgetPadding));
		tags.addChild(new TextFieldSynch(this, false, xPadding, yPadding));
		tags.addChild(new TextAreaAutoFormatterWidget(sharedResources.getSyntaxSettings()));
		
		tags.setOnKeyEvent(e -> {
			dialogue.setTags(tags.getTextBuilder().toString());
		});
		
		tags.setOnMouseEnteredEvent(e -> {
			if (mode == Mode.EDITING) {
				sharedResources.setContextHint("LMB = Edit tags");
			}
		});
		
		//tags.addChild(new TextAreaAutoFormatterWidget(sharedResources.getSyntaxSettings()));

		//Content
		float contentY = title.getHeight() + tags.getHeight() + yPadding + (widgetPadding * 2f);

		content = new TextAreaWidget(xPadding, contentY, textWidth, 0f, TEXT_COLOR, dialogue.getRenderableContent(), sharedResources.getNotoSerif(), 22);
		content.setLineNumbersEnabled(false);
		content.setLineSplitOverrideEnabled(true);
		content.setLineSplitOverrideWidth(Mode.EDITING.getWidth());
		content.addChild(new TextFieldSynch(this, true, xPadding, yPadding));
		content.addChild(new TextAreaAutoFormatterWidget(sharedResources.getSyntaxSettings()));
		
		content.setOnKeyEvent(e -> {
			keyEventCallback();
		});
		
		content.setOnMouseEnteredEvent(e -> {
			if (mode == Mode.EDITING) {
				sharedResources.setContextHint("LMB = Edit content");
			}
		});
		
		/*
		 * In-Connector
		 */
		
		inConnector = new ConnectorWidget(dialogue.getInConnector(), ConnectorType.IN);
		
		/*
		 * Finalize
		 */
		
		addChild(dropShadow, inConnector, background, highlight, title, tags, content);
		
		/*
		 * Input callbacks
		 */
		
		setOnMouseMotionEvent(e -> {
			highlightingCommands(sharedResources.getCanvas().canMouseMotionUnhighlightDialogueNode(this));
		});
		
		setOnMouseButtonEvent(e -> {
			if (isMouseWithin() && !e.isPressed()) {
				leftClickCommands(e);
				rightClickCommands(e);
			}
			
			highlightingCommands(true);
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
	
	private static final float SNAP_SIZE_MULTIPLIER = 1.5f;
	
	@Override
	protected void move(float newX, float newY) {
		float snapWidth = getWidth() * SNAP_SIZE_MULTIPLIER;
		float snapHeight = getHeight() * SNAP_SIZE_MULTIPLIER;
		
		float gridX = (snapWidth * (int) ((newX + getDraggingAnchor().x()) / snapWidth)) + snapWidth/2 - getWidth()/2;
		float gridY = (snapHeight * (int) ((newY + getDraggingAnchor().y()) / snapHeight)) + snapHeight/2 - getHeight()/2;
		
		//System.out.println("Called " + gridSnappingEnabled + " " + gridX + "/" + gridY + " " + newX + " " + newY + " " + (int) (newX / SNAP_WIDTH) + " " + (int) (newY / SNAP_HEIGHT));
		
		super.move((gridSnappingEnabled ? gridX : newX), (gridSnappingEnabled ? gridY : newY));
		
		dialogue.setX(getX());
		dialogue.setY(getY());
	}
	
	protected void keyEventCallback() {
		dialogue.parseAndSetContent(content.getTextBuilder().toString());
	}
	
	private void highlightingCommands(boolean resetHighlighting) {
		hovering = (isMouseWithin() && ClearStaticResources.isFocusedOrCanFocus(this)) || isDragging();
		
		setHighlighted(highlighted && !resetHighlighting || hovering);
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
						transitionMode(Mode.EDITING);
					} else {
						endEditing();
					}
				}
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
	public void setHighlighted(boolean highlighted) {
		boolean bHighlighted = this.highlighted;
		this.highlighted = highlighted;
		
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
			
			sharedResources.setContextHint(CONTEXT_HINT);
		}
		
		//Fade out
		if (bHighlighted && !highlighted) {
			FillTransition fadeOut = new FillTransition(TRANSITION_DURATION, highlight.getStrokeFill(), ClearColor.CORAL.alpha(0f));
			fadeOut.setLinkedObject(DraggableDialogueWidget.this);
			fadeOut.play();
				
			sharedResources.resetContextHint();
		}
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

	/**
	 * Applies the target dimensions for the current widget mode.
	 */
	public void transitionMode(Mode mode) {
		bMode = this.mode;
		this.mode = mode;

		new DialogueWidgetSizeTransition().play();
		
		if (mode != Mode.EDITING) {
			dialogue.setExpanded(mode == Mode.EXPANDED);
		}
		
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
		public void synch(Window window) {
			setXOffset(xPadding);
			setWOffset(-(xPadding * 2f));
			super.synch(window);
		}
		
		@Override
		protected void synchHeight(Window window, Mode mode) {
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
