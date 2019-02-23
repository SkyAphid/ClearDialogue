package nokori.jdialogue.ui.components.dropdown;

import nokori.jdialogue.ui.SharedResources;

public class ContextDropdownMenu extends DropdownMenu {

	private SharedResources sharedResources;
	
	private static final int CONTEXT_WIDTH = 125;
	private static final int CONTEXT_HEIGHT = 25;
	
	public ContextDropdownMenu(SharedResources sharedResources, DropdownOption...options) {
		super(-1, -1, CONTEXT_WIDTH, CONTEXT_HEIGHT, sharedResources.getTheme().getSansFont(), 16, "", 20 * options.length, options);
		this.sharedResources = sharedResources;
		controlsEnabled = false;
	}
	
	@Override
	protected void optionSelectedCallback(DropdownOption o) {
		hide();
	}
	
	public void show(double x, double y) {
		if (!isActive()) {
			setAbsolutePosition(x, y);
			forceHeight(DEFAULT_HEIGHT);
			setExpanded(true);
			sharedResources.getCanvasPane().getChildren().add(this);
		}
	}
	
	public void hide() {
		setExpanded(false);
		sharedResources.getCanvasPane().getChildren().remove(this);
	}
	
	public boolean isActive() {
		return sharedResources.getCanvasPane().getChildren().contains(this);
	}
	
	public boolean isMouseHoveringThis() {
		return (cached_context.isMouseInside(this));
	}
}
