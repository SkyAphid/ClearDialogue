package nokori.clear_dialogue.ui;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetContainer;
import nokori.clear_dialogue.ui.widget.ContextHintsWidget;
import nokori.clear_dialogue.ui.widget.DropdownMenuWidget;
import nokori.clear_dialogue.ui.widget.DropdownMenuWidgetFile;
import nokori.clear_dialogue.ui.widget.DropdownMenuWidgetNode;
import nokori.clear_dialogue.ui.widget.DropdownMenuWidgetTool;
import nokori.clear_dialogue.ui.widget.GitRepoLinkWidget;
import nokori.clear_dialogue.ui.widget.ProjectNameTextFieldWidget;

public class ClearDialogueWidgetAssembly extends WidgetAssembly {
	
	public static final int WIDGET_PADDING = 10;
	
	public static final ClearColor TOOLBAR_TEXT_FILL = ClearColor.WHITE_SMOKE;
	public static final int TOOLBAR_FONT_SIZE = 28;
	
	public static final ClearColor HIGHLIGHT_COLOR = ClearColor.CORAL.multiply(1.5f).immutable(true);
	
	public static final ClearColor CONTEXT_HINTS_TEXT_FILL = ClearColor.GRAY;
	public static final int CONTEXT_HINTS_FONT_SIZE = 24;
	
	
	public ClearDialogueWidgetAssembly() {
		super(true);
	}
	
	public void init(SharedResources sharedResources) {
		initCanvas(sharedResources);
		initToolbar(sharedResources);
	}
	
	private void initCanvas(SharedResources sharedResources) {
		ClearDialogueCanvas canvas = new ClearDialogueCanvas(sharedResources);
		
		//Finalize
		addChild(canvas);
		sharedResources.setCanvas(canvas);
	}
	
	private void initToolbar(SharedResources sharedResources) {
		//We'll group the toolbar widgets together with this container.
		WidgetContainer toolbar = new WidgetContainer();
		
		//Toolbar
		toolbar.addChild(new DropdownMenuWidgetFile(sharedResources));
		toolbar.addChild(new DropdownMenuWidgetTool(sharedResources));
		toolbar.addChild(new DropdownMenuWidgetNode(sharedResources));
		toolbar.addChild(new ProjectNameTextFieldWidget(sharedResources));
		
		//Program information and context hints
		toolbar.addChild(new GitRepoLinkWidget(sharedResources));
		toolbar.addChild(new ContextHintsWidget(sharedResources));
		
		//Finalize
		addAllChildrenOfContainer(toolbar);
		sharedResources.setToolbar(toolbar);
	}
	
	public static int getToolbarAbsoluteX(int index) {
		return WIDGET_PADDING + (DropdownMenuWidget.WIDTH + WIDGET_PADDING) * index;
	}
}
