package nokori.jdialogue.ui.components;

import lwjgui.font.Font;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.control.text_input.TextInputScrollPane;
import lwjgui.scene.control.text_input.TextField;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;
import nokori.jdialogue.ui.SharedResources;

public class ProjectNameField extends Button {

	public static final int DEFAULT_WIDTH = 500;
	
	public ProjectNameField(SharedResources sharedResources, int absoluteX, int absoluteY, Font font) {
		super(absoluteX, absoluteY, DEFAULT_WIDTH, DEFAULT_HEIGHT, true, false);
		
		Insets padding = new Insets(0, 0, 0, PADDING);
		
		//container pane
		StackPane textFieldContainerPane = new StackPane();
		textFieldContainerPane.setPadding(padding);
		textFieldContainerPane.setBackground(null);
		textFieldContainerPane.setAlignment(Pos.CENTER);
		
		//Internal scroll pane
		TextInputScrollPane internalScrollPane = new TextInputScrollPane();
		internalScrollPane.setBackground(null);
		internalScrollPane.setOutlineEnabled(false);
		
		//Text field
		TextField textField = new TextField(internalScrollPane, sharedResources.getProject().getName());
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
			sharedResources.getProject().setName(textField.getText());
		});
		
		textFieldContainerPane.getChildren().add(textField);
		
		getChildren().add(textFieldContainerPane);
	}
}
