package nokori.jdialogue.ui;

import javafx.animation.FillTransition;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * This is a class that extends Text. When you hover it, the text will glow. If you click it, the callbacks will be called.
 */
public class ClickableText extends Text {
	private static final int HIGHLIGHT_TIME = 200;
	
	private boolean clickingEnabled = true;
	
	public ClickableText(String text, Color defaultFill, Color highlightFill) {
		super(text);
		
		setFill(defaultFill);
		
		//Events
		setOnMouseEntered(event -> {
			if (clickingEnabled) {
				FillTransition fadeTransition = new FillTransition(Duration.millis(HIGHLIGHT_TIME), this);
				fadeTransition.setFromValue((Color) getFill());
				fadeTransition.setToValue(highlightFill);
				fadeTransition.play();
			}
			
			mouseEntered(event, clickingEnabled);
		});
		
		setOnMouseExited(event -> {
			if (clickingEnabled) {
				FillTransition fadeTransition = new FillTransition(Duration.millis(HIGHLIGHT_TIME), this);
				fadeTransition.setFromValue((Color) getFill());
				fadeTransition.setToValue(defaultFill);
				fadeTransition.play();
			}
			
			mouseExited(event, clickingEnabled);
		});
		
		setOnMouseClicked(event -> {
			mouseClicked(event, clickingEnabled);
		});
		
		setOnMouseMoved(event -> {
			mouseMoved(event, clickingEnabled);
		});
	}
	
	public void mouseEntered(MouseEvent event, boolean clickingEnabled) {
		
	}
	
	public void mouseExited(MouseEvent event, boolean clickingEnabled) {
		
	}
	
	public void mouseClicked(MouseEvent event, boolean clickingEnabled) {
		
	}
	
	public void mouseMoved(MouseEvent event, boolean clickingEnabled) {
		
	}

	/**
	 * Whether or not clicking is enabled (if false, this component behaves like a normal Text object and the callbacks won't be called)
	 * @return
	 */
	public boolean isClickingEnabled() {
		return clickingEnabled;
	}

	/**
	 * Set this to false to disable clicking temporarily.
	 * 
	 * @param clickingEnabled
	 */
	public void setClickingEnabled(boolean clickingEnabled) {
		this.clickingEnabled = clickingEnabled;
	}
}
