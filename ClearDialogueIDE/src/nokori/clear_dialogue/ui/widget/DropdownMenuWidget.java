package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;
import static nokori.clear_dialogue.ui.ClearDialogueTheme.*;

import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.font.Font;
import nokori.clear.vg.font.FontStyle;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.transition.WidgetSizeTransition;
import nokori.clear.vg.widget.LabelWidget;
import nokori.clear.vg.widget.RectangleWidget;
import nokori.clear.windows.Cursor;
import nokori.clear.windows.Window;

public abstract class DropdownMenuWidget extends ButtonWidget {
	
	public static final int WIDTH = 200;

	/*
	 * Options
	 */
	
	private boolean expanded = false;
	private LabelWidget[] optionLabels;
	private RectangleWidget[] optionHighlights;
	private boolean[] optionPressed;

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
		optionPressed = new boolean[options.length];
		
		for (int i = 0; i < optionLabels.length; i++) {
			int index = i;
			
			float optionX = WIDGET_CLIP_X_PADDING;
			float optionY = HEIGHT + (i * HEIGHT);
			
			/*
			 * Option Highlight
			 */
			
			RectangleWidget optionHighlight = new RectangleWidget(0, optionY, WIDTH, HEIGHT, HIGHLIGHT_COLOR.alpha(0f));
			
			optionHighlight.setOnMouseButtonEvent(e -> {
				//System.err.println(ClearStaticResources.getFocusedWidget() + " " + e.isPressed());
				
				if (ClearStaticResources.isFocusedOrCanFocus(DropdownMenuWidget.this) && expanded) {
					boolean mouseWithin = optionHighlight.isMouseWithin();
					
					setOptionPressedHighlight(index, mouseWithin && e.isPressed());
					
					if (mouseWithin && !e.isPressed()) {
						e.setConsumed(true);
						optionSelected(e.getWindow(), options[index], index);
					}
				}
			});
			
			optionHighlight.setOnMouseEnteredEvent(e -> {
				//On enter, focus on the widget if possible. This notifies the system to highlight it and change the cursor.
				if (expanded) {
					fadeHighlight(optionHighlight, 0.25f);
					ClearStaticResources.getCursor(Cursor.Type.HAND).apply(e.getWindow());
				}
			});
			
			optionHighlight.setOnMouseExitedEvent(e -> {
				fadeHighlight(optionHighlight, 0.0f);
				
				//The event check is for instances where the exited event is manually fired.
				if (e != null) {
					resetCursor(e.getWindow());
				}
				
				//Set option pressed to false on all options
				for (int j = 0; j < optionPressed.length; j++) {
					optionPressed[j] = false;
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
			if (ClearStaticResources.isFocusedOrCanFocus(this)) {
				expand();
			}
		});
		
		setOnInternalMouseExitedEvent(e -> {
			collapse(e.getWindow());
		});
	}
	
	private void setOptionPressedHighlight(int index, boolean pressed) {
		boolean bPressed = optionPressed[index];
		optionPressed[index] = pressed;
		
		if (pressed) {
			//Select
			fadeHighlight(optionHighlights[index], 1.0f);
		} else if (bPressed && !pressed){
			if (optionHighlights[index].isMouseWithin()) {
				//Fade into highlight
				fadeHighlight(optionHighlights[index], 0.25f);
			} else {
				//Fade out
				fadeHighlight(optionHighlights[index], 0.0f);
			}
		}
	}
	
	/**
	 * Fades the given widget's alpha to the "highlighted" parameter, which is 0.25
	 */
	private void fadeHighlight(RectangleWidget optionHighlight, float value) {
		FillTransition fadeIn = new FillTransition(TRANSITION_DURATION, optionHighlight.getFill(), HIGHLIGHT_COLOR.alpha(value));
		fadeIn.setLinkedObject(optionHighlight);
		fadeIn.play();
	}
	
	/**
	 * This is the callback for when an option is selected from the dropdown menu
	 * @param window TODO
	 * @param option - the name of the option selected
	 * @param index - the index of the option
	 */
	protected abstract void optionSelected(Window window, String option, int index);
	
	private void resetCursor(Window window) {
		//Reset the mouse cursor on exit, if none of the options are highlighted
		
		for (int j = 0; j < optionHighlights.length; j++) {
			if (optionHighlights[j].isMouseWithin()) {
				return;
			}
		}
		
		if (ClearStaticResources.isFocusedOrCanFocus(this)) {
			ClearStaticResources.getCursor(Cursor.Type.ARROW).apply(window);
		}
	}
	
	/**
	 * This activates the animation for expanding the dropdown menu and enables all of the option inputs
	 */
	public void expand() {
		//Expand the dropdown
		float expandedHeight = (HEIGHT * 1.5f) + (HEIGHT * optionLabels.length);
		new WidgetSizeTransition(this, TRANSITION_DURATION, WIDTH, expandedHeight).play();
		
		for (int i = 0; i < optionLabels.length; i++) {
			FillTransition expand = new FillTransition(TRANSITION_DURATION, optionLabels[i].getFill(), TOOLBAR_TEXT_FILL);
			expand.setLinkedObject(optionLabels[i]);
			
			expand.play();
		}
		
		expanded = true;
		
		ClearStaticResources.setFocusedWidget(this);
	}
	
	/**
	 * This activates the animation for collapsing the dropdown and disables all of the option inputs
	 * 
	 * @param window
	 */
	public void collapse(Window window) {
		//Collapse the dropdown
		new WidgetSizeTransition(this, TRANSITION_DURATION, WIDTH, HEIGHT).play();
		
		//Fade out the options
		for (int i = 0; i < optionLabels.length; i++) {
			RectangleWidget h = optionHighlights[i];

			if (h.isMouseWithin()) {
				h.getMouseExitedEventListener().listen(null);
				h.resetMouseWithin();
			}
			
			resetCursor(window);
			
			FillTransition collapse = new FillTransition(TRANSITION_DURATION, optionLabels[i].getFill(), TOOLBAR_TEXT_FILL.copy().alpha(0f));
			collapse.setLinkedObject(optionLabels[i]);
			collapse.play();
		}
		
		expanded = false;
		
		ClearStaticResources.clearFocusIfApplicable(this);
	}
}
