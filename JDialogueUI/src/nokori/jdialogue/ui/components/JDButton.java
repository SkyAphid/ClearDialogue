package nokori.jdialogue.ui.components;


import lwjgui.Color;
import lwjgui.geometry.Insets;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.Font;

/**
 * This is the general-use class for all buttons that expand when you hover over them on the "toolbar" interface of the UI (File, Tool, etc)
 */
public class JDButton extends JDButtonSkeleton {
	
	public static final int DEFAULT_WIDTH = 200;
	public static final int DEFAULT_HEIGHT = 50;

	private String text;
	private Label label;

	public JDButton(String text, Font font, Color backgroundFill, Color textFill) {
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT, backgroundFill);
		this.text = text;
		
		label = new Label(text);
		label.setTextFill(textFill);
		label.setFont(font);
		label.setFontSize(26);
		label.setPadding(new Insets(10, 0, 0, 20));
		label.setMouseTransparent(true);
		
		getChildren().add(label);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
