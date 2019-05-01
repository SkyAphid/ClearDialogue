package nokori.clear_dialogue.ui.widget;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.transition.TransitionImpl;
import nokori.clear.vg.widget.RectangleWidget;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.vg.widget.assembly.WidgetSynch;
import nokori.clear.vg.widget.assembly.WidgetSynch.Mode;
import nokori.clear.vg.widget.text.TextAreaWidget;
import nokori.clear.windows.util.Stopwatch;
import nokori.clear_dialogue.ui.SharedResources;
import static nokori.clear_dialogue.ui.ClearDialogueTheme.*;

public class PopupMessageWidget extends WidgetAssembly {
	
	private static final float SIZE_OFFSET = 100;
	
	private static final float START_Y_OFFSET = SIZE_OFFSET/1.35f;
	private static final float END_Y_OFFSET = SIZE_OFFSET;
	
	private static final int FADE_IN_DURATION = 200;
	private static final ClearColor OVERLAY_COLOR = ClearColor.LIGHT_BLACK.alpha(0.5f).immutable(true);
	private static final ClearColor BACKGROUND_STROKE_COLOR = ClearColor.LIGHT_BLACK;
	
	private SharedResources sharedResources;
	private Stopwatch openInputDelay = new Stopwatch();
	
	private RectangleWidget overlay, background;
	
	private TextAreaWidget content;
	private boolean contentEdited = false;
	
	public PopupMessageWidget(SharedResources sharedResources, String message, boolean editingEnabled) {
		this.sharedResources = sharedResources;
		
		/*
		 * Core setup
		 */
		
		addChild(new WidgetClip(WidgetClip.Alignment.CENTER, 0f, 0f));
		
		WidgetSynch synch = new WidgetSynch(Mode.WITH_PARENT, SIZE_OFFSET, 0f, -(SIZE_OFFSET * 2), -(SIZE_OFFSET * 2));
		addChild(synch);
		
		TransitionImpl transition = new TransitionImpl(FADE_IN_DURATION, 0f, 1f, new TransitionImpl.ProgressCallback() {
			@Override
			public void callback(float value) {
				synch.setYOffset(START_Y_OFFSET + ((END_Y_OFFSET - START_Y_OFFSET) * value));
			}
		});

		transition.play();
		
		/*
		 * Background
		 */
		
		overlay = new RectangleWidget(OVERLAY_COLOR.alpha(0.0f), true);
		overlay.addChild(new WidgetSynch(WidgetSynch.Mode.WITH_FRAMEBUFFER));
		new FillTransition(FADE_IN_DURATION, overlay.getFill(), OVERLAY_COLOR).play();
		addChild(overlay);
		
		background = new RectangleWidget(4f, BACKGROUND_COLOR.alpha(0.0f), BACKGROUND_STROKE_COLOR.alpha(0.0f), true);
		background.addChild(new WidgetSynch(this));
		new FillTransition(FADE_IN_DURATION * 2, background.getFill(), BACKGROUND_COLOR) {
			@Override
			public void tick(float progress) {
				super.tick(progress);
				blend(background.getStrokeFill(), BACKGROUND_STROKE_COLOR, background.getStrokeFill(), progress);
			}
		}.play();
		addChild(background);
		
		/*
		 * Content
		 */
		
		content = new TextAreaWidget(TEXT_COLOR.alpha(0.0f), message, sharedResources.getNotoSerif(), 20);
		content.setWordWrappingEnabled(false);
		content.setLineNumberBackgroundFill(null);
		content.getInputSettings().setEditingEnabled(editingEnabled);
		content.addChild(new WidgetSynch(this, 2f, 2f, -4f, -4f));
		new FillTransition(FADE_IN_DURATION * 2, content.getDefaultTextFill(), TEXT_COLOR).play();
		
		content.setOnCharEvent(e -> {
			contentEdited = true;
		});
		
		addChild(content);
		
		/*
		 * Input
		 */
		
		openInputDelay.timeInMilliseconds(100);
		
		setOnMouseButtonEvent(e -> {
			if (!openInputDelay.isCurrentTimePassedEndTime()) {
				return;
			}
			
			if (!e.isPressed() && !isMouseIntersectingThisWidget(e.getWindow())) {
				close();
			}
		});
	}
	
	public void show() {
		setInputEnabled(true);
		sharedResources.getCanvas().setInputEnabled(false);
		sharedResources.getToolbar().setInputEnabled(false);
		sharedResources.getRootWidgetAssembly().addChild(this);
	}
	
	public void close() {
		setInputEnabled(false);
		removeChild(content);
		
		FillTransition endTransition = new FillTransition(FADE_IN_DURATION, overlay.getFill(), OVERLAY_COLOR.alpha(0f)) {
			@Override
			public void tick(float progress) {
				super.tick(progress);
				blend(background.getFill(), BACKGROUND_COLOR.alpha(0f), background.getFill(), progress);
				blend(background.getStrokeFill(), BACKGROUND_STROKE_COLOR.alpha(0f), background.getStrokeFill(), progress);
			}
		};
		
		endTransition.setOnCompleted(c -> {
			sharedResources.getCanvas().setInputEnabled(true);
			sharedResources.getToolbar().setInputEnabled(true);
			sharedResources.getRootWidgetAssembly().removeChild(this);
			onClose(content.getTextBuilder().toString(), contentEdited);
		});
		
		endTransition.play();
	}
	
	/**
	 * Called when this Widget is closed and removed from the parent root assembly.
	 * 
	 * @param content - the final edited String content of the TextAreaWidget in this Widget.
	 * @param contentEdited - true if the string content was edited at any point
	 */
	protected void onClose(String content, boolean contentEdited) {
		
	}
	
	public TextAreaWidget getTextAreaWidget() {
		return content;
	}
}
