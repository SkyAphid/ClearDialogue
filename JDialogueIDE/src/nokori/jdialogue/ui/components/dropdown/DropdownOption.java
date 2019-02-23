package nokori.jdialogue.ui.components.dropdown;

import lwjgui.event.MouseEvent;

public class DropdownOption {
	protected String name;
	protected OptionSelectHandler optionSelectHandler;
	protected boolean highlightable = true;

	public DropdownOption(String name, OptionSelectHandler optionSelectHandler) {
		this.name = name;
		this.optionSelectHandler = optionSelectHandler;
	};

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OptionSelectHandler getOptionSelectHandler() {
		return optionSelectHandler;
	}

	public void setOptionSelectHandler(OptionSelectHandler optionSelectHandler) {
		this.optionSelectHandler = optionSelectHandler;
	}

	public boolean isHighlightable() {
		return highlightable;
	}

	public void setHighlightable(boolean highlightable) {
		this.highlightable = highlightable;
	}

	public interface OptionSelectHandler {
		public void select(MouseEvent e);
	}
	
}