package nokori.clear_dialogue.ui.widget;

import static nokori.clear.vg.widget.text.ClearEscapeSequences.ESCAPE_SEQUENCE_COLOR;
import static nokori.clear_dialogue.ui.ClearDialogueRootWidgetAssembly.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Properties;

import nokori.clear.vg.widget.text.TextAreaAutoFormatterWidget;
import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear.windows.util.TinyFileDialog.Icon;
import nokori.clear.windows.util.TinyFileDialog.InputType;
import nokori.clear_dialogue.ui.SharedResources;

public class DropdownMenuWidgetTool extends DropdownMenuWidget {

	private static final String LABEL = "TOOL";
	
	private static final String OPTION_REPLACE = "REPLACE...";
	private static final String OPTION_MULTI_REPLACE = "MULTI-REPLACE...";
	private static final String OPTION_VIEW_SYNTAX = "VIEW SYNTAX";
	private static final String OPTION_REFRESH_SYNTAX = "REFRESH SYNTAX";
	private static final String OPTION_SET_SYNTAX = "SET SYNTAX...";
	
	private static final String[] OPTIONS = {
		OPTION_REPLACE,
		OPTION_MULTI_REPLACE,
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
		case OPTION_VIEW_SYNTAX:
			showSyntaxPopup();
			break;
		}
	}
	
	private void showSyntaxPopup() {
		String syntaxContent = loadSyntax();
		boolean syntaxLoaded = (syntaxContent != null);
		
		PopupMessageWidget message = new PopupMessageWidget(sharedResources, syntaxLoaded ? syntaxContent : "//No syntax loaded.", syntaxLoaded) {
			@Override
			public void onClose(String content) {
				if (syntaxLoaded) {
					if (TinyFileDialog.showConfirmDialog("Save Changes", "Would you like to save your edits to the syntax file?", InputType.YES_NO, Icon.QUESTION, false)) {
						saveSyntax(content);
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
	
	private File getSyntaxFile() {
		/*
		 * Get user-set syntax location (if it exists)
		 */
		
		File f = new File(SharedResources.SYNTAX_FILE_LOCATION);

		Properties props = new Properties();
		
		if(f.exists()){
			try {
				props.load(new FileReader(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String projectDirectory = props.getProperty("syntaxFile");
		
		return (projectDirectory != null ? new File(projectDirectory) : null);
	}

	private String loadSyntax() {
		
		File syntaxFile = getSyntaxFile();
		
		if (syntaxFile == null) {
			syntaxFile = new File("example_syntax.txt");
		}
		
		if (syntaxFile != null && syntaxFile.exists()) {
			try {
				
				//Read the syntax file
				return new String(Files.readAllBytes(syntaxFile.toPath()));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			System.err.println("loadSyntax() failed: Syntax file not found.");
		}
		
		return null;
	}
	
	private void saveSyntax(String content) {
		File syntaxFile = getSyntaxFile();
		
		if (syntaxFile != null) {
			
			try (PrintWriter out = new PrintWriter(syntaxFile)){
				
				out.println(content);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			System.err.println("saveSyntax() failed: Syntax file not found.");
		}
	}
}
