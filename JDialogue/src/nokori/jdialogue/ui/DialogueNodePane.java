package nokori.jdialogue.ui;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNode;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueNodePane extends StackPane {
	public static final int WIDTH = 200;
	public static final int HEIGHT = 200;
	
	public static final int DIALOGUE_NODE_RIBBON_HEIGHT = 40;
	
	protected DialogueNode node;

	public DialogueNodePane(JDialogueCore core, DialogueNode node, DropShadow shadow, Font titleFont) {
		this.node = node;
		
		//Outline for highlighting node
		Rectangle outline = new Rectangle(WIDTH, HEIGHT);
		outline.setFill(Color.TRANSPARENT);
		outline.setStroke(Color.CORAL);
		outline.setOpacity(0.0);
		outline.setMouseTransparent(true);
		
		//Background
		Rectangle background = new Rectangle(WIDTH, HEIGHT);
		background.setFill(Color.WHITE);
		background.setStroke(Color.LIGHTGRAY);
		background.setEffect(shadow);
		background.setMouseTransparent(true);
		
		//Title text
		Text title = new Text(node.getName());
		title.setFont(titleFont);
		title.setFill(Color.BLACK);
		title.setMouseTransparent(true);
		
		StackPane.setAlignment(title, Pos.TOP_CENTER);
		StackPane.setMargin(title, new Insets(10, 0, 0, 0));
		
		//Separator
		Line separator = new Line();
		separator.setFill(Color.BLACK);
		separator.setStartX(background.getLayoutX());
		separator.setStartY(background.getLayoutY());
		separator.endXProperty().bind(background.layoutXProperty().add(WIDTH - 20));
		separator.endYProperty().bind(background.layoutYProperty());
		separator.setMouseTransparent(true);
		
		StackPane.setAlignment(separator, Pos.TOP_CENTER);
		StackPane.setMargin(separator, new Insets(DIALOGUE_NODE_RIBBON_HEIGHT, 0, 0, 0));
		
		//Configure pane
		getChildren().addAll(background, title, separator, outline);
		
		setOnMouseEntered(event -> {
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(Button.FADE_TIME), outline);
			fadeTransition.setFromValue(outline.getOpacity());
			fadeTransition.setToValue(1.0);
			fadeTransition.play();
		});
		
		setOnMouseExited(event -> {
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(Button.FADE_TIME), outline);
			fadeTransition.setFromValue(outline.getOpacity());
			fadeTransition.setToValue(0.0);
			fadeTransition.play();
		});
		
		//Initial animation
		int animTime = 200;
		
		RotateTransition rotateTransition = new RotateTransition(Duration.millis(animTime), this);
		rotateTransition.setFromAngle(45.0);
		rotateTransition.setToAngle(0.0);
		rotateTransition.play();
		
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(animTime), this);
		fadeTransition.setFromValue(0.0);
		fadeTransition.setToValue(1.0);
		fadeTransition.play();
	}
	
	public DialogueNode getNode() {
		return node;
	}
	
	/**
	 * Create a connector between two nodes
	 */
	
	private Line createDialogueNodeConnector(Node start, Node end) {
		Line l = new Line();

		l.startXProperty().bind(Bindings.createDoubleBinding(() -> start.getBoundsInParent().getMaxX(), start.boundsInParentProperty()));
		l.startYProperty().bind(Bindings.createDoubleBinding(() -> start.getBoundsInParent().getMinY() + start.getBoundsInParent().getHeight() / 2, start.boundsInParentProperty()));
		l.endXProperty().bind(Bindings.createDoubleBinding(() -> end.getBoundsInParent().getMinX(), end.boundsInParentProperty()));
		l.endYProperty().bind(Bindings.createDoubleBinding(() -> end.getBoundsInParent().getMinY() + end.getBoundsInParent().getHeight() / 2, end.boundsInParentProperty()));

		return l;

	}
}
