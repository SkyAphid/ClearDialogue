package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueWidgetAssembly.*;

import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.font.Font;
import nokori.clear.vg.font.FontStyle;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.transition.SizeTransition;
import nokori.clear.vg.widget.LabelWidget;
import nokori.clear.vg.widget.RectangleWidget;
import nokori.clear.windows.Cursor;
import nokori.clear.windows.Window;

public abstract class DropdownMenuWidget extends ButtonWidget {
	
	public static final int WIDTH = 200;

	/*
	 * Options
	 */
	
	private static final int OPTION_FADE_TIME = 200;
	private LabelWidget[] optionLabels;
	private RectangleWidget[] optionHighlights;

	public DropdownMenuWidget(float x, float y, Font font, String label, String options[]) {
		super(x, y, WIDTH);

		/*
		 * Create Label
		 */
		
		LabelWidget labelWidget = new LabelWidget(TOOLBAR_TEXT_FILL.copy(), label, font, FontStyle.REGULAR, TOOLBAR_FONT_SIZE);
		addJDButtonWidgetClip(labelWidget);
		addChild(labelWidget);
		
		/*
		 * Add Options
		 */
		
		optionLabels = new LabelWidget[options.length];
		optionHighlights = new RectangleWidget[options.length];
		
		for (int i = 0; i < optionLabels.length; i++) {
			int index = i;
			
			float optionX = WIDGET_CLIP_X_PADDING;
			float optionY = HEIGHT + (i * HEIGHT);
			
			/*
			 * Option Highlight
			 */
			
			RectangleWidget optionHighlight = new RectangleWidget(0, optionY, WIDTH, HEIGHT, HIGHLIGHT_COLOR.alpha(0f));
			optionHighlight.setInputEnabled(false);
			
			optionHighlight.setOnMouseButtonEvent(e -> {
				if (ClearStaticResources.canFocus(this)) {
					if (optionHighlight.isMouseWithinThisWidget() && !e.isPressed()) {
						optionSelected(options[index], index);	
					}
				}
			});
			
			optionHighlight.setOnMouseEnteredEvent(e -> {
				if (ClearStaticResources.canFocus(this)) {
					FillTransition fadeIn = new FillTransition(200, optionHighlight.getFill(), HIGHLIGHT_COLOR.alpha(0.25f));
					fadeIn.setLinkedObject(optionHighlight);
					fadeIn.play();
					
					ClearStaticResources.getCursor(Cursor.Type.HAND).apply(e.getWindow());
					ClearStaticResources.setFocusedWidget(this);
				}
			});
			
			optionHighlight.setOnMouseExitedEvent(e -> {
				FillTransition fadeOut = new FillTransition(200, optionHighlight.getFill(), HIGHLIGHT_COLOR.alpha(0f));
				fadeOut.setLinkedObject(optionHighlight);
				fadeOut.play();
				
				//The event check is for instances where the exited event is manually fired.
				if (e != null) {
					resetCursor(e.getWindow());
				}
				
				if (ClearStaticResources.isFocused(this)) {
					ClearStaticResources.setFocusedWidget(null);
				}
			});
			
			optionHighlights[i] = optionHighlight;
			
			/*
			 * Option Label
			 */
			
			LabelWidget optionLabel = new LabelWidget(optionX, labelWidget.getY() + (optionY + WIDGET_CLIP_Y_PADDING), 
					TOOLBAR_TEXT_FILL.alpha(0f), options[i], font, FontStyle.LIGHT, TOOLBAR_FONT_SIZE);
			
			optionLabels[i] = optionLabel;
			
			/*
			 * Add to Widget
			 */
			
			addChild(optionHighlights[i]);
			addChild(optionLabel);
		}
		
		/*
		 * Dropdown Expansion
		 */
		
		setOnInternalMouseEnteredEvent(e -> {
			if (ClearStaticResources.canFocus(this)) {
				expand();
			}
		});
		
		setOnInternalMouseExitedEvent(e -> {
			collapse(e.getWindow());
		});
	}
	
	protected abstract void optionSelected(String option, int index);
	
	private void resetCursor(Window window) {
		//Reset the mouse cursor on exit, if none of the options are highlighted
		
		for (int j = 0; j < optionHighlights.length; j++) {
			if (optionHighlights[j].isMouseWithinThisWidget()) {
				return;
			}
		}
		
		if (!ClearStaticResources.isHoveringWidget()) {
			ClearStaticResources.getCursor(Cursor.Type.ARROW).apply(window);
		}
	}
	
	private void expand() {
		//Expand the dropdown
		float expandedHeight = HEIGHT + (HEIGHT * optionLabels.length);
		new DropdownSizeTransition(200, WIDTH, expandedHeight).play();
		
		for (int i = 0; i < optionLabels.length; i++) {
			int index = i;
			
			FillTransition expand = new FillTransition(OPTION_FADE_TIME, optionLabels[i].getFill(), TOOLBAR_TEXT_FILL);
			expand.setLinkedObject(optionLabels[i]);
			
			expand.setOnCompleted(c -> {
				optionHighlights[index].setInputEnabled(true);
			});
			
			expand.play();
		}
	}
	
	private void collapse(Window window) {
		//Collapse the dropdown
		new DropdownSizeTransition(200, WIDTH, HEIGHT).play();
		
		//Fade out the options
		for (int i = 0; i < optionLabels.length; i++) {
			RectangleWidget h = optionHighlights[i];
			h.setInputEnabled(false);
			
			if (h.isMouseWithinThisWidget()) {
				h.getMouseExitedEventListener().listen(null);
				h.resetIsMouseWithin();
			}
			
			resetCursor(window);
			
			FillTransition collapse = new FillTransition(OPTION_FADE_TIME, optionLabels[i].getFill(), TOOLBAR_TEXT_FILL.copy().alpha(0f));
			collapse.setLinkedObject(optionLabels[i]);
			collapse.play();
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
			DropdownMenuWidget.this.setWidth(width);
		}

		@Override
		protected void setHeight(float height) {
			DropdownMenuWidget.this.setHeight(height);
		}
	}
}
