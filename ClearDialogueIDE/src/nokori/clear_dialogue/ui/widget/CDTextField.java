package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueWidgetAssembly.*;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.font.Font;
import nokori.clear.vg.widget.RectangleWidget;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.vg.widget.text.TextFieldWidget;

public abstract class CDTextField extends CDButton {

	public static final int WIDTH = 500;
	
	public CDTextField(float x, float y, Font font, String text) {
		super(x, y, WIDTH);
		
		TextFieldWidget field = new TextFieldWidget(getWidth() - (WIDGET_CLIP_X_PADDING * 3), getHeight() - 20, TOOLBAR_TEXT_FILL, text, font, TOOLBAR_FONT_SIZE);
		addJDButtonWidgetClip(field);
		addChild(field);
		
		field.setOnKeyEvent(e -> {
			textChanged(field.getTextBuilder().toString());
		});
		
		RectangleWidget underline = new RectangleWidget(WIDTH - 20f, 1, ClearColor.WHITE_SMOKE);
		underline.addChild(new WidgetClip(WidgetClip.Alignment.BOTTOM_CENTER, 0, -8));
		addChild(underline);
	}
	
	protected abstract void textChanged(String newText);
}
