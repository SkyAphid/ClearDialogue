package nokori.jdialogue.ui;

import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ButtonSkeleton {
	
	protected Rectangle rectangle;
	protected Pane pane;
	
	public ButtonSkeleton(int w, int h, int arc, DropShadow shadow) {
		//Rectangle style
		rectangle = new Rectangle(w, h);
		rectangle.setFill(Color.CORAL);
		rectangle.setArcHeight(arc);
		rectangle.setArcWidth(arc);
		rectangle.setEffect(shadow);
		rectangle.setLayoutX(0);
		rectangle.setLayoutY(0);
		
		//Compile to pane
		pane = new Pane();
		pane.getChildren().add(rectangle);
		
		//Events
		pane.setOnMouseEntered(event -> {
			mouseEntered(event);
		});
		
		pane.setOnMouseExited(event -> {
			mouseExited(event);
		});
		
		pane.setOnMouseClicked(event -> {
			mouseClicked(event);
		});
		
		pane.setOnMouseMoved(event -> {
			mouseMoved(event);
		});
	}
	
	public void mouseEntered(MouseEvent event) {
		
	}
	
	public void mouseExited(MouseEvent event) {
		
	}
	
	public void mouseClicked(MouseEvent event) {
		
	}
	
	public void mouseMoved(MouseEvent event) {
		
	}
	
	public Rectangle getBackgroundRectangle() {
		return rectangle;
	}
	public Pane getPane() {
		return pane;
	}
}
