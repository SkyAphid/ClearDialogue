package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.JDialogueWidgetAssembly.*;

import nokori.clear.vg.font.Font;
import nokori.clear.vg.font.FontStyle;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.transition.SizeTransition;
import nokori.clear.vg.widget.LabelWidget;

public abstract class CDDropdownMenu extends CDButton {
	
	public static final int WIDTH = 200;
	
	private boolean highlighted = false;
	
	/*
	 * Options
	 */
	
	private static final int OPTION_FADE_TIME = 200;
	private LabelWidget[] optionWidgets;
	private FillTransition[] fillTransitions = null;
	
	public CDDropdownMenu(float x, float y, Font font, String label, String options[]) {
		super(x, y, WIDTH);

		/*
		 * Create Label
		 */
		
		LabelWidget labelWidget = new LabelWidget(TOOLBAR_TEXT_FILL, label, font, FontStyle.REGULAR, TOOLBAR_FONT_SIZE);
		addJDButtonWidgetClip(labelWidget);
		addChild(labelWidget);
		
		/*
		 * Add Options
		 */
		
		optionWidgets = new LabelWidget[options.length];
		
		for (int i = 0; i < optionWidgets.length; i++) {
			int index = i;
			
			float optionX = WIDGET_CLIP_X_PADDING;
			float optionY = labelWidget.getY() + HEIGHT + (i * HEIGHT);
			
			LabelWidget optionWidget = new LabelWidget(optionX, optionY, TOOLBAR_TEXT_FILL.copy().alpha(0f), options[i], font, FontStyle.LIGHT, TOOLBAR_FONT_SIZE);
			optionWidget.setInputEnabled(false);
			
			optionWidget.setOnMouseButtonEvent(e -> {
				if (optionWidget.isPointWithinThisWidget(e.getMouseX(), e.getMouseY()) && e.isPressed()) {
					optionSelected(options[index], index);	
				}
			});
			
			optionWidgets[i] = optionWidget;
			addChild(optionWidget);
		}
		
		/*
		 * Dropdown Expansion
		 */
		
		setOnMouseMotionEvent(e -> {
			boolean bHighlighted = highlighted;
			highlighted = isPointWithinThisWidget(e.getMouseX(), e.getMouseY());
			
			if (!bHighlighted && highlighted) {
				expand();
			}
			
			if (bHighlighted && !highlighted) {
				collapse();
			}
		});
	}
	
	protected abstract void optionSelected(String option, int index);
	
	private void expand() {
		//Expand the dropdown
		float expandedHeight = HEIGHT + (HEIGHT * optionWidgets.length);
		new DropdownSizeTransition(200, WIDTH, expandedHeight).play();
		
		//Fade in the options
		resetFillTransitions();
		
		for (int i = 0; i < optionWidgets.length; i++) {
			int index = i;
			
			FillTransition f = new FillTransition(OPTION_FADE_TIME, optionWidgets[i].getFill(), TOOLBAR_TEXT_FILL);
			
			f.setOnCompleted(c -> {
				optionWidgets[index].setInputEnabled(true);
			});
			
			f.play();
		}
	}
	
	private void collapse() {
		//Collapse the dropdown
		new DropdownSizeTransition(200, WIDTH, HEIGHT).play();
		
		//Fade out the options
		resetFillTransitions();
		
		for (int i = 0; i < optionWidgets.length; i++) {
			optionWidgets[i].setInputEnabled(false);
			FillTransition f = new FillTransition(OPTION_FADE_TIME, optionWidgets[i].getFill(), TOOLBAR_TEXT_FILL.copy().alpha(0f));
			f.play();
		}
	}
	
	private void resetFillTransitions() {
		if (fillTransitions != null) {
			for (int i = 0; i < fillTransitions.length; i++) {
				
				if (fillTransitions[i] == null) {
					continue;
				}
				
				fillTransitions[i].stop();
				fillTransitions[i] = null;
			}
		} else {
			fillTransitions = new FillTransition[optionWidgets.length];
		}
	}
	
	private class DropdownSizeTransition extends SizeTransition {
		public DropdownSizeTransition(long durationInMillis, float targetWidth, float targetHeight) {
			super(durationInMillis, targetWidth, targetHeight);
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
			CDDropdownMenu.this.setWidth(width);
		}

		@Override
		protected void setHeight(float height) {
			CDDropdownMenu.this.setHeight(height);
		}
	}
}
