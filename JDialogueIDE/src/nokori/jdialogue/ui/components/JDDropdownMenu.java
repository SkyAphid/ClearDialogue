package nokori.jdialogue.ui.components;

import lwjgui.Color;
import lwjgui.event.Event;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;
import lwjgui.theme.Theme;
import lwjgui.transition.FillTransition;
import lwjgui.transition.SizeTransition;

/**
 * This is the general-use class for all buttons that expand when you hover over them on the "toolbar" interface of the UI (File, Tool, etc)
 */
public class JDDropdownMenu extends JDButtonSkeleton {
	
	public static final int DEFAULT_WIDTH = 200;
	
	private static final int EXPAND_SPEED_IN_MILLIS = 200;
	private boolean expanded = false;

	private MenuOption[] options;
	private boolean optionsActive = false;
	
	private HeightTransition heightTransition = null;

	public JDDropdownMenu(int absoluteX, int absoluteY, Font font, String title, String[] optionTitles) {
		super(absoluteX, absoluteY, DEFAULT_WIDTH, DEFAULT_HEIGHT, true, true);
		
		setAlignment(Pos.TOP_LEFT);
		
		//Add button title
		getChildren().add(buildLabel(font, title, new Insets(PADDING, 0, 0, PADDING)));
		
		//Add menu options
		options = new MenuOption[optionTitles.length];

		for (int i = 0; i < optionTitles.length; i++) {
			int incrementY = DEFAULT_HEIGHT + (DEFAULT_HEIGHT * i);
			options[i] = new MenuOption(absoluteX, absoluteY + incrementY, font, optionTitles[i]);
		}
	}

	/**
	 * Gets a Label object configured for this component specifically.
	 */
	private Label buildLabel(Font font, String text, Insets padding) {
		Label label = new Label(text);
		label.setTextFill(Theme.current().getTextAlt().copy());
		label.setFont(font);
		label.setFontSize(FONT_SIZE);
		label.setMouseTransparent(true);
		label.setPadding(padding);
		
		return label;
	}
	
	private void setHeight(double height) {
		background.setPrefHeight(height);
		dropShadow.setPrefHeight(height);
	}
	
	/*
	 * 
	 * 
	 * Options controls
	 * 
	 * 
	 */
	
	
	/**
	 * Called when an option from the dropdown is clicked. Override this to customize this Dropdown's functionality.
	 * 
	 * @param e
	 * @param option
	 */
	public void optionClicked(Event e, String option) {

	}
	
	private void addOptions() {
		for (int i = 0; i < options.length; i++) {
			new FillTransition(200, Color.TRANSPARENT, Theme.current().getTextAlt(), options[i].label.getTextFill()).play();
		}
		
		getChildren().addAll(options);
		optionsActive = true;
	}
	
	private void removeOptions() {
		getChildren().removeAll(options);
		optionsActive = false;
	}
	
	@Override
	public void render(Context context) {
		super.render(context);
		
		//Reset the menu if it or its children aren't highlighted
		if (!isHighlighted() && !isOptionHighlighted()) {
			//Remove options from dropdown
			if (optionsActive) {
				removeOptions();
			}
			
			//Shrink the menu back to its default size
			if (expanded) {
				playHeightTransition(new HeightTransition(EXPAND_SPEED_IN_MILLIS, this, DEFAULT_HEIGHT));
				expanded = false;
			}
		}
	}
	
	public boolean isOptionHighlighted() {
		if (optionsActive) {
			for (int i = 0; i < options.length; i++) {
				if (options[i].isHighlighted()) {
					return true;
				}
			}
		}
		
		return false;
	}

	/*
	 * 
	 * 
	 * Menu Expansion controls
	 * 
	 */
	
	@Override
	protected void mouseEntered(Event e) {
		if (!expanded) {
			playHeightTransition(new HeightTransition(EXPAND_SPEED_IN_MILLIS, this, DEFAULT_HEIGHT + (options.length * DEFAULT_HEIGHT)));
			expanded = true;
		}
	}
	
	/**
	 * Cancels the current height transition if there is one playing so that they don't deadlock each other.
	 */
	private void playHeightTransition(HeightTransition transition) {
		if (heightTransition != null) {
			heightTransition.stop();
			heightTransition = null;
		}
		
		heightTransition = transition;
		heightTransition.play();
	}
	
	/*
	 * 
	 * 
	 * Custom classes 
	 * 
	 * 
	 */
	
	private class MenuOption extends JDButtonSkeleton {
		
		private Label label;

		public MenuOption(int absoluteX, int absoluteY, Font font, String text) {
			super(absoluteX, absoluteY, DEFAULT_WIDTH, DEFAULT_HEIGHT, false, true);
			
			label = buildLabel(font, text, new Insets(0, 0, 0, PADDING));
			label.setFontStyle(FontStyle.LIGHT);
			getChildren().add(label);
			
			setOnMouseClicked(e -> {
				optionClicked(e, text);
			});
		}
	}
	
	private class HeightTransition extends SizeTransition {
		private JDDropdownMenu button;
		
		public HeightTransition(long durationInMillis, JDDropdownMenu button, double targetHeight) {
			super(durationInMillis, -1, targetHeight);
			this.button = button;
		}

		@Override
		public void completedCallback() {
			if (button.isHighlighted()) {
				button.addOptions();
			}
		}

		@Override
		protected double getCurrentWidth() {return 0;}

		@Override
		protected double getCurrentHeight() {
			return button.getHeight();
		}

		@Override
		protected void setWidth(double width) {}

		@Override
		protected void setHeight(double height) {
			button.setHeight(height);
		}
	}
}
