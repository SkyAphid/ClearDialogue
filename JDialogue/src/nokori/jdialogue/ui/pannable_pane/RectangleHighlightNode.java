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
		setOpacity(0.5);
		setMouseTransparent(true);
	}
	
	public void update(double mouseX, double mouseY) {
		setWidth(mouseX - startX);
		setHeight(mouseY - startY);
	}
}
