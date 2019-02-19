package nokori.jdialogue.ui.components;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.control.text_input.TextField;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;
import nokori.jdialogue.ui.JDUIController;

public class JDProjectNameField extends JDButtonSkeleton {

	public static final int DEFAULT_WIDTH = 500;
	
	public JDProjectNameField(JDUIController controller, int absoluteX, int absoluteY, Font font) {
		super(absoluteX, absoluteY, DEFAULT_WIDTH, DEFAULT_HEIGHT, true, false);
		
		Insets padding = new Insets(0, 0, 0, PADDING);
		
		StackPane textFieldContainerPane = new StackPane();
		textFieldContainerPane.setPadding(padding);
		textFieldContainerPane.setBackground(null);
		textFieldContainerPane.setAlignment(Pos.CENTER);
		
		TextField textField = new TextField(controller.getProject().getName());
		textField.setPrefWidth(DEFAULT_WIDTH - 20);
		textField.setFillToParentWidth(true);
		
		textField.setFont(font);
		textField.setFontSize(FONT_SIZE);
		textField.setFontFill(Theme.current().getTextAlt());
		
		textField.setPadding(new Insets(0, 0, 1, 0));
		textField.setPaddingColor(Theme.current().getTextAlt());

		textField.setDecorated(false);
		textField.setSelectionOutlineEnabled(false);
		textField.setBackground(null);
		textField.setCaretFill(Theme.current().getTextAlt());
		textField.setCaretFading(true);
		
		textField.setOnTextInput(e -> {
			controller.getProject().setName(textField.getText());
		});
		
		textFieldContainerPane.getChildren().add(textField);
		
		getChildren().add(textFieldContainerPane);
	}
	
}
