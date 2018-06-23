package nokori.jdialogue.ui;

import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ButtonSkeleton {
	
	protected Rectangle rectangle;
	protected StackPane stackPane;
	
	public ButtonSkeleton(int x, int y, int w, int h, int arc, DropShadow shadow) {
		//Rectangle style
		rectangle = new Rectangle(w, h);
		rectangle.setFill(Color.CORAL);
		rectangle.setArcHeight(arc);
		rectangle.setArcWidth(arc);
		rectangle.setEffect(shadow);

		//Events
		rectangle.setOnMouseEntered(event -> {
			mouseEntered(event, rectangle);
		});
		
		rectangle.setOnMouseExited(event -> {
			mouseExited(event, rectangle);
		});
		
		rectangle.setOnMouseClicked(event -> {
			mouseClicked(event, rectangle);
		});
		
		//Compile to StackPane
		stackPane = new StackPane();
		stackPane.getChildren().add(rectangle);
		stackPane.setTranslateX(x);
		stackPane.setTranslateY(y);
	}
	
	public void mouseEntered(MouseEvent event, Rectangle background) {
		
	}
	
	public void mouseExited(MouseEvent event, Rectangle background) {
		
	}
	
	public void mouseClicked(MouseEvent event, Rectangle background) {
		
	}
	
	public Rectangle getBackgroundRectangle() {
		return rectangle;
	}
	
	public StackPane getStackPane() {
		return stackPane;
	}
}
