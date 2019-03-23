package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueWidgetAssembly.*;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.font.FontStyle;
import nokori.clear.vg.widget.LabelWidget;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear_dialogue.ui.ClearDialogueIDECore;
import nokori.clear_dialogue.ui.SharedResources;

public class CDGitRepoLink extends LabelWidget {

	private boolean highlighted = false;
	
	public CDGitRepoLink(SharedResources sharedResources) {
		super(CONTEXT_HINTS_TEXT_FILL, ClearDialogueIDECore.PROGRAM_NAME_FULL, sharedResources.getNotoSans(), FontStyle.LIGHT, CONTEXT_HINTS_FONT_SIZE);
		addChild(new WidgetClip(WidgetClip.Alignment.BOTTOM_LEFT, WIDGET_PADDING, -WIDGET_PADDING));
		

	}

}
