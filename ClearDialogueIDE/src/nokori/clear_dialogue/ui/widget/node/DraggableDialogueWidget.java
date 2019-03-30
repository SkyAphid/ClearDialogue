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
import nokori.clear.vg.widget.text.TextAreaWidget;
import nokori.clear.vg.widget.text.TextFieldWidget;
import nokori.clear.windows.Window;
import nokori.clear.windows.event.MouseButtonEvent;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueConnector;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.ConnectorWidget.ConnectorType;

import java.util.concurrent.TimeUnit;

public abstract class DraggableDialogueWidget extends DraggableWidgetAssembly {
	
	public static final int COLLAPSED_WIDTH = 100;
	public static final int COLLAPSED_HEIGHT = 100;
	
	public static final int EXPANDED_WIDTH = COLLAPSED_WIDTH * 2;
	public static final int EXPANDED_HEIGHT = COLLAPSED_HEIGHT * 2;
	
	public static final int EDITING_WIDTH = EXPANDED_WIDTH * 6;
	public static final int EDITING_HEIGHT = EXPANDED_HEIGHT * 3;
	
	public static final ClearColor TEXT_COLOR = ClearColor.LIGHT_BLACK;
	
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
	
	public DraggableDialogueWidget(SharedResources sharedResources, Dialogue dialogue) {
		super(dialogue.getX(), dialogue.getY(), 0f, 0f);
		this.sharedResources = sharedResources;
		this.dialogue = dialogue;
		
		/*
		 * Widget Container
		 */
		
		dropShadow = new DropShadowWidget(CORNER_RADIUS);
		dropShadow.setInputEnabled(false);
		
		background = new RectangleWidget(CORNER_RADIUS, ClearColor.WHITE_SMOKE.multiply(0.95f), ClearColor.LIGHT_GRAY);
		background.setInputEnabled(false);
		
		highlight = new RectangleWidget(CORNER_RADIUS, null, ClearColor.CORAL.alpha(0f));
		highlight.setInputEnabled(false);
		
		/*
		 * Text fields
		 */
		
		NanoVGContext context = sharedResources.getNanoVGContext();
		
		float xPadding = 15f;
		float yPadding = 15f;
		float widgetPadding = (yPadding/2f);
		float textWidth = mode.getWidth() - (xPadding * 2f);
		
		//Title
		title = new TextFieldWidget(context, textWidth, TEXT_COLOR, dialogue.getTitle(), sharedResources.getNotoSans(), 24);
		title.addChild(new WidgetClip(WidgetClip.Alignment.TOP_LEFT, xPadding, yPadding));
		title.addChild(new TextFieldSynch(false, xPadding, yPadding));
		
		title.setOnKeyEvent(e -> {
			dialogue.setTitle(title.getTextBuilder().toString());
		});
		
		//Tags
		tags = new TextFieldWidget(context, textWidth, TEXT_COLOR, dialogue.getTitle(), sharedResources.getNotoSans(), 20);
		tags.addChild(new WidgetClip(WidgetClip.Alignment.TOP_LEFT, xPadding, title.getHeight() + yPadding + widgetPadding));
		tags.addChild(new TextFieldSynch(false, xPadding, yPadding));
		
		tags.setOnKeyEvent(e -> {
			dialogue.setTag(tags.getTextBuilder().toString());
		});

		//Content
		float contentY = title.getHeight() + tags.getHeight() + yPadding + (widgetPadding * 2f);

		content = new TextAreaWidget(xPadding, contentY, textWidth, 0f, TEXT_COLOR, dialogue.getRenderableContent(), sharedResources.getNotoSerif(), 22);
		content.setLineNumbersEnabled(false);
		content.setLineSplitOverrideEnabled(true);
		content.setLineSplitOverrideWidth(Mode.EDITING.getWidth());
		content.addChild(new TextFieldSynch(true, xPadding, yPadding));
		
		content.setOnKeyEvent(e -> {
			keyEventCallback();
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
		
		setOnMouseEnteredEvent(e -> {
			if (ClearStaticResources.canFocus(this)) {
				FillTransition fadeIn = new FillTransition(200, highlight.getStrokeFill(), ClearColor.CORAL);
				fadeIn.setLinkedObject(DraggableDialogueWidget.this);
				fadeIn.play();
					
				ClearStaticResources.setHoveringWidget(DraggableDialogueWidget.this);
				sharedResources.setContextHint(CONTEXT_HINT);
			}
		});
		
		setOnMouseExitedEvent(e -> {
			FillTransition fadeOut = new FillTransition(200, highlight.getStrokeFill(), ClearColor.CORAL.alpha(0f));
			fadeOut.setLinkedObject(DraggableDialogueWidget.this);
			fadeOut.play();
				
			if (ClearStaticResources.getHoveringWidget() == DraggableDialogueWidget.this) {
				ClearStaticResources.setHoveringWidget(null);
				sharedResources.resetContextHint();
			}
		});
		
		setOnMouseButtonEvent(e -> {
			if (isMouseWithinThisWidget() && !e.isPressed()) {
				leftClickCommands(e);
				rightClickCommands(e);
			}
		});
		
		/*
		 * Apply
		 */
		
		transitionMode(mode);
	}

	public Dialogue getDialogue() {
		return dialogue;
	}
	
	public ConnectorWidget findConnectorWidget(DialogueConnector connector) {
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
	
	protected void keyEventCallback() {
		dialogue.parseAndSetContent(content.getTextBuilder().toString());
	}
	
	public void leftClickCommands(MouseButtonEvent e) {
		long clickTime = e.getTimestamp();

		if (e.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			if (lastLeftClickTime != -1 && TimeUnit.NANOSECONDS.toMillis(clickTime - lastLeftClickTime) <= CLICK_TIME) {
				
				//Switch between expanded and collapsed mode
				if (mode == Mode.COLLAPSED) {
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
						
						delete();
						
					}
				} else {
					//Switch between editing and the last selected mode
					if (mode != Mode.EDITING) {
						transitionMode(Mode.EDITING);
					} else {
						transitionMode(bMode);
					}
				}
			}
			
			lastRightClickTime = clickTime;
		}
	}
	
	public void delete() {
		transitionMode(Mode.DELETION);
		fadeOutConnector(inConnector);
	}
	
	protected void fadeOutConnector(ConnectorWidget connector) {
		new FillTransition(200, connector.getFill(), connector.getFill().copy().alpha(0f)).play();
	}

	/**
	 * Applies the target dimensions for the current widget mode.
	 */
	public void transitionMode(Mode mode) {
		bMode = this.mode;
		this.mode = mode;

		new DialogueWidgetSizeTransition().play();
		
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

	private class TextFieldSynch extends WidgetSynch {
		
		private float xPadding, yPadding;
		
		public TextFieldSynch(boolean synchHeight, float xPadding, float yPadding) {
			super(false, false, true, synchHeight);
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
		protected void synchHeight(Window window) {
			TextAreaWidget textArea = (TextAreaWidget) parent;
			textArea.setHeight(DraggableDialogueWidget.this.getHeight() - textArea.getY() - yPadding);
		}
	};
	
	private class DialogueWidgetSizeTransition extends SizeTransition {

		public DialogueWidgetSizeTransition() {
			super(200, Math.max(mode.getWidth(), getMinWidth()), Math.max(mode.getHeight(), getMinHeight()));
			
			setLinkedObject(DraggableDialogueWidget.this);
			
			setOnCompleted(e -> {
				if (mode == Mode.DELETION) {
					sharedResources.getCanvas().deleteDialogueNode(DraggableDialogueWidget.this);
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
