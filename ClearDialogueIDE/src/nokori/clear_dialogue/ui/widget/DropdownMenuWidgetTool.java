package nokori.clear_dialogue.ui.widget;

import static nokori.clear.vg.widget.text.ClearEscapeSequences.ESCAPE_SEQUENCE_COLOR;
import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;

import nokori.clear.vg.widget.text.TextAreaAutoFormatterWidget;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear.windows.util.TinyFileDialog.Icon;
import nokori.clear.windows.util.TinyFileDialog.InputType;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.util.DialogueUtils;
import nokori.clear_dialogue.ui.util.ReplaceUtils;

public class DropdownMenuWidgetTool extends DropdownMenuWidget {

	private static final String LABEL = "TOOL";
	
	private static final String OPTION_REPLACE = "REPLACE...";
	private static final String OPTION_VIEW_SYNTAX = "VIEW SYNTAX";
	private static final String OPTION_REFRESH_SYNTAX = "REFRESH SYNTAX";
	private static final String OPTION_SET_SYNTAX = "SET SYNTAX...";
	
	private static final String[] OPTIONS = {
		OPTION_REPLACE,
		OPTION_VIEW_SYNTAX,
		OPTION_REFRESH_SYNTAX,
		OPTION_SET_SYNTAX
	};
	
	private SharedResources sharedResources;
	
	public DropdownMenuWidgetTool(SharedResources sharedResources) {
		super(getToolbarAbsoluteX(1), WIDGET_PADDING, sharedResources.getNotoSans(), LABEL, OPTIONS);
		this.sharedResources = sharedResources;
	}

	@Override
	protected void optionSelected(String option, int index) {
		switch(option) {
		case OPTION_REPLACE:
			ReplaceUtils.runReplaceTool(sharedResources);
			break;
		case OPTION_VIEW_SYNTAX:
			showSyntaxPopup();
			break;
		case OPTION_REFRESH_SYNTAX:
			sharedResources.loadAndProcessSyntax();
			break;
		case OPTION_SET_SYNTAX:
			DialogueUtils.showSyntaxFileSelectDialog();
			sharedResources.loadAndProcessSyntax();
			break;
		}
	}
	
	private void showSyntaxPopup() {
		String syntaxContent = DialogueUtils.loadSyntax();
		boolean syntaxLoaded = (syntaxContent != null);
		
		PopupMessageWidget message = new PopupMessageWidget(sharedResources, syntaxLoaded ? syntaxContent : "//No syntax loaded.", syntaxLoaded) {
			@Override
			public void onClose(String content, boolean contentEdited) {
				if (syntaxLoaded && contentEdited) {
					if (TinyFileDialog.showConfirmDialog("Save Changes", "Would you like to save your edits to the syntax file?", InputType.YES_NO, Icon.QUESTION, false)) {
						DialogueUtils.saveSyntax(content);
					}
				}
			}
		};
		
		TextAreaAutoFormatterWidget syntaxHighlighter = new TextAreaAutoFormatterWidget();
		syntaxHighlighter.addSyntax("//", ESCAPE_SEQUENCE_COLOR, "#3F7F5F", TextAreaAutoFormatterWidget.SyntaxResetMode.RESET_AFTER_NEW_LINE);
		message.getTextAreaWidget().addChild(syntaxHighlighter);
		
		message.show();
	}
	
	/*
	 * 
	 * 
	 * Syntax Viewing / Editing
	 * 
	 * 
	 */
	

}
