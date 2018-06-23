package nokori.jdialogue.ui;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nokori.jdialogue.DialogueNode;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.util.DraggableStackPane;

/**
 * This is the GUI representation of a DialogueNode.
 * 
 * It doesn't store any actual dialogue data, it's just the GUI representation of that data.
 *
 */
public class DialogueNodeFX {
	public static final int DIALOGUE_NODE_WIDTH = 200;
	public static final int DIALOGUE_NODE_HEIGHT = 200;
	
	public static final int DIALOGUE_NODE_RIBBON_HEIGHT = 40;
	
	private DialogueNode node;
	
	private DraggableStackPane draggablePane;

	public DialogueNodeFX(JDialogueCore core, DialogueNode node, DropShadow shadow, Font titleFont, Font textFont) {
		this.node = node;
		
		//Outline for highlighting node
		Rectangle outline = new Rectangle(DIALOGUE_NODE_WIDTH, DIALOGUE_NODE_HEIGHT);
		outline.setFill(Color.TRANSPARENT);
		outline.setStroke(Color.CORAL);
		outline.setOpacity(0.0);
		outline.setMouseTransparent(true);
		
		//Background
		Rectangle background = new Rectangle(DIALOGUE_NODE_WIDTH, DIALOGUE_NODE_HEIGHT);
		background.setFill(Color.WHITE);
		background.setStroke(Color.LIGHTGRAY);
		background.setEffect(shadow);
		background.setMouseTransparent(true);
		
		//Title text
		Text title = new Text(node.getName());
		title.setFont(titleFont);
		title.setFill(Color.DARKSLATEGRAY);
		title.setMouseTransparent(true);
		
		StackPane.setAlignment(title, Pos.TOP_CENTER);
		StackPane.setMargin(title, new Insets(10, 0, 0, 0));
		
		//Separator
		Line separator = new Line();
		separator.setFill(Color.DARKSLATEGRAY);
		separator.setStartX(background.getLayoutX());
		separator.setStartY(background.getLayoutY());
		separator.endXProperty().bind(background.layoutXProperty().add(DIALOGUE_NODE_WIDTH - 20));
		separator.endYProperty().bind(background.layoutYProperty());
		separator.setMouseTransparent(true);
		
		StackPane.setAlignment(separator, Pos.TOP_CENTER);
		StackPane.setMargin(separator, new Insets(DIALOGUE_NODE_RIBBON_HEIGHT, 0, 0, 0));
		
		//Body Text Viewer
		Label label = new Label(node.getText());
		label.setMaxWidth(DIALOGUE_NODE_WIDTH - 20f);
		label.setMaxHeight(DIALOGUE_NODE_HEIGHT - DIALOGUE_NODE_RIBBON_HEIGHT - 20f); 
		label.setFont(textFont);
		label.setBackground(Background.EMPTY);
		label.setWrapText(true);
		label.setMouseTransparent(true);
		
		StackPane.setAlignment(label, Pos.BOTTOM_CENTER);
		StackPane.setMargin(label, new Insets(0, 10, 10, 10));
		
		//DraggablePane
		draggablePane = new DraggableStackPane(background, title, separator, label, outline) {
			@Override
			public void mouseDragged(MouseEvent event, double newX, double newY) {
				node.setX(newX);
				node.setY(newY);
			}
			
			@Override
			public void mouseClicked(MouseEvent event) {
				
			}
		};
		
		draggablePane.setOnMouseEntered(event -> {
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(Button.FADE_TIME), outline);
			fadeTransition.setFromValue(outline.getOpacity());
			fadeTransition.setToValue(1.0);
			fadeTransition.play();
		});
		
		draggablePane.setOnMouseExited(event -> {
			FadeTransition fadeTransition = new FadeTransition(Duration.millis(Button.FADE_TIME), outline);
			fadeTransition.setFromValue(outline.getOpacity());
			fadeTransition.setToValue(0.0);
			fadeTransition.play();
		});
		
		draggablePane.setLayoutX(node.getX());
		draggablePane.setLayoutY(node.getY());
	}
	
	public DialogueNode getNode() {
		return node;
	}
	
	public DraggableStackPane getDraggablePane() {
		return draggablePane;
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
