package nokori.clear_dialogue.ui.widget;

import static nokori.clear_dialogue.ui.ClearDialogueWidgetAssembly.*;

import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.SharedResources;

public class CDTextFieldProjectName extends CDTextField {

	private Project project;
	
	public CDTextFieldProjectName(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(3), WIDGET_PADDING, sharedResources.getNotoSans(), sharedResources.getProject().getName());
		this.project = sharedResources.getProject();
	}

	@Override
	protected void textChanged(String newText) {
		project.setName(newText);
	}

}
