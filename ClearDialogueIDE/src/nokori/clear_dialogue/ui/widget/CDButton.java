package nokori.clear_dialogue.ui.widget;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.widget.DropShadowWidget;
import nokori.clear.vg.widget.RectangleWidget;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.windows.Window;
import nokori.clear.windows.WindowManager;

public class CDButton extends Widget {

	public static final int HEIGHT = 50;
	public static final int WIDGET_CLIP_X_PADDING = 20;
	public static final int WIDGET_CLIP_Y_PADDING = 10;

	public CDButton(float x, float y, float width) {
		super(x, y, width, HEIGHT);

		float cornerRadius = 3f;
		addChild(new DropShadowWidget(cornerRadius, ClearColor.LIGHT_BLACK.alpha(0.5f)));
		addChild(new RectangleWidget(cornerRadius, ClearColor.CORAL));
	}
	
	protected void addJDButtonWidgetClip(Widget widget) {
		widget.addChild(new WidgetClip(WidgetClip.Alignment.UPPER_LEFT, WIDGET_CLIP_X_PADDING, WIDGET_CLIP_Y_PADDING));
	}

	@Override
	public void tick(WindowManager windowManager, Window window, NanoVGContext context, WidgetAssembly rootWidgetAssembly) {
		
	}

	@Override
	public void render(WindowManager windowManager, Window window, NanoVGContext context, WidgetAssembly rootWidgetAssembly) {
		
	}

	@Override
	public void dispose() {
		
	}

}