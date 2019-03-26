package nokori.clear_dialogue.ui.widget.node;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.transition.SizeTransition;
import nokori.clear.vg.widget.DropShadowWidget;
import nokori.clear.vg.widget.RectangleWidget;
import nokori.clear.vg.widget.assembly.DraggableWidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.vg.widget.text.TextAreaWidget;
import nokori.clear.vg.widget.text.TextFieldWidget;
import nokori.clear.windows.Window;
import nokori.clear.windows.WindowManager;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueResponse;
import nokori.clear_dialogue.project.DialogueText;
import nokori.clear_dialogue.ui.SharedResources;

public class DraggableDialogueWidget extends DraggableWidgetAssembly {
	
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
	
	public enum Mode {
		COLLAPSED,
		EXPANDED,
		EDITING;
		
		public int getWidth() {
			switch(this) {
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
	
	private SharedResources sharedResources;
	private Dialogue dialogue;
	
	private Mode mode = Mode.COLLAPSED;
	
	private RectangleWidget background, highlight;
	private DropShadowWidget dropShadow;
	
	private TextFieldWidget title, tags;
	private TextAreaWidget content;

	public DraggableDialogueWidget(SharedResources sharedResources, Dialogue dialogue) {
		super(dialogue.getX(), dialogue.getY(), 0f, 0f);
		this.sharedResources = sharedResources;
		this.dialogue = dialogue;
		
		setIgnoreChildrenWidgets(true);
		applySize(false);
		
		/*
		 * Widget configuration
		 */
		
		dropShadow = new DropShadowWidget(CORNER_RADIUS);
		background = new RectangleWidget(CORNER_RADIUS, ClearColor.WHITE_SMOKE.multiply(0.95f), ClearColor.LIGHT_GRAY);
		highlight = new RectangleWidget(CORNER_RADIUS, null, ClearColor.CORAL.alpha(0f));
		
		NanoVGContext context = sharedResources.getNanoVGContext();
		
		float xPadding = 10f;
		float yPadding = 10f;
		float textWidth = COLLAPSED_WIDTH - (xPadding * 2f);
		
		title = new TextFieldWidget(context, textWidth, TEXT_COLOR, dialogue.getName(), sharedResources.getNotoSans(), 22);
		title.addChild(new WidgetClip(WidgetClip.Alignment.UPPER_LEFT, xPadding, yPadding));
		title.getInputSettings().setInputEnabled(false);
		
		tags = new TextFieldWidget(context, textWidth, TEXT_COLOR, dialogue.getName(), sharedResources.getNotoSans(), 16);
		tags.addChild(new WidgetClip(WidgetClip.Alignment.UPPER_LEFT, xPadding, yPadding + title.getHeight()));
		tags.getInputSettings().setInputEnabled(false);
		
		float contentY = yPadding + title.getHeight() + tags.getHeight();
		float contentHeight = (getHeight() - contentY);
		content = new TextAreaWidget(xPadding, contentY, textWidth, contentHeight, TEXT_COLOR, dialogue.getRenderableContent(), sharedResources.getNotoSerif(), 18);
		
		addChild(dropShadow, background, highlight, title, tags, content);
		
		/*
		 * Input callbacks
		 */
		
		setOnMouseEnteredEvent(e -> {
			if (!ClearStaticResources.isHoveringWidget()) {
				FillTransition fadeIn = new FillTransition(200, highlight.getStrokeFill(), ClearColor.CORAL);
				fadeIn.setLinkedObject(DraggableDialogueWidget.this);
				fadeIn.play();
				
				ClearStaticResources.setHoveringWidget(DraggableDialogueWidget.this);
			}
		});
		
		setOnMouseExitedEvent(e -> {
			if (ClearStaticResources.getHoveringWidget() == DraggableDialogueWidget.this) {
				FillTransition fadeOut = new FillTransition(200, highlight.getStrokeFill(), ClearColor.CORAL);
				fadeOut.setLinkedObject(DraggableDialogueWidget.this);
				fadeOut.play();
				
				ClearStaticResources.setHoveringWidget(null);
			}
		});
	}

	/**
	 * Applies the target dimensions for the current widget mode.
	 */
	public void applySize(boolean useTransition) {
		if (useTransition) {
			new DialogueWidgetSizeTransition().play();
		} else {
			setWidth(mode.getWidth());
			setHeight(mode.getHeight());
		}
	}
	
	private class DialogueWidgetSizeTransition extends SizeTransition {

		public DialogueWidgetSizeTransition() {
			super(200, mode.getWidth(), mode.getHeight());
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
			setWidth(width);
		}

		@Override
		protected void setHeight(float height) {
			setHeight(height);
		}
		
	};

}
