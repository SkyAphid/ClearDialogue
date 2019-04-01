package nokori.clear_dialogue.ui.widget;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.transition.TemplateTransition;
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
	private static final int FADE_IN_DURATION = 200;
	private static final ClearColor OVERLAY_COLOR = ClearColor.LIGHT_BLACK.alpha(0.5f).immutable(true);
	
	private SharedResources sharedResources;
	private Stopwatch openInputDelay = new Stopwatch();
	
	
	public PopupMessageWidget(SharedResources sharedResources, String message) {
		this.sharedResources = sharedResources;
		
		/*
		 * Core setup
		 */
		
		addChild(new WidgetClip(WidgetClip.Alignment.CENTER, 0f, 0f));
		
		WidgetSynch synch = new WidgetSynch(Mode.WITH_PARENT, SIZE_OFFSET, 0f, -(SIZE_OFFSET * 2), -(SIZE_OFFSET * 2));
		addChild(synch);
		
		TemplateTransition transition = new TemplateTransition(FADE_IN_DURATION, 0f, 1f, new TemplateTransition.ProgressCallback() {
			@Override
			public void callback(float value) {
				synch.setYOffset(SIZE_OFFSET * value);
			}
		});
		transition.play();
		
		/*
		 * Background
		 */
		
		RectangleWidget overlay = new RectangleWidget(OVERLAY_COLOR.alpha(0.0f));
		overlay.addChild(new WidgetSynch(WidgetSynch.Mode.WITH_WINDOW));
		new FillTransition(FADE_IN_DURATION, overlay.getFill(), OVERLAY_COLOR).play();
		addChild(overlay);
		
		RectangleWidget background = new RectangleWidget(4f, BACKGROUND_COLOR.alpha(0.0f), ClearColor.LIGHT_BLACK);
		background.addChild(new WidgetSynch(this));
		new FillTransition(FADE_IN_DURATION * 2, background.getFill(), BACKGROUND_COLOR).play();
		addChild(background);
		
		/*
		 * Content
		 */
		
		TextAreaWidget content = new TextAreaWidget(TEXT_COLOR.alpha(0.0f), message, sharedResources.getNotoSerif(), 20);
		content.setWordWrappingEnabled(false);
		content.addChild(new WidgetSynch(this, 2f, 2f, -4f, -4f));
		new FillTransition(FADE_IN_DURATION * 2, content.getDefaultTextFill(), TEXT_COLOR).play();
		addChild(content);
		
		/*
		 * Input
		 */
		
		openInputDelay.timeInMilliseconds(100);
		
		setOnMouseButtonEvent(e -> {
			if (!openInputDelay.isCurrentTimePassedEndTime()) {
				return;
			}
			
			if (!e.isPressed() && !isMouseWithinThisWidget(e.getWindow())) {
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
		sharedResources.getCanvas().setInputEnabled(true);
		sharedResources.getToolbar().setInputEnabled(true);
		sharedResources.getRootWidgetAssembly().removeChild(this);
	}
}
