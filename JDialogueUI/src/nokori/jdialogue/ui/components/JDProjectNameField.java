package nokori.jdialogue.ui.components;

import lwjgui.scene.control.TextField;
import lwjgui.scene.layout.Font;
import lwjgui.theme.Theme;
import nokori.jdialogue.ui.JDUIController;

public class JDProjectNameField extends JDButtonSkeleton {

	public static final int DEFAULT_WIDTH = 500;
	
	public JDProjectNameField(int absoluteX, int absoluteY, Font font, JDUIController controller) {
		super(absoluteX, absoluteY, DEFAULT_WIDTH, DEFAULT_HEIGHT, true, false);
		
		TextField textField = new TextField(controller.getProject().getName());

		textField.setFont(font);
		textField.setFontSize(FONT_SIZE);
		textField.setFontFill(Theme.currentTheme().getTextAlt());
		textField.setBackgroundEnabled(false);
		textField.setUnderlineEnabled(true);
		textField.setPadding(TEXT_PADDING);
		textField.setFillToParentWidth(true);
		textField.setPreferredColumnCount(28);
		
		getChildren().add(textField);
	}
	
}
