package nokori.jdialogue.ui.components.dropdown;

import lwjgui.Color;
import lwjgui.event.Event;
import lwjgui.font.Font;
import lwjgui.font.FontStyle;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.scene.control.Label;
import lwjgui.scene.shape.Rectangle;
import lwjgui.theme.Theme;
import lwjgui.transition.FillTransition;
import lwjgui.transition.SizeTransition;
import nokori.jdialogue.ui.components.Button;

/**
 * This is the general-use class for all buttons that expand when you hover over them on the "toolbar" interface of the UI (File, Tool, etc)
 */
public class DropdownMenu extends Button {
	
	public static final int DEFAULT_WIDTH = 200;
	
	private int expandSpeed;
	private boolean expanded = false;
	protected boolean controlsEnabled = true;
	
	private String title;
	private int fontSize;

	private int optionWidth, optionHeight;
	private InternalOption[] internalOptions;
	private boolean optionsActive = false;
	
	private HeightTransition heightTransition = null;

	public DropdownMenu(int absoluteX, int absoluteY, Font font, String title, DropdownOption...options) {
		this(absoluteX, absoluteY, DEFAULT_WIDTH, DEFAULT_HEIGHT, font, FONT_SIZE, title, (40 * options.length), options);
	}

	public DropdownMenu(int absoluteX, int absoluteY, int optionWidth, int optionHeight, Font font, int fontSize, String title, int expandSpeed, DropdownOption...options) {
		super(absoluteX, absoluteY, optionWidth, optionHeight, true, true);
		this.optionWidth = optionWidth;
		this.optionHeight = optionHeight;
		this.title = title;
		this.expandSpeed = expandSpeed;
		this.fontSize = fontSize;
		
		setAlignment(Pos.TOP_LEFT);
		
		//Add button title
		getChildren().add(buildLabel(font, title, new Insets(PADDING, 0, 0, PADDING)));
		
		//Add menu options
		internalOptions = new InternalOption[options.length];
		
		for (int i = 0; i < internalOptions.length; i++) {
			internalOptions[i] = new InternalOption(this, i, font, options[i]);
		}
	}

	/**
	 * Gets a Label object configured for this component specifically.
	 */
	private Label buildLabel(Font font, String text, Insets padding) {
		Label label = new Label(text);
		label.setTextFill(Theme.current().getTextAlt().copy());
		label.setFont(font);
		label.setFontSize(fontSize);
		label.setMouseTransparent(true);
		label.setPadding(padding);
		
		return label;
	}

	/*
	 * 
	 * 
	 * Options controls
	 * 
	 * 
	 */
	
	private void addOptions() {
		for (int i = 0; i < internalOptions.length; i++) {
			new FillTransition(200, Color.TRANSPARENT, Theme.current().getTextAlt(), internalOptions[i].fill).play();
		}
		
		getChildren().addAll(internalOptions);
		optionsActive = true;
	}
	
	private void removeOptions() {
		getChildren().removeAll(internalOptions);
		optionsActive = false;
	}
	
	public boolean isOptionHighlighted() {
		if (optionsActive) {
			for (int i = 0; i < internalOptions.length; i++) {
				if (internalOptions[i].isHighlighting()) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Called when an option in this dropdown is clicked.
	 * 
	 * @param o - the option selected
	 */
	protected void childOptionSelected(DropdownOption o) {
		
	}
	
	/*
	 * 
	 * 
	 * Menu Expansion controls
	 * 
	 */

	/**
	 * Expand the menu if a mouse enters
	 */
	@Override
	protected void mouseEntered(Event e) {
		if (controlsEnabled) {
			setExpanded(true);
		}
	}
	
	/**
	 * Contract the menu if a mouse exits. We check this in the position() function so that it's checked concurrently.
	 */
	@Override
	public void position(Node parent) {
		super.position(parent);
		
		if (controlsEnabled) {
			//Reset the menu if it or its children aren't highlighted
			if (!isHighlighting() && !isOptionHighlighted()) {
				setExpanded(false);
			}
		}
	}

	/**
	 * Sets the expanded status of this dropdown menu. Animations only play if the state has changed.
	 * 
	 * @param expanded
	 */
	protected void setExpanded(boolean expanded) {
		if (this.expanded != expanded) {
			if (expanded) {
				playHeightTransition(new HeightTransition(expandSpeed, this, getTargetHeight(internalOptions.length)));
			} else {
				removeOptions();
				playHeightTransition(new HeightTransition(expandSpeed, this, optionHeight));
			}
		}
		
		this.expanded = expanded;
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
	
	private double getTargetHeight(int index) {
		int baseY = (title != null && !title.isEmpty()) ? optionHeight : 0;
		return baseY + (optionHeight * index);
	}
	
	/*
	 * 
	 * 
	 * Custom classes 
	 * 
	 * 
	 */

	private class InternalOption extends Button {
		
		private DropdownMenu parent;
		private int index;
		
		private Color fill;
		private Label label;
		private Rectangle divider;
		
		
		public InternalOption(DropdownMenu parent, int index, Font font, DropdownOption option) {
			super(-1, -1, parent.optionWidth, parent.optionHeight, false, option.highlightable);
			this.parent = parent;
			this.index = index;
			
			fill = Theme.current().getTextAlt().copy();
			
			if (option.name != null) {
				label = buildLabel(font, option.name, new Insets(0, 0, 0, PADDING));
				label.setFontStyle(FontStyle.LIGHT);
				label.setTextFill(fill);
				getChildren().add(label);
			}
			
			if (option instanceof DropdownDivider) {
				setMouseTransparent(true);
				divider = new Rectangle((int) parent.optionWidth, 1, fill);
				setPadding(new Insets(parent.optionHeight/2, 0, 0, 0));
				getChildren().add(divider);
			}
			
			setOnMouseClicked(e -> {
				parent.childOptionSelected(option);
				option.h.select(e);
			});
			
			pos();
		}
		
		private void pos() {
			double absoluteX = parent.getX();
			double absoluteY = parent.getY() + getTargetHeight(index);
			
			setAbsolutePosition(absoluteX, absoluteY);
		}
		
		@Override
		public void position(Node parent) {
			pos();
			super.position(parent);
		}
	}
	
	private class HeightTransition extends SizeTransition {
		private DropdownMenu button;
		
		public HeightTransition(long durationInMillis, DropdownMenu button, double targetHeight) {
			super(durationInMillis, -1, targetHeight);
			this.button = button;
		}

		@Override
		public void completedCallback() {
			if (button.expanded) {
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
