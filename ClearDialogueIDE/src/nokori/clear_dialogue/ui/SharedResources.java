package nokori.clear_dialogue.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.font.Font;
import nokori.clear.vg.util.NanoVGScaler;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.text.ClearEscapeSequences;
import nokori.clear.vg.widget.text.TextAreaAutoFormatterWidget;
import nokori.clear.vg.widget.text.TextAreaAutoFormatterWidget.Syntax;
import nokori.clear.windows.Window;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.util.FileUtils;

/**
 * This is a pass-around class that allows JDialogue to communicate data around the program, such as the current project, context hints, etc.
 */
public class SharedResources {
	
	private ClearDialogueIDECore core;

	private Window window;
	private NanoVGContext context;
	private ClearDialogueRootWidgetAssembly rootWidgetAssembly;
	
	/*
	 * Dialogue Data
	 */
	
	public static final String SYNTAX_FILE_LOCATION = "syntax_directory.ini";
	
	private Project project = new Project();
	private File projectFileLocation = null;
	
	/*
	 * GUI Data
	 */
	
	private Font notoEmoji, notoSans, notoSerif;
	private String contextHint;
	private TextAreaAutoFormatterWidget syntaxWidget = new TextAreaAutoFormatterWidget();

	private WidgetAssembly toolbar;
	private ClearDialogueCanvas canvas;
	
	private NanoVGScaler scaler = new NanoVGScaler();

	public void init(ClearDialogueIDECore core, Window window, NanoVGContext context, ClearDialogueRootWidgetAssembly rootWidgetAssembly) {
		this.core = core;
		this.window = window;
		this.context = context;
		this.rootWidgetAssembly = rootWidgetAssembly;
		
		try {
			//Create fallback font for emojis
			Font.FallbackFontMode fallbackFontMode = Font.FallbackFontMode.REGULAR_TO_ALL;
			notoEmoji = new Font("NotoEmoji", new File("res/fonts/NotoEmoji/NotoEmoji-Regular.ttf")).load(context);
			
			//Create fonts for use on UIs and set the emojis as a fallback font for missing special characters
			notoSans = new Font("res/fonts/NotoSans/", "NotoSans-Regular", "NotoSans-Bold", "NotoSans-Italic", "NotoSans-Light").load(context);
			notoSans.addFallbackFont(context, notoEmoji, fallbackFontMode);
			
			notoSerif = new Font("res/fonts/NotoSerif/", "NotoSerif-Regular", "NotoSerif-Bold", "NotoSerif-Italic", "NotoSerif-Light").load(context);
			notoSerif.addFallbackFont(context, notoEmoji, fallbackFontMode);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		refreshContextHint();
		loadAndProcessSyntax(false);
		
		rootWidgetAssembly.init(this);
	}
	
	public ClearDialogueIDECore getIDECore() {
		return core;
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

	public NanoVGScaler getScaler() {
		return scaler;
	}

	public void refreshCanvas() {
		canvas.refresh(project);
	}
	
	/**
	 * Gets the current context hint visible at the bottom of the screen. Context hints give contextual information on how to use the IDE.
	 * @return
	 */
	public String getContextHint() {
		return contextHint;
	}

	/**
	 * Resets the context hint back to the general controls for navigating the canvas.
	 */
	public void refreshContextHint() {
		String scale = "Scale: " + Math.round(scaler.getScale() * 10.0) / 10.0;
		
		contextHint = scale + " | Drag LMB = Pan Canvas & Drag Nodes | Drag RMB = Highlight | Scroll MMB = Viewport Zoom";
		
		if (canvas != null && canvas.getNumHighlightedNodes() > 0) {
			if (canvas.getNumHighlightedNodes() == 1) {
				contextHint = scale + " | \"" + canvas.getHighlightedNode(0).getDialogue().getTitle() + "\" Highlighted | T = Add Tags | R = Remove Tags | N = Rename";
			} else {
				contextHint = scale + " | " + canvas.getNumHighlightedNodes() + " Highlighted | T = Add Tags to All | R = Remove Tags from All | N = Rename All | S = Relative Mouse sAuto-Snap";
			}
		}
	}

	/**
	 * Gets the currently active ClearDialogue Project.
	 */
	public Project getProject() {
		return project;
	}
	
	/**
	 * Gets the file location of the active ClearDialogue Project if it was loaded from a file.
	 */
	public File getProjectFileLocation() {
		return projectFileLocation;
	}

	/**
	 * Sets a new ClearDialogue Project and refreshes the Canvas with its data.
	 * @param project
	 */
	public void setProject(Project project, File projectFileLocation) {
		this.project = project;
		this.projectFileLocation = projectFileLocation;
		
		canvas.refresh(project);
		scaler.setScale(project.getViewportScale());
		rootWidgetAssembly.getProjectNameField().refresh();
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
	public void loadAndProcessSyntax(boolean refreshCanvas) {
		String syntax = FileUtils.loadSyntax();
		
		if (syntax != null) {
			syntaxWidget.clearAllSyntax();
			
			String[] split = FileUtils.loadSyntax().split("\n");
			
			for (int i = 0; i < split.length; i++) {
				String s = split[i].replaceAll("\n", "").trim();

				if (!s.isEmpty() && !s.startsWith("//")) {
					syntaxWidget.addSyntax(s, ClearEscapeSequences.ESCAPE_SEQUENCE_COLOR, ClearColor.CORAL.toHEX());
					//System.out.println("Registered Syntax: " + syntax.getKey() + " -> " + syntax.getEscapeSequence() + syntax.getInstructions() + " " + syntax.getResetMode().name());
				}
			}
			
			if (refreshCanvas) {
				canvas.refresh(project);
			}
		} else {
			System.err.println("Syntax content was null. Processing aborted.");
		}
	}
	
	/**
	 * Gets the syntax settings that can be passed into TextAreaAutoFormatterWidgets. This allows the widgets to stay up to date on the latest syntax settings.
	 */
	public ArrayList<Syntax> getSyntaxSettings() {
		return syntaxWidget.getSyntaxSettings();
	}
}
