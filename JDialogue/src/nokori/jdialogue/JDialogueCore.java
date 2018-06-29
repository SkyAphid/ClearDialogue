package nokori.jdialogue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

import javax.swing.UIManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
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
import nokori.jdialogue.io.JDialogueIO;
import nokori.jdialogue.io.JDialogueJsonIO;
import nokori.jdialogue.io.JDialogueSerializerIO;
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
import nokori.jdialogue.ui.node.BoundLine;
import nokori.jdialogue.ui.node.ConnectorSelection;
import nokori.jdialogue.ui.node.DialogueNodeConnectorArc;
import nokori.jdialogue.ui.node.DialogueNodePane;
import nokori.jdialogue.ui.node.DialogueResponseNodePane;
import nokori.jdialogue.ui.node.DialogueTextNodePane;
import nokori.jdialogue.ui.pannable_pane.NodeGestures;
import nokori.jdialogue.ui.pannable_pane.PannablePane;
import nokori.jdialogue.ui.pannable_pane.SceneGestures;
import nokori.jdialogue.ui.util.ReplaceTool;
import nokori.jdialogue.ui.util.ReplaceTool.ReplaceMode;
import nokori.jdialogue.ui.util.UIUtil;

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
 * How to add a new DialogueNode type:
 * 
 * 1) Make a DialogueNode extension (example: DialogueTextNode)
 * 2) Make a DialogueNodePane extension that implements your custom DialogueNode (example: DialogueTextNodePane)
 * 3) Make a DialogueNodeEditor extension that implements your custom DialogueNode (example: DialogueTextNodeEditor)
 * 4) Hook up to JDialogueCore: addNodeButton(), addDialogueNode()
 * 5) Add support to various JDialogueIO behaviors (unless you use the serializer, in which case, it'll just werk)
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
	private static final String PROGRAM_VERSION = "Rev. 1";
	
	//window settings
	private static final int WINDOW_WIDTH = 1280;
	private static final int WINDOW_HEIGHT = 720;

	//display data
	private Pane uiPane;
	
	private static final int PANNABLE_PANE_WIDTH = 10_000;
	private static final int PANNABLE_PANE_HEIGHT = 10_000;
	private PannablePane pannablePane;
	private NodeGestures nodeGestures;
	
	private Scene scene;
	private SceneGestures sceneGestures;
	
	//styling
	public static final int BUTTON_START_X = 20;
	public static final int BUTTON_Y = 20;
	public static final int BUTTON_WIDTH = 200;
	public static final int BUTTON_HEIGHT = 50;
	
	public static final int ROUNDED_RECTANGLE_ARC = 5;
	
	private static final int MENU_BUTTON_INCREMENT_HEIGHT = 50;
	
	private DropShadow shadow;
	
	private Font robotoRegular20 = Font.loadFont(UIUtil.loadFromPackage("nokori/jdialogue/fonts/RobotoRegular.ttf"), 20);
	private Font robotoLight20 = Font.loadFont(UIUtil.loadFromPackage("nokori/jdialogue/fonts/RobotoLight.ttf"), 20);
	private Font markazi12 = Font.loadFont(UIUtil.loadFromPackage("nokori/jdialogue/fonts/Markazi.ttf"), 22);
	
	//Project data
	private Project project;
	
	//UI components
	private TextField projectNameField;
	
	//Connector management
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
		stage.getIcons().addAll(
				new Image(UIUtil.loadFromPackage("nokori/jdialogue/icons/icon_512x512.png")),
				new Image(UIUtil.loadFromPackage("nokori/jdialogue/icons/icon_256x256.png")),
				new Image(UIUtil.loadFromPackage("nokori/jdialogue/icons/icon_128x128.png")),
				new Image(UIUtil.loadFromPackage("nokori/jdialogue/icons/icon_64x64.png")),
				new Image(UIUtil.loadFromPackage("nokori/jdialogue/icons/icon_48x48.png")),
				new Image(UIUtil.loadFromPackage("nokori/jdialogue/icons/icon_32x32.png")),
				new Image(UIUtil.loadFromPackage("nokori/jdialogue/icons/icon_24x24.png")),
				new Image(UIUtil.loadFromPackage("nokori/jdialogue/icons/icon_16x16.png")));
		
		stage.setMinWidth(WINDOW_WIDTH);
		stage.setMinHeight(WINDOW_HEIGHT);
		
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
		pannablePane = new PannablePane(PANNABLE_PANE_WIDTH, PANNABLE_PANE_HEIGHT) {
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
		
		//Configure pannable pane mouse gestures
		sceneGestures = new SceneGestures(pannablePane) {
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
		scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        
        //Reset the cursor when you panning panning
        scene.setOnMouseClicked(event -> {
        	scene.setCursor(Cursor.DEFAULT);
        });
		
		/*
		 * NodeGestures controls the node dragging in pannable pane
		 * 
		 * We extend it to also notify the node panes of dragging so that they can reposition their
		 * connector lines accordingly
		 */
        
    	nodeGestures = new NodeGestures(pannablePane) {
    		@Override
    		public void mouseDragged(MouseEvent event) {
    			if (event.getSource() instanceof Node) {
            		setSelectedConnector(null);
            		
	    			Node n = (Node) event.getSource();
					
					if (n instanceof DialogueNodePane) {
						//Update connectors if a node was moved
						updateConnectors(event);
						
						//Update node positiong
						DialogueNodePane pane = (DialogueNodePane) n;
						DialogueNode node = pane.getDialogueNode();
						
						node.setX(pane.getTranslateX());
						node.setY(pane.getTranslateY());
					}
    			}
    		}
    	};
		
		/*
		 * Initialize UI
		 */
		
		initializeShadows();
		
		addBackground();
		addProgramInfo();
		addMenuButton(stage);
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
	private void addProgramInfo() {
		int offsetY = 20;
		
		Text text = new Text(PROGRAM_NAME + " " + PROGRAM_VERSION + " | Hold LMB = Drag/Pan | 2xLMB = Edit Node | 2xRMB = Delete Node");
		text.setFont(robotoRegular20);
		text.setFill(Color.LIGHTGRAY.darker());
		text.setX(20);
		text.setY(WINDOW_HEIGHT - offsetY);
		
		//Clip to bottom-left
		uiPane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
			text.setY(newValue.getHeight() - offsetY);
		});
		
		//Add to pane
		uiPane.getChildren().add(text);
	}
	

	private static final String NEW_PROJECT = "NEW PROJECT";
	private static final String SELECT_PROJECT_DIRECTORY = "PROJECT DIR...";
	private static final String SAVE = "SAVE...";
	private static final String OPEN = "OPEN...";
	private static final String EXPORT_JSON = "EXPORT JSON...";
	private static final String IMPORT_JSON = "IMPORT JSON...";
	
	/**
	 * Button for activating save/load settings and any other settings added in the future
	 */
	private void addMenuButton(Stage stage) {
		String[] options = {
				NEW_PROJECT,
				SELECT_PROJECT_DIRECTORY,
				SAVE,
				OPEN,
				EXPORT_JSON,
				IMPORT_JSON
		};
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, shadow, "FILE", robotoRegular20, robotoLight20, options, MENU_BUTTON_INCREMENT_HEIGHT) {
			@Override
			public void optionClicked(MouseEvent event, String optionName, int optionIndex) {
				switch(optionName) {
				case NEW_PROJECT:
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Start New Project");
					alert.setHeaderText("Start new project? Unsaved changes will be lost.");
					((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
					
					Optional<ButtonType> result = alert.showAndWait();
					
					if (result.get() == ButtonType.OK){
						newProject();
					}
					
					break;
				case SELECT_PROJECT_DIRECTORY:
					setProjectDirectory(stage);
					break;
				case SAVE:
					exportProject(stage, new JDialogueSerializerIO());
					break;
				case OPEN:
					importProject(stage, new JDialogueSerializerIO());
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
		
		project = new Project(0.0, -PANNABLE_PANE_HEIGHT/2, 1.0);
		
		if (!projectNull) {
			refreshAfterImport();
		}
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

		String projectDirectory = props.getProperty("projectDir");
		
		File defaultLocation = (projectDirectory != null ? new File(projectDirectory) : new File("."));

		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Select Project Directory");
		fileChooser.setInitialDirectory(defaultLocation);

		File dir = fileChooser.showDialog(stage);

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
		
		return (projectDirectory != null ? new File(projectDirectory) : new File("."));
	}
	
	/**
	 * Opens an export dialog for the selected JDialogueIO and runs it once a file is selected
	 */
	private void exportProject(Stage stage, JDialogueIO behavior) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save " + behavior.getTypeName() + " file");
		fileChooser.setInitialDirectory(getProjectDirectory());
		fileChooser.setInitialFileName(project.getName() + "." + behavior.getTypeName());
		fileChooser.getExtensionFilters().add(behavior.getExtensionFilter());

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
		fileChooser.setInitialDirectory(getProjectDirectory());
		fileChooser.getExtensionFilters().add(behavior.getExtensionFilter());
		
		File f = fileChooser.showOpenDialog(stage);

		if (f != null) {
			try {
				Project project = behavior.importProject(f);
				
				pannablePane.getChildren().clear();
				this.project = project;
				refreshAfterImport();
				
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
	 * After a Project is imported, run this to update the editor to contain its contents.
	 */
	private void refreshAfterImport() {
		pannablePane.setTranslateX(project.getViewportX());
		pannablePane.setTranslateY(project.getViewportY());
		pannablePane.setScale(project.getViewportScale());
		
		projectNameField.setText(project.getName());

		//Build all of the DialogueNodePanes (graphical representation of node)
		for (int i = 0; i < project.getNumNodes(); i++) {
			DialogueNode node = project.getNode(i);
			createDialogueNodePane(node);
		}

		//Build all BoundLine objects for each Connection
		for (int i = 0; i < project.getNumConnections(); i++) {
			Connection connection = project.getConnection(i);
			
			DialogueNodeConnector connector1 = connection.getConnector1();
			DialogueNodeConnector connector2 = connection.getConnector2();
			
			DialogueNodeConnectorArc arc1 = getDialogueNodeConnectorArcOf(connector1);
			DialogueNodeConnectorArc arc2 = getDialogueNodeConnectorArcOf(connector2);
			
			if (arc1 != null && arc2 != null) {
				BoundLine line = new BoundLine(arc1, connector1, arc2, connector2);

				addConnectorLine(line);
				
			}else {
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
	
	private static final String REPLACE = "REPLACE...";
	private static final String MULTIREPLACE = "MULTI-REPLACE...";
	
	/**
	 * Button for adding various tools to the editor
	 */
	private void addToolsButton(Stage stage) {
		int buttonX = BUTTON_START_X + BUTTON_WIDTH + 10;
		
		String[] options = { 
				REPLACE,
				MULTIREPLACE
		};
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, shadow, "TOOL", robotoRegular20, robotoLight20, options, MENU_BUTTON_INCREMENT_HEIGHT) {
			
			@Override
			public void optionClicked(MouseEvent event, String optionName, int optionIndex) {
				switch(optionName) {
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
				}
			}
		};
		
		//Add to pane
		button.setLayoutX(buttonX);
		button.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(button);
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
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, shadow, "+NODE", robotoRegular20, robotoLight20, options, MENU_BUTTON_INCREMENT_HEIGHT) {
			
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
			dialogueNodePane = new DialogueTextNodePane(this, (DialogueTextNode) dialogueNode, shadow, robotoRegular20, markazi12);
		}
		
		if (dialogueNode instanceof DialogueResponseNode) {
			dialogueNodePane = new DialogueResponseNodePane(this, (DialogueResponseNode) dialogueNode, shadow, robotoRegular20, markazi12, 45);
		}
		
		if (dialogueNodePane != null) {
			
			//Add node to current instance (UI)
	        dialogueNodePane.setTranslateX(dialogueNode.getX());
	        dialogueNodePane.setTranslateY(dialogueNode.getY());
	        dialogueNodePane.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
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
		
		ButtonSkeleton projectNameFieldButton = new ButtonSkeleton(300, BUTTON_HEIGHT, shadow);
		
		//Project name text field
		projectNameField = new TextField(project.getName());
		projectNameField.setFont(robotoRegular20);
		projectNameField.setBackground(Background.EMPTY);
		projectNameField.setStyle("-fx-text-inner-color: " + Button.getTextColorCode() + "; -fx-border-color: " + Button.getTextColorCode() + "; -fx-border-width: 0 0 1 0;");
		projectNameField.setLayoutX(Button.BUTTON_MARGIN_X);
		projectNameField.setLayoutY(6); //manually measured
		
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

		//Add to button skeleton
		projectNameFieldButton.getChildren().add(projectNameField);
		
		//Add to pane
		projectNameFieldButton.setLayoutX(buttonX);
		projectNameFieldButton.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(projectNameFieldButton);
	}

	public Scene getScene() {
		return scene;
	}

	public PannablePane getPannablePane() {
		return pannablePane;
	}
	
	public Pane getUIPane() {
		return uiPane;
	}
}
