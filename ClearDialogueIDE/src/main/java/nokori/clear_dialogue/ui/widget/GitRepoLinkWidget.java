package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;
import static nokori.clear_dialogue.ui.ClearDialogueTheme.*;

import java.io.IOException;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.font.FontStyle;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.widget.LabelWidget;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.windows.util.ClearUtil;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear.windows.util.TinyFileDialog.Icon;
import nokori.clear_dialogue.ui.ClearDialogueIDECore;
import nokori.clear_dialogue.ui.SharedResources;

import nokori.clear.windows.Cursor.Type;

public class GitRepoLinkWidget extends LabelWidget {
	
	public GitRepoLinkWidget(SharedResources sharedResources) {
		super(CONTEXT_HINTS_TEXT_FILL.copy(), ClearDialogueIDECore.PROGRAM_INFORMATION, sharedResources.getNotoSans(), FontStyle.LIGHT, CONTEXT_HINTS_FONT_SIZE);
		addChild(new WidgetClip(WidgetClip.Alignment.BOTTOM_LEFT, WIDGET_PADDING, -WIDGET_PADDING));
		
		setOnMouseEnteredEvent(e -> {
			new FillTransition(TRANSITION_DURATION, getFill(), ClearColor.CORAL).play();
			ClearStaticResources.getCursor(Type.HAND).apply(e.getWindow());
		});
		
		setOnMouseExitedEvent(e -> {
			new FillTransition(TRANSITION_DURATION, getFill(), CONTEXT_HINTS_TEXT_FILL).play();
			ClearStaticResources.getCursor(Type.ARROW).apply(e.getWindow());
		});
		
		setOnMouseButtonEvent(e -> {
			if (isMouseWithin() && !e.isPressed()) {
				String projectRepoURL = "https://github.com/SkyAphid/ClearDialogue";
				
				try {
					ClearUtil.openURLInBrowser(projectRepoURL);
				} catch (IOException e1) {
					TinyFileDialog.showMessageDialog(ClearDialogueIDECore.PROGRAM_NAME + " About", ClearDialogueIDECore.PROGRAM_INFORMATION + "\n" + projectRepoURL, Icon.INFORMATION);
					e1.printStackTrace();
				}
			}
		});
	}

}
