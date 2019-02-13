package nokori.jdialogue.ui.components;

import lwjgui.Color;
import lwjgui.scene.shape.Rectangle;
import lwjgui.transition.Transition;

public class RectangleFillTransition extends Transition {

	private Rectangle rectangle;
	private Color fromFill, toFill, storeFill;
	
	public RectangleFillTransition(long durationInMillis, Rectangle rectangle, Color fromFill, Color toFill) {
		super(durationInMillis);
		this.rectangle = rectangle;
		this.fromFill = fromFill;
		this.toFill = toFill;
		
		storeFill = new Color(fromFill);
		
	}

	@Override
	public void tick(double progress) {
		rectangle.setFill(Color.blend(fromFill, toFill, storeFill, progress));
	}

}
