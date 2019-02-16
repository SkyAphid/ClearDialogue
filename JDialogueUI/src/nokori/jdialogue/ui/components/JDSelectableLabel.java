package nokori.jdialogue.ui.components;

import lwjgui.event.Event;
import lwjgui.scene.control.Label;
import lwjgui.theme.Theme;
import nokori.jdialogue.ui.transitions.LabelFillTransition;

public class JDSelectableLabel extends Label {
	
	private static final int HIGHLIGHT_SPEED_IN_MILLIS = 200;
	
	private boolean clickingEnabled = true;
	
	public JDSelectableLabel(String text) {
		super(text);
		
		setOnMouseEntered(e -> {
			if (clickingEnabled) {
				new LabelFillTransition(HIGHLIGHT_SPEED_IN_MILLIS, this, getTextFill(), Theme.current().getSelection()).play();
			}
		});
		
		setOnMouseExited(e -> {
			if (clickingEnabled) {
				new LabelFillTransition(HIGHLIGHT_SPEED_IN_MILLIS, this, getTextFill(), Theme.current().getText()).play();
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
