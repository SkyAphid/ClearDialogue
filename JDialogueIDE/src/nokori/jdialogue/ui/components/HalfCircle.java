package nokori.jdialogue.ui.components;

import lwjgui.Color;
import lwjgui.scene.shape.SectorCircle;

public class HalfCircle extends SectorCircle {

	public HalfCircle(Color fill, float radius, boolean flipped, int circlePoints) {
		super(fill, radius, 0.5f, flipped ? (float) (Math.PI + Math.PI/2) : (float) Math.PI/2, circlePoints);
		
		if (flipped) {
			renderOffset.set(-radius, 0);
		}
	}

	@Override
	protected void resize() {
		setMaxSize(radius, radius*2);
		setPrefSize(getMaxWidth(), getMaxHeight());
	}
}
