package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.font.Font;
import nokori.clear.vg.widget.text.TextFieldWidget;
import nokori.clear_dialogue.ui.SharedResources;

public class ProjectNameTextFieldWidget extends ButtonWidget {

	public static final int WIDTH = 500;

	private SharedResources sharedResources;
	private TextFieldWidget field;
	
	public ProjectNameTextFieldWidget(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(3), WIDGET_PADDING, WIDTH);
		this.sharedResources = sharedResources;
		
		Font font = sharedResources.getNotoSans();

		field = new TextFieldWidget(getWidth() - (WIDGET_CLIP_X_PADDING * 3), TOOLBAR_TEXT_FILL, sharedResources.getProject().getName(), font, TOOLBAR_FONT_SIZE);
		field.setHighlightFill(HIGHLIGHT_COLOR);
		field.setUnderlineFill(ClearColor.WHITE_SMOKE);
		
		addJDButtonWidgetClip(field);
		addChild(field);
		
		field.setOnKeyEvent(e -> {
			sharedResources.getProject().setName(field.getTextBuilder().toString());
		});
	}
	
	/**
	 * Resets this text field to the project name. Called this if the Project is changed during runtime.
	 */
	public void refresh() {
		field.setText(sharedResources.getProject().getName());
	}
}
