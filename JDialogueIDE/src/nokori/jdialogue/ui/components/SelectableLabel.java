package nokori.jdialogue.ui.components;

import lwjgui.event.Event;
import lwjgui.scene.control.Label;
import lwjgui.theme.Theme;
import lwjgui.transition.FillTransition;

public class SelectableLabel extends Label {
	
	private static final int HIGHLIGHT_SPEED_IN_MILLIS = 200;
	
	private boolean clickingEnabled = true;
	
	public SelectableLabel(String text) {
		super(text);
		setTextFill(Theme.current().getText().copy());
		
		setOnMouseEntered(e -> {
			System.err.println("Label entered");
			if (clickingEnabled) {
				new FillTransition(HIGHLIGHT_SPEED_IN_MILLIS, getTextFill(), Theme.current().getControl()).play();
			}
		});
		
		setOnMouseExited(e -> {
			System.err.println("Label exited");
			if (clickingEnabled) {
				new FillTransition(HIGHLIGHT_SPEED_IN_MILLIS, getTextFill(), Theme.current().getText()).play();
			}
		});
		
		setOnMouseClicked(e -> {
			if (clickingEnabled) {
				mouseClicked(e);
			}
		});
	}
	
	protected void mouseClicked(Event e) {
		
	}

	public boolean isClickingEnabled() {
		return clickingEnabled;
	}

	public void setClickingEnabled(boolean clickingEnabled) {
		this.clickingEnabled = clickingEnabled;
	}
}
