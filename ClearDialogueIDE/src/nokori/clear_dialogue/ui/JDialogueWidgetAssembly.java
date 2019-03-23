package nokori.clear_dialogue.ui;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear_dialogue.ui.widget.CDDropdownMenu;
import nokori.clear_dialogue.ui.widget.CDDropdownMenuFile;
import nokori.clear_dialogue.ui.widget.CDDropdownMenuNode;
import nokori.clear_dialogue.ui.widget.CDDropdownMenuTool;
import nokori.clear_dialogue.ui.widget.CDGitRepoLink;
import nokori.clear_dialogue.ui.widget.CDTextFieldProjectName;

public class JDialogueWidgetAssembly extends WidgetAssembly {
	
	public static final int WIDGET_PADDING = 10;
	
	public static final ClearColor TOOLBAR_TEXT_FILL = ClearColor.WHITE_SMOKE;
	public static final int TOOLBAR_FONT_SIZE = 28;
	
	public static final ClearColor CONTEXT_HINTS_TEXT_FILL = ClearColor.GRAY;
	public static final int CONTEXT_HINTS_FONT_SIZE = 28;
	
	
	public JDialogueWidgetAssembly() {
		super();
	}
	
	public void init(SharedResources sharedResources) {
		/*
		 * Toolbar
		 */
		
		addChild(new CDDropdownMenuFile(sharedResources));
		addChild(new CDDropdownMenuTool(sharedResources));
		addChild(new CDDropdownMenuNode(sharedResources));
		addChild(new CDTextFieldProjectName(sharedResources));
		
		/*
		 * Context Hints
		 */
		
		addChild(new CDGitRepoLink(sharedResources));
	}
	
	
	public static int getToolbarAbsoluteX(int index) {
		return WIDGET_PADDING + (CDDropdownMenu.WIDTH + WIDGET_PADDING) * index;
	}
}
