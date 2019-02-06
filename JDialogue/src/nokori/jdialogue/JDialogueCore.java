package nokori.jdialogue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.Stack;

import javax.swing.UIManager;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import nokori.jdialogue.io.JDialogueIO;
import nokori.jdialogue.io.JDialogueJsonIO;
import nokori.jdialogue.project.Connection;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueNodeConnector;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.throwable.MissingArcError;
import nokori.jdialogue.throwable.MissingDialogueNodePaneError;
import nokori.jdialogue.ui.Button;
import nokori.jdialogue.ui.ButtonSkeleton;
import nokori.jdialogue.ui.MenuButton;
import nokori.jdialogue.ui.TextViewerMenu;
import nokori.jdialogue.ui.node.BoundLine;
import nokori.jdialogue.ui.node.ConnectorSelection;
import nokori.jdialogue.ui.node.DialogueNodeConnectorArc;
import nokori.jdialogue.ui.node.DialogueNodePane;
import nokori.jdialogue.ui.node.DialogueResponseNodePane;
import nokori.jdialogue.ui.node.DialogueTextNodePane;
import nokori.jdialogue.ui.pannable_pane.NodeGestures;
import nokori.jdialogue.ui.pannable_pane.PannablePane;
import nokori.jdialogue.ui.pannable_pane.SceneGestures;
import nokori.jdialogue.ui.util.CanvasSizeTool;
import nokori.jdialogue.ui.util.MergeTool;
import nokori.jdialogue.ui.util.MultiEditTool;
import nokori.jdialogue.ui.util.ReplaceTool;
import nokori.jdialogue.ui.util.ReplaceTool.ReplaceMode;
import nokori.jdialogue.ui.util.JDialogueUtils;

/**
 * The Core of this program, containing the GUI.
 * 
 * UI design inspired by Yarn:
 * https://github.com/InfiniteAmmoInc/Yarn
 * 
 * And Monologue:
 * https://github.com/nospoone/monologue
 * 
 * I apologize in advance for weird uses of JavaFX, this was my first time using it, 
 * but I wanted to make something that works right out of the box and this was my best bet.
 * 
 * The system is pretty customizable, so if you see anything you don't like, 
 * it should be pretty easy to plug and play the various parts. I.E. you could totally 
 * rip out this UI and make a replacement, since I've detached the actual project data.
 * 
 * When in doubt, read the comments
 * 
 * @author NOKORIWARE 2018
 * 
 * ------------------------------------------------------------------------------
 * 
 * This software uses JDK11 and JavaFX11. Use the following VM arguments to ensure that the program runs correctly (make sure to read them first):
 * 
 * --module-path "(javafx filepath)" --add-modules=javafx.controls,javafx.fxml
 * 
 * --add-exports javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED
 * --add-exports javafx.graphics/com.sun.javafx.text=ALL-UNNAMED
 * --add-exports javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED
 * --add-opens javafx.graphics/javafx.scene.text=ALL-UNNAMED
 * --add-opens javafx.graphics/com.sun.javafx.text=ALL-UNNAMED
 * 
 * ------------------------------------------------------------------------------
 * 
 * How to add a new DialogueNode type:
 * 
 * 1) Make a DialogueNode extension (example: DialogueTextNode)
 * 2) Make a DialogueNodePane extension that implements your custom DialogueNode (example: DialogueTextNodePane)
 * 3) Make a DialogueNodeEditor extension that implements your custom DialogueNode (example: DialogueTextNodeEditor)
 * 4) Hook up to JDialogueCore: addNodeButton(), addDialogueNode()
 * 5) Add support to various JDialogueIO behaviors
 * 6) Optional: add support to RefactorTool
 * 
 * ------------------------------------------------------------------------------
 * 
 * How to add a new export/import behavior:
 * 
 * 1) Make a class that implements JDialogueIO (example: JsonIO)
 * 2) Hook it up JDialogueCore: addMenuButton()
 * 
 */

public class JDialogueCore extends Application {
	
	private static final String PROGRAM_NAME = "JDialogue";
	private static final String PROGRAM_VERSION = "Rev. 7";
	
	/*
	 * window settings
	 */
	private static final int WINDOW_WIDTH = 1280;
	private static final int WINDOW_HEIGHT = 720;

	private Stage stage;
	
	/*
	 * display data
	 */
	
	private Pane uiPane;
	
	private PannablePane pannablePane;
	private NodeGestures nodeGestures;
	
	private Scene scene;
	private SceneGestures sceneGestures;
	
	/*
	 * Syntax
	 */
	
	public static final String SYNTAX_HIGHLIGHT_COLOR = "coral";
	private String[] syntax = null;
	
	/*
	 * Styling
	 */
	public static final int BUTTON_START_X = 20;
	public static final int BUTTON_Y = 20;
	public static final int BUTTON_WIDTH = 200;
	public static final int BUTTON_HEIGHT = 50;
	
	public static final int ROUNDED_RECTANGLE_ARC = 5;
	
	private static final int MENU_BUTTON_INCREMENT_HEIGHT = 50;
	
	private DropShadow shadow;
	
	private Font sansRegular = Font.loadFont(JDialogueUtils.loadFromPackage("nokori/jdialogue/fonts/NotoSans-Regular.ttf"), 20);
	private Font sansLight = Font.loadFont(JDialogueUtils.loadFromPackage("nokori/jdialogue/fonts/NotoSans-Light.ttf"), 20);
	private Font serifRegular = Font.loadFont(JDialogueUtils.loadFromPackage("nokori/jdialogue/fonts/NotoSerif-Regular.ttf"), 16);
	
	/*
	 * Project data
	 */
	private Project project;
	
	/*
	 * UI components
	 */
	private Text programInformation, contextHints;
	private InlineCssTextArea projectNameField;
	
	/*
	 * Connector management
	 */
	protected ArrayList<BoundLine> connectorLines = new ArrayList<BoundLine>();
	private ConnectorSelection selectedConnector = null;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		launch(args);
	}

	/**
	 * Start program
	 */
	@Override
	public void start(Stage stage) {
		this.stage = stage;
		
		stage.getIcons().addAll(
				new Image(JDialogueUtils.loadFromPackage("nokori/jdialogue/icons/icon_512x512.png")),
				new Image(JDialogueUtils.loadFromPackage("nokori/jdialogue/icons/icon_256x256.png")),
				new Image(JDialogueUtils.loadFromPackage("nokori/jdialogue/icons/icon_128x128.png")),
				new Image(JDialogueUtils.loadFromPackage("nokori/jdialogue/icons/icon_64x64.png")),
				new Image(JDialogueUtils.loadFromPackage("nokori/jdialogue/icons/icon_48x48.png")),
				new Image(JDialogueUtils.loadFromPackage("nokori/jdialogue/icons/icon_32x32.png")),
				new Image(JDialogueUtils.loadFromPackage("nokori/jdialogue/icons/icon_24x24.png")),
				new Image(JDialogueUtils.loadFromPackage("nokori/jdialogue/icons/icon_16x16.png")));
		
		stage.setMinWidth(WINDOW_WIDTH);
		stage.setMinHeight(WINDOW_HEIGHT);
		
		/*
		 * Load syntax
		 */
		
		loadSyntax();
		
		/*
		 * Default Project
		 */
		
		newProject();
		
		/*
		 * Program UI containers
		 * 
		 * Panes and pane accessories, I tell ya what
		 */
		
		//uiPane contains UI elements that don't move
		uiPane = new Pane();
		
		//Moves focus away from various TextFields in the UI (namely the project name one beside the node button)
		uiPane.setOnMousePressed(event -> {
			uiPane.requestFocus();
		});
		
		//Pannable pane contains all of the nodes, can be panned and zoomed
		pannablePane = new PannablePane(project.getCanvasWidth(), project.getCanvasHeight()) {
			@Override
			protected void layoutChildren() {
				super.layoutChildren();
			
				//Updates connectors if the pane changes
				Platform.runLater(() -> {
					updateConnectors(null);
				});
			}
		};
		
		//Cancel out selected connector if you're holding one and click the background
		pannablePane.setOnMouseClicked(event -> {
			if (selectedConnector != null) {
				setSelectedConnector(null);
			}
		});
		
		//Update the following connector line on mouse move
		pannablePane.setOnMouseMoved(event -> {
			if (selectedConnector != null) {
				scene.setCursor(Cursor.OPEN_HAND);
				selectedConnector.getFollowingConnectorLine().update(event, pannablePane);
			}
		});
		
		pannablePane.setTranslateX(project.getViewportX());
		pannablePane.setTranslateY(project.getViewportY());
		pannablePane.setScale(project.getViewportScale());
		
		uiPane.getChildren().add(pannablePane);
		
		//Scene
		scene = new Scene(uiPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("nokori/jdialogue/css/scrollbar_style.css").toExternalForm());
		
		/*
		 * NodeGestures controls the node dragging in pannable pane
		 * 
		 * We extend it to also notify the node panes of dragging so that they can reposition their
		 * connector lines accordingly
		 */
        
    	nodeGestures = new NodeGestures(this, pannablePane) {
    		@Override
    		public void mouseDragged(MouseEvent event) {
    			if (event.getSource() instanceof Node) {
            		setSelectedConnector(null);
            		scene.setCursor(Cursor.CLOSED_HAND);
    			}
    		}
    	};
		
		/*
		 * SceneGestures controls the PannablePane panning/highlighting
		 * 
		 * It's overriden to connect it to the Project object so that the viewport settings can be saved for the next session.
		 */
		sceneGestures = new SceneGestures(this, pannablePane, nodeGestures) {
			@Override
			public void mouseDragged(MouseEvent event, double newTranslateX, double newTranslateY) {
				// Drag the grabbing hand when you pane the screen
				scene.setCursor(Cursor.CLOSED_HAND);

				// Set viewport memory
				project.setViewportX(newTranslateX);
				project.setViewportY(newTranslateY);
			}

			@Override
			public void mouseScrolled(ScrollEvent event, double newScale) {
				project.setViewportScale(newScale);
			}
		};
        
        //PannablePane event handlers (panning/zooming)
        scene.setOnMouseDragged(sceneGestures.getOnMouseDraggedEventHandler());
        scene.setOnMousePressed(sceneGestures.getOnMousePressedEventHandler());
        scene.setOnMouseReleased(sceneGestures.getOnMouseReleasedEventHandler());
        scene.addEventHandler(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        scene.addEventHandler(KeyEvent.KEY_PRESSED, MultiEditTool.getKeyPressEventHandler(this));
        
        
        //Reset the cursor when you panning panning
        scene.setOnMouseClicked(event -> {
        	scene.setCursor(Cursor.DEFAULT);
        });
		
		/*
		 * Initialize UI
		 */
		
		initializeShadows();
		
		addBackground();
		addProgramInformationAndContextHints();
		addFileButton(stage);
		addToolsButton(stage);
		addNodeButton();
		addProjectNameField();

		uiPane.requestFocus();
		
		/*
		 * Finalize
		 */
		
		stage.setTitle(PROGRAM_NAME);
		stage.setScene(scene);
		stage.show();
	}
	
	/**
	 * Loads user syntax for highlighting text in JDialogue. Will help the user with error checking their game-specific tags.
	 * 
	 * @return the name of the syntax file loaded
	 */
	private String loadSyntax() {
		/*
		 * Get user-set syntax location (if it exists)
		 */
		File f = new File("syntax_directory.ini");
		
		Properties props = new Properties();
		
		if(f.exists()){
			try {
				props.load(new FileReader(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String projectDirectory = props.getProperty("syntaxFile");
		
		/*
		 * Load the syntax file
		 */
		
		File syntaxFile = (projectDirectory != null ? new File(projectDirectory) : new File("example_syntax.txt"));
		
		if (syntaxFile != null && syntaxFile.exists()) {
			try {
				//Read the syntax file, split it by new lines
				String s = new String(Files.readAllBytes(syntaxFile.toPath()));
				syntax = s.split("\n");
				
				//Remove the new lines after (uses a non-OS specific version)
				for (int i = 0; i < syntax.length; i++) {
					syntax[i] = syntax[i].replaceAll("[\\r\\n]", "");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			System.err.println("Syntax file not found.");
			syntax = new String[]{"//No syntax loaded"};
		}
		
		/*
		 * Refresh the various nodes with the new syntax information
		 */
		
		if (pannablePane != null) {
			for (int i = 0; i < pannablePane.getChildren().size(); i++) {
				Node n = pannablePane.getChildren().get(i);
				
				if (n instanceof DialogueNodePane) {
					((DialogueNodePane) n).refresh(this);
				}
			}
		}
		
		/*
		 * Return the name of the syntax file loaded
		 */
		
		return syntaxFile.getName();
	}
	
	/**
	 * Initialize backshadows to be re-used throughout UI
	 */
	private void initializeShadows() {
		shadow = new DropShadow();
		shadow.setOffsetX(1);
		shadow.setOffsetY(1);
		shadow.setRadius(2);
		shadow.setColor(Color.LIGHTGRAY);
	}
	
	/**
	 * Fill background with color
	 */
	private void addBackground() {
		uiPane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, null, null)));
	}

	/**
	 * Add info on the program to the bottom-left corner of the screen
	 */
	private void addProgramInformationAndContextHints() {
		int offsetY = 20;
		
		Font sansLightSmall = Font.loadFont(JDialogueUtils.loadFromPackage("nokori/jdialogue/fonts/NotoSans-Light.ttf"), 18);
		
		//Information on the program itself (so that if screenshots are taken, people will know what the program is)
		String programInformationString = PROGRAM_NAME + " " + PROGRAM_VERSION + " by NOKORI•WARE";
		
		programInformation = new Text(programInformationString);
		programInformation.setFont(sansLightSmall);
		programInformation.setFill(Color.LIGHTGRAY.darker());
		programInformation.setX(20);
		programInformation.setY(WINDOW_HEIGHT - offsetY);
		programInformation.setMouseTransparent(true);
		
		//Context hints. These are activated under certain circumstances to tell the user instructions on how to use tools when they're activated.
		contextHints = new Text();
		contextHints.setFont(sansLightSmall);
		contextHints.setFill(programInformation.getFill());
		contextHints.setX(JDialogueUtils.getStringBounds(sansLightSmall, programInformationString).getWidth() + 75);
		contextHints.setY(programInformation.getY());
		contextHints.setMouseTransparent(true);
		setDefaultContextHint();
		
		//Clip to bottom-left
		uiPane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
			double newY = newValue.getHeight() - offsetY;
			programInformation.setY(newY);
			contextHints.setY(newY);
		});
		
		//Add to pane
		uiPane.getChildren().addAll(programInformation, contextHints);
	}

	private static final String NEW_PROJECT = "NEW PROJECT";
	private static final String SELECT_PROJECT_DIRECTORY = "PROJECT DIR...";
	private static final String MERGE_PROJECT = "MERGE PROJECT...";
	private static final String EXPORT_JSON = "EXPORT JSON...";
	private static final String IMPORT_JSON = "IMPORT JSON...";
	
	/**
	 * Button for activating save/load settings and any other settings added in the future
	 */
	private void addFileButton(Stage stage) {
		String[] options = {
				NEW_PROJECT,
				SELECT_PROJECT_DIRECTORY,
				MERGE_PROJECT,
				EXPORT_JSON,
				IMPORT_JSON
		};
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, shadow, "FILE", sansRegular, sansLight, options, MENU_BUTTON_INCREMENT_HEIGHT) {
			@Override
			public void optionClicked(MouseEvent event, String optionName, int optionIndex) {
				switch(optionName) {
				case NEW_PROJECT:
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Start New Project");
					alert.setHeaderText("Start new project? Unsaved changes will be lost.");
					((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
					
					Platform.runLater(new Runnable() {
					    @Override
					    public void run() {
							alert.setX(stage.getX() + stage.getWidth()/2 - alert.getWidth()/2);
							alert.setY(stage.getY() + stage.getHeight()/2 - alert.getHeight()/2);
					    }
					});
					
					Optional<ButtonType> result = alert.showAndWait();
					
					if (result.get() == ButtonType.OK){
						newProject();
					}
					
					break;
				case SELECT_PROJECT_DIRECTORY:
					setProjectDirectory(stage);
					break;
				case MERGE_PROJECT:
					MergeTool.openMergeToolDialog(JDialogueCore.this, getProjectDirectory(), project);
					break;
				case EXPORT_JSON:
					exportProject(stage, new JDialogueJsonIO());
					break;
				case IMPORT_JSON:
					importProject(stage, new JDialogueJsonIO());
					break;
				}
			}
		};
		
		//Add to pane
		button.setLayoutX(BUTTON_START_X);
		button.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(button);
	}
	
	/**
	 * Initialize a default project
	 */
	private void newProject() {
		boolean projectNull = (project == null);
		
		if (!projectNull) {
			pannablePane.getChildren().clear();
		}
		
		project = new Project();
		
		if (!projectNull) {
			refreshUI(false);
		}
	}
	
	/**
	 * Opens an export dialog for the selected JDialogueIO and runs it once a file is selected
	 */
	private void exportProject(Stage stage, JDialogueIO behavior) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save " + behavior.getTypeName() + " file");
		fileChooser.setInitialFileName(project.getName() + "." + behavior.getTypeName());
		fileChooser.getExtensionFilters().add(behavior.getExtensionFilter());
		fileChooser.setInitialDirectory(getProjectDirectory());

		File f = fileChooser.showSaveDialog(stage);
		
		if (f != null) {
			try {
				behavior.exportProject(project, f);
			} catch (Exception e) {
				e.printStackTrace();
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Caught " + e.getClass().getSimpleName());
				alert.setHeaderText("Failed to export project.");
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
		}
	}
	
	/**
	 * Opens an import dialog for the selected JDialogueIO and runs it once a file is selected
	 */
	private void importProject(Stage stage, JDialogueIO behavior) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open " + behavior.getTypeName() + " file");
		fileChooser.getExtensionFilters().add(behavior.getExtensionFilter());
		fileChooser.setInitialDirectory(getProjectDirectory());
		
		File f = fileChooser.showOpenDialog(stage);

		if (f != null) {
			try {
				Project project = behavior.importProject(f);
				
				this.project = project;
				refreshUI(false);
				
			} catch(Exception e) {
				e.printStackTrace();
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Caught " + e.getClass().getSimpleName());
				alert.setHeaderText("Failed to import project.");
				alert.setContentText(e.getMessage());
				alert.showAndWait();
			}
		}
	}
	
	/**
	 * This refreshes PannablePane and any other UI element to match a newly imported Project's settings. This can also be used if anything concerning the
	 * viewport is updated as well, such as the canvas size or scaling.
	 * 
	 * @param retainViewportSettings if set to true, the viewports X/Y/Scale/Size won't be modified upon refresh (e.g. when merging projects instead of just importing)
	 */
	public void refreshUI(boolean retainViewportSettings) {
		
		//Updated project name field with this project's name
		projectNameField.replaceText(project.getName());
		
		//Clear PannablePane and update its size
		pannablePane.getChildren().clear();
		
		if (!retainViewportSettings) {
			pannablePane.setSize(project.getCanvasWidth(), project.getCanvasHeight());
		
			pannablePane.setTranslateX(project.getViewportX());
			pannablePane.setTranslateY(project.getViewportY());
			pannablePane.setScale(project.getViewportScale());
		}
		
		// Build all of the DialogueNodePanes (graphical representation of node)
		for (int i = 0; i < project.getNumNodes(); i++) {
			DialogueNode node = project.getNode(i);
			createDialogueNodePane(node);
		}

		// Build all BoundLine objects for each Connection
		for (int i = 0; i < project.getNumConnections(); i++) {
			Connection connection = project.getConnection(i);

			DialogueNodeConnector connector1 = connection.getConnector1();
			DialogueNodeConnector connector2 = connection.getConnector2();

			DialogueNodeConnectorArc arc1 = getDialogueNodeConnectorArcOf(connector1);
			DialogueNodeConnectorArc arc2 = getDialogueNodeConnectorArcOf(connector2);

			if (arc1 != null && arc2 != null) {
				BoundLine line = new BoundLine(arc1, connector1, arc2, connector2);

				addConnectorLine(line);

			} else {
				throw new MissingArcError("\n" + connector1.getParent().getName() + " -> " + arc1 + "\n" + connector2.getParent().getName() + " -> " + arc2);
			}
		}
	}
	
	private DialogueNodeConnectorArc getDialogueNodeConnectorArcOf(DialogueNodeConnector connector) {
		for (int i = 0; i < pannablePane.getChildren().size(); i++) {
			Node n = pannablePane.getChildren().get(i);
			
			if (n instanceof DialogueNodePane) {
				DialogueNodePane pane = (DialogueNodePane) n;
				
				DialogueNodeConnectorArc arc = pane.getDialogueNodeConnectorArcOf(connector);
				
				if (arc != null) {
					return arc;
				}
			}
		}
		
		return null;
	}
	
	private static final String CANVAS_SIZE = "CANVAS SIZE...";
	private static final String REPLACE = "REPLACE...";
	private static final String MULTIREPLACE = "MULTI-REPLACE...";
	private static final String VIEW_SYNTAX = "VIEW SYNTAX";
	private static final String REFRESH_SYNTAX = "REFRESH SYNTAX";
	private static final String SET_SYNTAX = "SET SYNTAX...";
	
	/**
	 * Button for adding various tools to the editor
	 */
	private void addToolsButton(Stage stage) {
		int buttonX = BUTTON_START_X + BUTTON_WIDTH + 10;
		
		String[] options = { 
				CANVAS_SIZE,
				REPLACE,
				MULTIREPLACE,
				VIEW_SYNTAX,
				REFRESH_SYNTAX,
				SET_SYNTAX
		};
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, shadow, "TOOL", sansRegular, sansLight, options, MENU_BUTTON_INCREMENT_HEIGHT) {
			
			@Override
			public void optionClicked(MouseEvent event, String optionName, int optionIndex) {
				switch(optionName) {
				case CANVAS_SIZE:
					new CanvasSizeTool().openCanvasSizeDialog(JDialogueCore.this, project, pannablePane);
					break;
				case REPLACE:
					ReplaceTool.run(stage, getProjectDirectory(), project, ReplaceMode.LOCAL);
					
					//Refresh various info inside panes to reflect the new changes
					for (int i = 0; i < getChildren().size(); i++) {
						Node n = getChildren().get(i);
						
						if (n instanceof DialogueNodePane) {
							((DialogueNodePane) n).refresh(JDialogueCore.this);
						}
					}
					
					break;
				case MULTIREPLACE:
					ReplaceTool.run(stage, getProjectDirectory(), project, ReplaceMode.MULTI);
					break;
				case VIEW_SYNTAX:
					uiPane.getChildren().add(new TextViewerMenu(JDialogueCore.this, serifRegular, syntax));
					break;
				case REFRESH_SYNTAX:
					JDialogueUtils.showAlert(stage, AlertType.INFORMATION, "Syntax Refreshed", "Reloaded syntax file: " + loadSyntax(), "");
					break;
				case SET_SYNTAX:
					setSyntax(stage);
					break;
				}
			}
		};
		
		//Add to pane
		button.setLayoutX(buttonX);
		button.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(button);
	}
	
	/**
	 * Set the Project directory (where the FileChoosers will open to by default)
	 */
	private void setProjectDirectory(Stage stage) {
		File f = new File("project_directory.ini");
		
		Properties props = new Properties();
		
		if(f.exists()){
			try {
				props.load(new FileReader(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File defaultLocation = new File(".");
		
		String projectDirectory = props.getProperty("projectDir");
		
		if (projectDirectory != null) {
			File location = new File(projectDirectory);
			
			if (location.exists()) {
				defaultLocation = location;
			}
		}

		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Select Project Directory");
		fileChooser.setInitialDirectory(defaultLocation);

		File dir = fileChooser.showDialog(stage);

		if (dir != null) {
			props.setProperty("projectDir", dir.getPath());
			
			try{
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);
				props.store(fos, "");
				fos.flush();
				fos.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Get the set directory for saving projects (convenience)
	 */
	public File getProjectDirectory() {
		File f = new File("project_directory.ini");
		
		Properties props = new Properties();
		
		if(f.exists()){
			try {
				props.load(new FileReader(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String projectDirectory = props.getProperty("projectDir");
		
		if (projectDirectory != null) {
			File dir = new File(projectDirectory);
			
			if (dir.exists()) {
				return dir;
			}
		}
		
		return new File(".");
	}
	
	/**
	 * Set the location of the syntax file to load at startup
	 * @param stage
	 */
	private void setSyntax(Stage stage) {
		//Try to get the current directory first so that the filechooser will open in that location
		File f = new File("syntax_directory.ini");
		
		Properties props = new Properties();
		
		if(f.exists()){
			try {
				props.load(new FileReader(f));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File defaultLocation = new File(".");
		
		String syntaxLocation = props.getProperty("syntaxFile");
		
		if (syntaxLocation != null) {
			File location = new File(syntaxLocation).getParentFile();
			
			if (location.exists()) {
				defaultLocation = location;
			}
		}

		//Open the directory filechooser
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Syntax File");
		
		if (defaultLocation.exists()) {
			fileChooser.setInitialDirectory(defaultLocation);
		}
		
		//Select the syntax file
		File file = fileChooser.showOpenDialog(stage);

		if (file != null && file.exists()) {
			//If the file is valid, record the location
			props.setProperty("syntaxFile", file.getAbsolutePath());
			
			//Save the location
			try{
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);
				props.store(fos, "");
				fos.flush();
				fos.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			//Reload the current syntax
			loadSyntax();
		}
	}
	
	private static final String DIALOGUE = "DIALOGUE...";
	private static final String RESPONSE = "RESPONSE...";
	
	/**
	 * Button for adding new story nodes, easily expanded (see below)
	 */
	private void addNodeButton() {
		int buttonX = BUTTON_START_X + ((BUTTON_WIDTH + 10) * 2);
		
		String[] options = { 
				DIALOGUE,
				RESPONSE
		};
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, shadow, "+NODE", sansRegular, sansLight, options, MENU_BUTTON_INCREMENT_HEIGHT) {
			
			@Override
			public void optionClicked(MouseEvent event, String optionName, int optionIndex) {
				//Yeah, I know right
				Window window = scene.getWindow();
				double screenCenterX = (window.getX() + window.getWidth()/2);
				double screenCenterY = (window.getY() + window.getHeight()/2);
				
				Point2D point = pannablePane.screenToLocal(screenCenterX, screenCenterY);
				
				double nodeX = point.getX() - DialogueNodePane.WIDTH/2;
				double nodeY = point.getY() - DialogueNodePane.HEIGHT/2;
				
				//Create node
				DialogueNode node = null;
				
				switch(optionName) {
				case DIALOGUE:
					node = new DialogueTextNode(project, "Dialogue", nodeX, nodeY);
					break;
				case RESPONSE:
					node = new DialogueResponseNode(project, "Response", nodeX, nodeY);
					break;
				}
				
				//Add node to project data
				project.addNode(node);
				createDialogueNodePane(node);
			}
		};
		
		//Add to pane
		button.setLayoutX(buttonX);
		button.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(button);
	}
	
	/**
	 * Automatically generates and adds a new DialogueNodeFX to the instance based on the passed in DialogueNode type.
	 * @param dialogueNode
	 * @throws MissingDialogueNodePaneError 
	 */
	private void createDialogueNodePane(DialogueNode dialogueNode) {
		DialogueNodePane dialogueNodePane = null;
		
		if (dialogueNode instanceof DialogueTextNode) {
			dialogueNodePane = new DialogueTextNodePane(this, (DialogueTextNode) dialogueNode, shadow, sansRegular, serifRegular);
		}
		
		if (dialogueNode instanceof DialogueResponseNode) {
			dialogueNodePane = new DialogueResponseNodePane(this, (DialogueResponseNode) dialogueNode, shadow, sansRegular, serifRegular, 45);
		}
		
		if (dialogueNodePane != null) {
			
			//Add node to current instance (UI) and add event handlers (NodeGestures.java) for dragging it around
	        dialogueNodePane.setTranslateX(dialogueNode.getX());
	        dialogueNodePane.setTranslateY(dialogueNode.getY());
	        dialogueNodePane.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
	        dialogueNodePane.addEventFilter(MouseEvent.MOUSE_RELEASED, nodeGestures.getOnMouseReleasedEventHandler());
	        dialogueNodePane.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
	        
			//Add to parent
			pannablePane.getChildren().add(dialogueNodePane);
			
	        //Clamp it inside of the workspace
			nodeGestures.clampToParentBounds(dialogueNodePane);
		} else {
			throw new MissingDialogueNodePaneError(dialogueNode);
		}
	}
	
	/**
	 * Removes a dialogue node
	 * @param dialogueNode
	 */
	public void removeDialogueNode(DialogueNodePane dialogueNodePane) {
		DialogueNode node = dialogueNodePane.getDialogueNode();
		
		node.disconnectAllConnectors();
		project.removeNode(node);
		
		pannablePane.getChildren().remove(dialogueNodePane);
		
		updateConnectors(null);
	}
	
	/**
	 * Set the selected connector so that the user can connect two nodes together
	 */
	public void setSelectedConnector(ConnectorSelection selectedConnector) {
		if (this.selectedConnector != null) {
			pannablePane.getChildren().remove(this.selectedConnector.getFollowingConnectorLine());
		}
		
		this.selectedConnector = selectedConnector;
		
		if (selectedConnector != null) {
			pannablePane.getChildren().add(selectedConnector.getFollowingConnectorLine());
		}else {
			scene.setCursor(Cursor.DEFAULT);
		}
	}
	
	/**
	 * @return the currently selected DialogueNodeConnector.
	 */
	public ConnectorSelection getSelectedConnector() {
		return selectedConnector;
	}
	
	/**
	 * Update the positions of the node connectors and remove ones that are no longer valid
	 */
	public void updateConnectors(MouseEvent event) {
		for (int i = 0; i < connectorLines.size(); i++) {
			BoundLine line = connectorLines.get(i);
			
			if (line.update(event, pannablePane)) {
				continue;
			}else {
				pannablePane.getChildren().remove(line);
				connectorLines.remove(i);
				i--;
			}
		}
	}
	
	public void addConnectorLine(BoundLine line) {
		connectorLines.add(line);
		pannablePane.getChildren().add(line);
	}
	
	/**
	 * Project name field for customizing the name of the Project
	 */
	private void addProjectNameField() {
		int buttonX = BUTTON_START_X + ((BUTTON_WIDTH + 10) * 3);
		int buttonW = 500;
		
		ButtonSkeleton projectNameFieldButton = new ButtonSkeleton(buttonW, BUTTON_HEIGHT, shadow);
		
		//Project name text field
		projectNameField = new InlineCssTextArea();
		projectNameField.replaceText(project.getName());
		
		projectNameField.setMinWidth(buttonW - 40);
		projectNameField.setMaxWidth(buttonW - 40);
		projectNameField.setMaxHeight(BUTTON_HEIGHT - 20); 
		
		projectNameField.setLayoutX(Button.BUTTON_MARGIN_X);
		projectNameField.setLayoutY(13); //manually measured
		
		projectNameField.setBackground(Background.EMPTY);
		projectNameField.setWrapText(false);
		
		String fontStyle 	= "-fx-font-family: '" + sansRegular.getFamily() + "'; -fx-font-size: " + sansRegular.getSize() + ";";
		String borderStyle 	= "-fx-border-color: " + Button.getTextColorCode() + "; -fx-border-width: 0 0 1 0;";
		
		projectNameField.setStyle(fontStyle + borderStyle);
		
		//I couldn't find a better solution, just using -fx-fill doesn't do anything
		projectNameField.setStyle(0, projectNameField.getText().length(), "-fx-fill: " + Button.getTextColorCode() + ";");
		
		//Update project name 
		projectNameField.textProperty().addListener((o, oldText, newText) -> {
			project.setName(newText);
		});
		
		//having enter cancel out the focus gives a feeling of confirmation
		projectNameField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				uiPane.requestFocus();
			}
		});
		
		//Hack fix for not being able to disable multi-line
		JDialogueUtils.disableMultiLineShortcuts(projectNameField);

		//Add to button skeleton
		projectNameFieldButton.getChildren().add(projectNameField);
		
		//Add to pane
		projectNameFieldButton.setLayoutX(buttonX);
		projectNameFieldButton.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(projectNameFieldButton);
	}

	/*
	 * 
	 * GETTERS/SETTERS
	 * 
	 */
	
	/**
	 * Compiles a Stack of all the multi-selected nodes and returns them. Stacks are not recycled, use wisely!
	 * @return
	 */
	public Stack<DialogueNodePane> getAllMultiSelected(){
		Stack<DialogueNodePane> selected = new Stack<DialogueNodePane>();
		
		for (int i = 0; i < pannablePane.getChildren().size(); i++) {
			Node node = pannablePane.getChildren().get(i);
			
			if (node instanceof DialogueNodePane) {
				DialogueNodePane dNode = (DialogueNodePane) node;
				
				if (dNode.isMultiSelected()) {
					selected.push(dNode);
				}
			}
		}
		
		return selected;
	}
	
	public int getNumMultiSelected() {
		int numSel = 0;
		
		for (int i = 0; i < pannablePane.getChildren().size(); i++) {
			Node node = pannablePane.getChildren().get(i);
			
			if (node instanceof DialogueNodePane) {
				DialogueNodePane dNode = (DialogueNodePane) node;
				
				if (dNode.isMultiSelected()) {
					numSel++;
				}
			}
		}
		
		return numSel;
	}
	
	public void setContextHint(String hint) {
		if (!contextHints.getText().equals(hint)) {
			FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), contextHints);
			fadeTransition.setFromValue(0.0);
			fadeTransition.setToValue(1.0);
			fadeTransition.play();
			
			contextHints.setText(hint);
			
			//FYI I totally pulled the 2.1 out of nowhere just so multi-line context hints would line up with the program information
			//Feel free to change it if necessary
			double newLineOffset = (hint.contains("\n") ? (contextHints.getBoundsInParent().getHeight()/2.1) : 0);
			contextHints.setY(programInformation.getY() - newLineOffset);
		}
	}
	
	public void setDefaultContextHint() {
		setContextHint(SceneGestures.getSceneContextHint());
	}
	
	public Project getActiveProject() {
		return project;
	}
	
	public String[] getSyntax() {
		return syntax;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public Scene getScene() {
		return scene;
	}

	public PannablePane getPannablePane() {
		return pannablePane;
	}
	
	public NodeGestures getNodeGestures() {
		return nodeGestures;
	}
	
	public Pane getUIPane() {
		return uiPane;
	}
}
