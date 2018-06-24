package nokori.jdialogue;

import javafx.application.Application;
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
import nokori.jdialogue.ui.DialogueNodePane;
import nokori.jdialogue.ui.DialogueResponseNodePane;
import nokori.jdialogue.ui.DialogueTextNodePane;
import nokori.jdialogue.ui.MenuButton;
import nokori.jdialogue.ui.pannable_pane.NodeGestures;
import nokori.jdialogue.ui.pannable_pane.PannablePane;
import nokori.jdialogue.ui.pannable_pane.SceneGestures;

/**
 * The Core of this program, containing the GUI.
 * 
 * Inspired by this example:
 * https://stackoverflow.com/questions/39556757/best-easiest-way-to-implement-node-based-graphical-gui
 * 
 * UI design inspired by Yarn:
 * https://github.com/InfiniteAmmoInc/Yarn
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
	private PannablePane pannablePane;
	private NodeGestures nodeGestures;
	
	private Scene scene;
	
	//styling
	private static final int BUTTON_START_X = 20;
	private static final int BUTTON_Y = 20;
	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 50;
	private static final int BUTTON_ARC = 5;
	
	private static final int MENU_BUTTON_INCREMENT_HEIGHT = 50;
	
	private DropShadow shadow;
	
	private Font replicaProRegular20 = Font.loadFont("file:ReplicaProRegular.otf", 20);
	private Font replicaProLight20 = Font.loadFont("file:ReplicaProLight.otf", 20);
	private Font monaco12 = Font.loadFont("file:Monaco.ttf", 12);
	
	//Project data
	private Project project = new Project();
	
	//Instance data
	private DialogueNodePane selectedNode = null;
	
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
		 */
		
		//Panes and pane accessories, I tell ya what
		uiPane = new Pane();
		
		uiPane.setOnMouseClicked(event -> {
			uiPane.requestFocus();
		});
		
		pannablePane = new PannablePane();

		uiPane.getChildren().add(pannablePane);
		
		//Scene
		scene = new Scene(uiPane, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		//Configure pannable pane
        SceneGestures sceneGestures = new SceneGestures(pannablePane);
        scene.setOnMouseDragged(sceneGestures.getOnMouseDraggedEventHandler());
        scene.setOnMousePressed(sceneGestures.getOnMousePressedEventHandler());
        
		//scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		//scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
		
		nodeGestures = new NodeGestures(pannablePane);
		
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
		
		
		/*Rectangle startBox = createNode(50, 50);
		startBox.setX(50);
		startBox.setY(50);

		Rectangle endBox = createNode(50, 50);
		endBox.setX(350);
		endBox.setY(200);

		Line connector = createConnector(startBox, endBox);
		
		pane.getChildren().addAll(startBox, endBox, connector);*/
		
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
		
		Text text = new Text(PROGRAM_NAME + " " + PROGRAM_VERSION);
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
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_ARC, shadow, "FILE", replicaProRegular20, replicaProLight20, options, MENU_BUTTON_INCREMENT_HEIGHT);
		
		//Add to pane
		Pane buttonPane = button.getPane();
		buttonPane.setLayoutX(BUTTON_START_X);
		buttonPane.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(buttonPane);
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
		
		MenuButton button = new MenuButton(scene, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_ARC, shadow, "+NODE", replicaProRegular20, replicaProLight20, options, MENU_BUTTON_INCREMENT_HEIGHT) {
			
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
		Pane buttonPane = button.getPane();
		buttonPane.setLayoutX(buttonX);
		buttonPane.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(buttonPane);
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
	 * @return the currently selected DialogueNodeFX, used for connecting nodes together.
	 */
	public DialogueNodePane getSelectedNode() {
		return selectedNode;
	}
	
	/**
	 * Project name field for customizing the name of the Project
	 */
	private void addProjectNameField() {
		int buttonX = BUTTON_START_X + ((BUTTON_WIDTH + 10) * 2);
		
		ButtonSkeleton projectNameField = new ButtonSkeleton(300, BUTTON_HEIGHT, BUTTON_ARC, shadow);
		
		//Project name text field
		TextField textField = new TextField(project.getName());
		textField.setFont(replicaProRegular20);
		textField.setBackground(Background.EMPTY);
		textField.setStyle("-fx-text-inner-color: " + Button.getTextColorCode() + ";");
		textField.setLayoutX(Button.BUTTON_MARGIN_X);
		textField.setLayoutY(6); //manually measured
		
		//Update project name 
		textField.setOnKeyTyped(event -> {
			project.setName(textField.getText());
		});
		
		//having enter cancel out the focus gives a feeling of confirmation
		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				uiPane.requestFocus();
			}
		});

		//Add to button skeleton
		Pane buttonPane = projectNameField.getPane();
		
		projectNameField.getPane().getChildren().add(textField);
		
		//Add to pane
		buttonPane.setLayoutX(buttonX);
		buttonPane.setLayoutY(BUTTON_Y);
		
		uiPane.getChildren().add(buttonPane);
	}
}
