package nokori.jdialogue.ui.components.dropdown;

import lwjgui.event.MouseEvent;

public class DropdownOption {
	String name;
	OptionSelectHandler h;
	boolean highlightable = true;

	public DropdownOption(String name, OptionSelectHandler h) {
		this.name = name;
		this.h = h;
	};
	
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