package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueWidgetAssembly.*;

import java.io.IOException;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.font.FontStyle;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.widget.LabelWidget;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.windows.util.ClearUtil;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear.windows.util.TinyFileDialog.Icon;
import nokori.clear_dialogue.ui.ClearDialogueIDECore;
import nokori.clear_dialogue.ui.SharedResources;

public class CDGitRepoLink extends LabelWidget {
	
	public CDGitRepoLink(SharedResources sharedResources) {
		super(CONTEXT_HINTS_TEXT_FILL.copy(), ClearDialogueIDECore.PROGRAM_INFORMATION, sharedResources.getNotoSans(), FontStyle.LIGHT, CONTEXT_HINTS_FONT_SIZE);
		addChild(new WidgetClip(WidgetClip.Alignment.BOTTOM_LEFT, WIDGET_PADDING, -WIDGET_PADDING));
		
		setOnMouseEnteredEvent(e -> {
			new FillTransition(200, getFill(), ClearColor.CORAL).play();
		});
		
		setOnMouseExitedEvent(e -> {
			new FillTransition(200, getFill(), CONTEXT_HINTS_TEXT_FILL).play();
		});
		
		setOnMouseButtonEvent(e -> {
			String projectRepoURL = "https://github.com/SkyAphid/ClearDialogue";
			
			try {
				ClearUtil.openURLInBrowser(projectRepoURL);
			} catch (IOException e1) {
				TinyFileDialog.showMessageDialog(ClearDialogueIDECore.PROGRAM_NAME + " About", ClearDialogueIDECore.PROGRAM_INFORMATION + "\n" + projectRepoURL, Icon.INFORMATION);
				e1.printStackTrace();
			}
		});
	}

}
