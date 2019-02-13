package nokori.jdialogue.ui.transitions;

import lwjgui.Color;
import lwjgui.scene.control.Label;
import lwjgui.transition.Transition;

public class LabelFillTransition extends Transition {
	
	private Label label;
	private Color fromFill, toFill, storeFill;
	
	public LabelFillTransition(long durationInMillis, Label label, Color fromFill, Color toFill) {
		super(durationInMillis);
		this.label = label;
		this.fromFill = fromFill;
		this.toFill = toFill;
		
		storeFill = new Color(fromFill);
	}

	@Override
	public void tick(double progress) {
		label.setTextFill(Color.blend(fromFill, toFill, storeFill, progress));
	}
}
