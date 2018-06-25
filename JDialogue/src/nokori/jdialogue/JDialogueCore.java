package nokori.jdialogue;

import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.throwable.MissingDialogueNodePaneError;
import nokori.jdialogue.ui.Button;
import nokori.jdialogue.ui.ButtonSkeleton;
import nokori.jdialogue.ui.MenuButton;
import nokori.jdialogue.ui.node.ConnectorSelection;
import nokori.jdialogue.ui.node.DialogueNodePane;
import nokori.jdialogue.ui.node.DialogueResponseNodePane;
import nokori.jdialogue.ui.node.DialogueTextNodePane;
import nokori.jdialogue.ui.pannable_pane.NodeGestures;
import nokori.jdialogue.ui.pannable_pane.PannablePane;
import nokori.jdialogue.ui.pannable_pane.SceneGestures;

/**
 * The Core of this program, containing the GUI.
 * 
 * UI design inspired by Yarn:
 * https://github.com/InfiniteAmmoInc/Yarn
 * 
 * I apologize in advance for weird uses of JavaFX, this was my first time using it, 
 * but I wanted to make something that works right out of the box and this was my best bet.
 * 
 * The system is pretty customizable, so if you see anything you don't like, 
 * it should be pretty easy to plug and play the various parts.
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
 * 4) Hook up to JDialogueCore (addNodeButton(), addDialogueNode())
 * 
 * it just werkz
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
	
	//styling
	public static final int BUTTON_START_X = 20;
	public static final int BUTTON_Y = 20;
	public static final int BUTTON_WIDTH = 150;
	public static final int BUTTON_HEIGHT = 50;
	
	public static final int ROUNDED_RECTANGLE_ARC = 5;
	
	private static final int MENU_BUTTON_INCREMENT_HEIGHT = 50;
	
	private DropShadow shadow;
	
	private Font replicaProRegular20 = Font.loadFont("file:ReplicaProRegular.otf", 20);
	private Font replicaProLight20 = Font.loadFont("file:ReplicaProLight.otf", 20);
	private Font monaco12 = Font.loadFont("file:Monaco.ttf", 12);
	
	//Project data
	private Project project = new Project();
	
	//Connector management
	private ConnectorSelection selectedConnector = null;
	
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Start program
	 */
	@Override
	public void start(Stage primaryStage) {
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
		pannablePane = new PannablePane(PANNABLE_PANE_WIDTH, PANNABLE_PANE_HEIGHT);
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
		
		pannablePane.setTranslateX(-PANNABLE_PANE_WIDTH/2);
		pannablePane.setTranslateY(-PANNABLE_PANE_HEIGHT/2);
		
		uiPane.getChildren().add(pannablePane);
		
		//Scene
		scene = new Scene(uiPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.getStylesheets().add("file:scrollbar_style.css");
		
		//Configure pannable pane mouse gestures
        SceneGestures sceneGestures = new SceneGestures(pannablePane) {
        	@Override
        	public void mouseDragged(MouseEvent event) {
        		//Drag the grabbing hand when you pane the screen
        		scene.setCursor(Cursor.CLOSED_HAND);
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
						((DialogueNodePane) n).updateConnectors(event, pannablePane);
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
		addMenuButton();
		addNodeButton();
		addProjectNameField();

		uiPane.requestFocus();
		
		/*
		 * Finalize
		 */
		
		primaryStage.setTitle(PROGRAM_NAME);
		primaryStage.setScene(scene);
		primaryStage.show();
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
		
		Text text = new Text(PROGRAM_NAME + " " + PROGRAM_VERSION + " | Hold LMB = Drag Node | 2xLMB = Edit Node | 2xRMB = Delete Node");
		text.setFont(replicaProRegular20);
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
	
	/**
	 * Button for activating save/load settings and any other settings added in the future
	 */
	private void addMenuButton() {
		//Add new import/export options here and add functionality below
		String[] options = {
				"SAVE...",
				"LOAD..."
		};
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, shadow, "FILE", replicaProRegular20, replicaProLight20, options, MENU_BUTTON_INCREMENT_HEIGHT);
		
		//Add to pane
		button.setLayoutX(BUTTON_START_X);
		button.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(button);
	}
	
	/**
	 * Button for adding new story nodes, easily expanded (see below)
	 */
	private void addNodeButton() {
		int buttonX = BUTTON_START_X + BUTTON_WIDTH + 10;
		
		String[] options = { 
				"DIALOGUE...",
				"RESPONSE..."
		};
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, shadow, "+NODE", replicaProRegular20, replicaProLight20, options, MENU_BUTTON_INCREMENT_HEIGHT) {
			
			@Override
			public void optionClicked(MouseEvent event, String optionName, int optionIndex) {
				double nodeX = -pannablePane.getTranslateX() + uiPane.widthProperty().get()/2 - DialogueNodePane.WIDTH/2;
				double nodeY = -pannablePane.getTranslateY() + uiPane.heightProperty().get()/2 - DialogueNodePane.HEIGHT/2;
				int totalNodes = project.getNodes().size();
				
				DialogueNode node = null;
				
				switch(optionIndex) {
				case 0:
					node = new DialogueTextNode("[" + totalNodes +"] Dialogue Node", nodeX, nodeY);
					break;
				case 1:
					node = new DialogueResponseNode("[" + totalNodes +"] Response Node", nodeX, nodeY);
					break;
				}
				
				addDialogueNode(node);
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
	private void addDialogueNode(DialogueNode dialogueNode) {
		DialogueNodePane dialogueNodePane = null;
		
		if (dialogueNode instanceof DialogueTextNode) {
			dialogueNodePane = new DialogueTextNodePane(this, (DialogueTextNode) dialogueNode, shadow, replicaProRegular20, monaco12);
		}
		
		if (dialogueNode instanceof DialogueResponseNode) {
			dialogueNodePane = new DialogueResponseNodePane(this, (DialogueResponseNode) dialogueNode, shadow, replicaProRegular20, monaco12, 30);
		}
		
		if (dialogueNodePane != null) {
			
			//Add node to project data
			project.addNode(dialogueNode);
			
			//Add node to current instance (UI)
	        dialogueNodePane.setTranslateX(dialogueNode.getX());
	        dialogueNodePane.setTranslateY(dialogueNode.getY());
	        dialogueNodePane.addEventFilter(MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
	        dialogueNodePane.addEventFilter(MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
	        
			pannablePane.getChildren().add(dialogueNodePane);
			
		} else {
			throw new MissingDialogueNodePaneError(dialogueNode);
		}
	}
	
	/**
	 * Removes a dialogue node
	 * @param dialogueNode
	 */
	public void removeDialogueNode(DialogueNodePane dialogueNodePane) {
		project.removeNode(dialogueNodePane.getNode());
		pannablePane.getChildren().remove(dialogueNodePane);
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
	 * Project name field for customizing the name of the Project
	 */
	private void addProjectNameField() {
		int buttonX = BUTTON_START_X + ((BUTTON_WIDTH + 10) * 2);
		
		ButtonSkeleton projectNameField = new ButtonSkeleton(300, BUTTON_HEIGHT, shadow);
		
		//Project name text field
		TextField textField = new TextField(project.getName());
		textField.setFont(replicaProRegular20);
		textField.setBackground(Background.EMPTY);
		textField.setStyle("-fx-text-inner-color: " + Button.getTextColorCode() + "; -fx-border-color: " + Button.getTextColorCode() + "; -fx-border-width: 0 0 1 0;");
		textField.setLayoutX(Button.BUTTON_MARGIN_X);
		textField.setLayoutY(6); //manually measured
		
		//Update project name 
		textField.textProperty().addListener((o, oldText, newText) -> {
			project.setName(newText);
		});
		
		//having enter cancel out the focus gives a feeling of confirmation
		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				uiPane.requestFocus();
			}
		});

		//Add to button skeleton
		projectNameField.getChildren().add(textField);
		
		//Add to pane
		projectNameField.setLayoutX(buttonX);
		projectNameField.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(projectNameField);
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
