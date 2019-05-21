package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;
import java.io.File;

import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.font.FontStyle;
import nokori.clear.vg.widget.LabelWidget;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear_dialogue.ui.SharedResources;

public class ProjectFileLocationLabel extends LabelWidget {
	
	private SharedResources sharedResources;
	private File currentFileLocation = null;
	
	public ProjectFileLocationLabel(SharedResources sharedResources) {
		super(CONTEXT_HINTS_TEXT_FILL.copy(), "", sharedResources.getNotoSans(), FontStyle.LIGHT, CONTEXT_HINTS_FONT_SIZE);
		addChild(new WidgetClip(WidgetClip.Alignment.BOTTOM_LEFT, WIDGET_PADDING, -WIDGET_PADDING * 4f));
		this.sharedResources = sharedResources;
	}
	
	@Override
	public void tick(NanoVGContext context, WidgetAssembly rootWidgetAssembly) {
		super.tick(context, rootWidgetAssembly);
		
		if (sharedResources.getProjectFileLocation() != currentFileLocation) {
			currentFileLocation = sharedResources.getProjectFileLocation();
			setText(context, (currentFileLocation != null ? currentFileLocation.getAbsolutePath() : ""));
		}
	}
}
