package nokori.clear_dialogue.ui;

import java.io.IOException;
import java.util.ArrayList;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.font.Font;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.text.ClearEscapeSequences;
import nokori.clear.vg.widget.text.TextAreaAutoFormatterWidget;
import nokori.clear.vg.widget.text.TextAreaAutoFormatterWidget.Syntax;
import nokori.clear.windows.Window;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.util.DialogueUtils;
import nokori.clear_dialogue.ui.widget.node.ConnectionRendererWidget;

/**
 * This is a pass-around class that allows JDialogue to communicate data around the program, such as the current project, context hints, etc.
 */
public class SharedResources {

	private Window window;
	private NanoVGContext context;
	private ClearDialogueRootWidgetAssembly rootWidgetAssembly;
	
	/*
	 * Dialogue Data
	 */
	public static final String SYNTAX_FILE_LOCATION = "syntax_directory.ini";
	
	private Project project = new Project();
	
	/*
	 * GUI Data
	 */
	
	private Font notoSans, notoSerif;
	private String contextHint;
	private TextAreaAutoFormatterWidget syntaxWidget = new TextAreaAutoFormatterWidget();

	private WidgetAssembly toolbar;
	private ClearDialogueCanvas canvas;
	private ConnectionRendererWidget connectionRenderer;
	
	public void init(Window window, NanoVGContext context, ClearDialogueRootWidgetAssembly rootWidgetAssembly) {
		this.window = window;
		this.context = context;
		this.rootWidgetAssembly = rootWidgetAssembly;
		
		try {
			notoSans = new Font("res/fonts/NotoSans/", "NotoSans-Regular", "NotoSans-Bold", "NotoSans-Italic", "NotoSans-Light").load(context);
			notoSerif = new Font("res/fonts/NotoSerif/", "NotoSerif-Regular", "NotoSerif-Bold", "NotoSerif-Italic", "NotoSerif-Light").load(context);
		} catch (IOException e) {
			e.printStackTrace();
		}

		resetContextHint();
		loadAndProcessSyntax();
		
		rootWidgetAssembly.init(this);
	}
	
	public Window getWindow() {
		return window;
	}

	public NanoVGContext getNanoVGContext() {
		return context;
	}
	
	public WidgetAssembly getRootWidgetAssembly() {
		return rootWidgetAssembly;
	}

	public WidgetAssembly getToolbar() {
		return toolbar;
	}

	public void setToolbar(WidgetAssembly toolbar) {
		this.toolbar = toolbar;
	}

	public ClearDialogueCanvas getCanvas() {
		return canvas;
	}

	public void setCanvas(ClearDialogueCanvas canvas) {
		this.canvas = canvas;
	}

	public ConnectionRendererWidget getConnectionRenderer() {
		return connectionRenderer;
	}

	public void setConnectionRenderer(ConnectionRendererWidget connectionRenderer) {
		this.connectionRenderer = connectionRenderer;
	}
	
	/**
	 * Gets the current context hint visible at the bottom of the screen. Context hints give contextual information on how to use the IDE.
	 * @return
	 */
	public String getContextHint() {
		return contextHint;
	}

	/**
	 * Sets the current context hint.
	 * @param contextHint
	 */
	public void setContextHint(String contextHint) {
		this.contextHint = contextHint;
	}
	
	/**
	 * Resets the context hint back to the general controls for navigating the canvas.
	 */
	public void resetContextHint() {
		contextHint = "Drag LMB = Pan Canvas & Drag Nodes";
	}

	/**
	 * Gets the currently active JDialogue Project.
	 * @return
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Sets a new JDialogue Project and refreshes the Canvas with its data.
	 * @param project
	 */
	public void setProject(Project project) {
		this.project = project;
		canvas.refresh(project);
	}

	public Font getNotoSans() {
		return notoSans;
	}

	public Font getNotoSerif() {
		return notoSerif;
	}
	
	/**
	 * This function will call ClearDialogueIDEUtil.loadSyntax() and then process that string into the TextAreaAutoFormatterWidget that's used with 
	 * the TextAreaWidget's around the IDE.
	 */
	public void loadAndProcessSyntax() {
		syntaxWidget.clearAllSyntax();
		String[] split = DialogueUtils.loadSyntax().split("\n");
		
		for (int i = 0; i < split.length; i++) {
			String s = split[i].replaceAll("\n", "").trim();

			if (!s.isEmpty() && !s.startsWith("//")) {
				@SuppressWarnings("unused")
				Syntax syntax = syntaxWidget.addSyntax(s, ClearEscapeSequences.ESCAPE_SEQUENCE_COLOR, ClearColor.CORAL.toHEX());
				//System.out.println("Registered Syntax: " + syntax.getKey() + " -> " + syntax.getEscapeSequence() + syntax.getInstructions() + " " + syntax.getResetMode().name());
			}
		}
	}
	
	/**
	 * Gets the syntax settings that can be passed into TextAreaAutoFormatterWidgets. This allows the widgets to stay up to date on the latest syntax settings.
	 */
	public ArrayList<Syntax> getSyntaxSettings() {
		return syntaxWidget.getSyntaxSettings();
	}
}
