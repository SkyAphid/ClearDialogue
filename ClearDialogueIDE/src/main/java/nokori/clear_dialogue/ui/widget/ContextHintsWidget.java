package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;
import static nokori.clear_dialogue.ui.ClearDialogueTheme.*;

import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.font.FontStyle;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.widget.LabelWidget;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear_dialogue.ui.SharedResources;

public class ContextHintsWidget extends LabelWidget {
	
	public static final int CONTEXT_HINT_X_OFFSET = 400;
	
	private SharedResources sharedResources;
	
	public ContextHintsWidget(SharedResources sharedResources) {
		super(Float.MAX_VALUE, CONTEXT_HINTS_TEXT_FILL.copy(), sharedResources.getContextHint(), sharedResources.getNotoSans(), FontStyle.LIGHT, CONTEXT_HINTS_FONT_SIZE);
		this.sharedResources = sharedResources;
		addChild(new WidgetClip(WidgetClip.Alignment.BOTTOM_LEFT, WIDGET_PADDING + CONTEXT_HINT_X_OFFSET, -WIDGET_PADDING));
	}

	@Override
	public void tick(NanoVGContext context, WidgetAssembly rootWidgetAssembly) {
		super.tick(context, rootWidgetAssembly);
		
		String contextHint = sharedResources.getContextHint();
		
		if (!getText().equals(contextHint)) {
			getFill().alpha(0f);
			setText(context, sharedResources.getContextHint());
			
			FillTransition t = new FillTransition(TRANSITION_DURATION, getFill(), CONTEXT_HINTS_TEXT_FILL);
			t.setLinkedObject(this);
			t.play();
		}
	}
}
