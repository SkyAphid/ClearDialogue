package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueWidgetAssembly.*;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.font.Font;
import nokori.clear.vg.widget.RectangleWidget;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.vg.widget.text.TextFieldWidget;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.SharedResources;

public class ProjectNameTextFieldWidget extends ButtonWidget {

	public static final int WIDTH = 500;
	
	public ProjectNameTextFieldWidget(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(3), WIDGET_PADDING, WIDTH);
		
		Project project = sharedResources.getProject();
		Font font = sharedResources.getNotoSans();

		TextFieldWidget field = new TextFieldWidget(getWidth() - (WIDGET_CLIP_X_PADDING * 3), getHeight() - 20, TOOLBAR_TEXT_FILL, project.getName(), font, TOOLBAR_FONT_SIZE);
		field.setHighlightFill(HIGHLIGHT_COLOR);
		addJDButtonWidgetClip(field);
		addChild(field);
		
		field.setOnKeyEvent(e -> {
			project.setName(field.getTextBuilder().toString());
		});
		
		RectangleWidget underline = new RectangleWidget(WIDTH - 20f, 1, ClearColor.WHITE_SMOKE);
		underline.addChild(new WidgetClip(WidgetClip.Alignment.BOTTOM_CENTER, 0, -8));
		addChild(underline);
	}
}
