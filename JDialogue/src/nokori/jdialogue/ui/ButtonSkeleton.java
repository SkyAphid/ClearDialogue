package nokori.jdialogue.ui;

import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nokori.jdialogue.JDialogueCore;

public class ButtonSkeleton extends Pane {
	
	protected Rectangle rectangle;
	
	public ButtonSkeleton(int w, int h, DropShadow shadow) {
		//Rectangle style
		rectangle = new Rectangle(w, h);
		rectangle.setFill(Color.CORAL);
		rectangle.setArcHeight(JDialogueCore.ROUNDED_RECTANGLE_ARC);
		rectangle.setArcWidth(JDialogueCore.ROUNDED_RECTANGLE_ARC);
		rectangle.setEffect(shadow);
		rectangle.setLayoutX(0);
		rectangle.setLayoutY(0);
		
		//Compile to pane
		getChildren().add(rectangle);
		
		//Events
		setOnMouseEntered(event -> {
			mouseEntered(event);
		});
		
		setOnMouseExited(event -> {
			mouseExited(event);
		});
		
		setOnMouseClicked(event -> {
			mouseClicked(event);
		});
		
		setOnMouseMoved(event -> {
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
}
