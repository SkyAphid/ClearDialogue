package nokori.jdialogue;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import nokori.jdialogue.ui.Button;
import nokori.jdialogue.ui.ButtonSkeleton;
import nokori.jdialogue.ui.DialogueNodeFX;
import nokori.jdialogue.util.RectangleResizeHeightTransition;

/**dasdas
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
	private Pane pane;
	private Scene scene;
	
	//styling
	private static final int BUTTON_START_X = 20;
	private static final int BUTTON_Y = 20;
	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 50;
	private static final int BUTTON_ARC = 5;
	
	private DropShadow shadow;
	
	private Font replicaPro20 = Font.loadFont("file:ReplicaProRegular.otf", 20);
	private Font monaco12 = Font.loadFont("file:Monaco.ttf", 12);
	
	//Menu button
	private static final int MENU_BUTTON_EXPANDED_HEIGHT = 300;
	private boolean menuButtonSelected = false;
	
	//Project data
	private Project project = new Project();
	
	//Instance data
	private DialogueNodeFX selectedNode = null;
	private ArrayList<DialogueNodeFX> nodes = new ArrayList<DialogueNodeFX>();
	
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Start program
	 */
	@Override
	public void start(Stage primaryStage) {
		pane = new Pane();
		scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
		
		initializeShadows();
		
		addBackground();
		addProgramInfo();
		addMenuButton();
		addNodeButton();
		addProjectNameField();
		
		pane.requestFocus();
		pane.setOnMouseClicked(event -> {
			pane.requestFocus();
		});
		
		
		/*Rectangle startBox = createNode(50, 50);
		startBox.setX(50);
		startBox.setY(50);

		Rectangle endBox = createNode(50, 50);
		endBox.setX(350);
		endBox.setY(200);

		Line connector = createConnector(startBox, endBox);
		
		pane.getChildren().addAll(startBox, endBox, connector);*/
		
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
		pane.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, null, null)));
	}

	/**
	 * Add info on the program to the bottom-left corner of the screen
	 */
	private void addProgramInfo() {
		int offsetY = 20;
		
		Text text = new Text(PROGRAM_NAME + " " + PROGRAM_VERSION);
		text.setFont(replicaPro20);
		text.setFill(Color.LIGHTGRAY.darker());
		text.setX(20);
		text.setY(WINDOW_HEIGHT - offsetY);
		
		//Clip to bottom-left
		pane.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
			text.setY(newValue.getHeight() - offsetY);
		});
		
		//Add to pane
		pane.getChildren().add(text);
	}
	
	/**
	 * Button for activating save/load settings and any other settings I decide to add in the future
	 */
	private void addMenuButton() {
		Button button = new Button(BUTTON_START_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_ARC, shadow, "FILE", replicaPro20) {
			@Override
			public void mouseEntered(MouseEvent event, Rectangle background) {
				super.mouseEntered(event, background);
				
				new RectangleResizeHeightTransition(Duration.millis(Button.FADE_TIME), background, MENU_BUTTON_EXPANDED_HEIGHT).play();
				menuButtonSelected = true;
			}
			
			@Override
			public void mouseExited(MouseEvent event, Rectangle background) {
				super.mouseExited(event, background);
				
				new RectangleResizeHeightTransition(Duration.millis(Button.FADE_TIME), background, BUTTON_HEIGHT).play();
				menuButtonSelected = false;
			}
			
			@Override
			public void mouseClicked(MouseEvent event, Rectangle background) {
				if (menuButtonSelected) {
					
				}else {
					
				}
			}
		};
		
		//Add to pane
		pane.getChildren().add(button.getStackPane());
	}
	
	/**
	 * Button for adding new story nodes
	 */
	private void addNodeButton() {
		Button button = new Button(BUTTON_START_X + BUTTON_WIDTH + 10, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, BUTTON_ARC, shadow, "+ NODE", replicaPro20) {
			@Override
			public void mouseClicked(MouseEvent event, Rectangle background) {
				
				double nodeX = pane.getWidth()/2 - DialogueNodeFX.DIALOGUE_NODE_WIDTH/2;
				double nodeY = pane.getHeight()/2 - DialogueNodeFX.DIALOGUE_NODE_HEIGHT/2;
				DialogueNode dialogueNode = new DialogueNode("Node " + project.getNodes().size(), nodeX, nodeY);
				project.addNode(dialogueNode);
				
				newDialogueNodeFX(dialogueNode);
			}
		};
		
		pane.getChildren().add(button.getStackPane());
	}
	
	/**
	 * Adds a new DialogueNodeFX to the instance.
	 * @param dialogueNode
	 */
	private void newDialogueNodeFX(DialogueNode dialogueNode) {
		DialogueNodeFX dialogueNodeFX = new DialogueNodeFX(this, dialogueNode, shadow, replicaPro20, monaco12);
		nodes.add(dialogueNodeFX);
		pane.getChildren().add(dialogueNodeFX.getDraggablePane());
	}
	
	/**
	 * @return the currently selected DialogueNodeFX, used for connecting nodes together.
	 */
	public DialogueNodeFX getSelectedNode() {
		return selectedNode;
	}
	
	/**
	 * Project name field for customizing the name of the Project
	 */
	private void addProjectNameField() {
		ButtonSkeleton projectNameField = new ButtonSkeleton(BUTTON_START_X + ((BUTTON_WIDTH + 10) * 2), BUTTON_Y, 300, BUTTON_HEIGHT, BUTTON_ARC, shadow);
		
		//Project name text field
		TextField textField = new TextField(project.getName());
		textField.setFont(replicaPro20);
		textField.setBackground(Background.EMPTY);
		textField.setStyle("-fx-text-inner-color: " + Button.getTextColorCode() + ";");
		
		textField.setOnInputMethodTextChanged(event -> {
			project.setName(event.getCommitted());
		});

		//Add to button skeleton
		projectNameField.getStackPane().getChildren().add(textField);
		
		//Add to pane
		pane.getChildren().add(projectNameField.getStackPane());
	}
}
