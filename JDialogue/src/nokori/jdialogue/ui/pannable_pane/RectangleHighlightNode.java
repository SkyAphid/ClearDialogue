package nokori.jdialogue.ui.pannable_pane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class RectangleHighlightNode extends Rectangle {
	
	private double startX, startY;
	
	public RectangleHighlightNode(double startX, double startY) {
		this.startX = startX;
		this.startY = startY;
		
		setTranslateX(startX);
		setTranslateY(startY);
		
		setFill(Color.LIGHTCORAL);
		setStroke(Color.CORAL);
		setOpacity(0.25);
		setMouseTransparent(true);
		
		setWidth(0);
		setHeight(0);
	}
	
	public void update(double mouseX, double mouseY) {
		if (mouseX < startX) {
			setTranslateX(mouseX);
			setWidth(startX - mouseX);
		} else {
			setTranslateX(startX);
			setWidth(mouseX - startX);
		}
		
		if (mouseY < startY) {
			setTranslateY(mouseY);
			setHeight(startY - mouseY);
		} else {
			setTranslateY(startY);
			setHeight(mouseY - startY);
		}
	}
}
