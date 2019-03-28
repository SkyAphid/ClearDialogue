package nokori.clear_dialogue.ui.widget.node;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.widget.SectorCircleWidget;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.windows.Window;
import nokori.clear.windows.WindowManager;

public class ConnectorWidget extends SectorCircleWidget {
	public static final int CONNECTOR_RADIUS = 20;
	
	public enum ConnectorType {
		IN(new ClearColor(228, 80, 65, 255), false),
		OUT(new ClearColor(52, 205, 112, 255), true);
		
		private ClearColor color;
		private boolean flipped;
		
		private ConnectorType(ClearColor color, boolean flipped) {
			this.color = color;
			this.flipped = flipped;
		}
	};
	
	private DraggableDialogueWidget widget;
	private ConnectorType connectorType;
	
	public ConnectorWidget(DraggableDialogueWidget widget, ConnectorType connectorType) {
		super(connectorType.color, CONNECTOR_RADIUS, 0.5f);
		this.widget = widget;
		this.connectorType = connectorType;
		
		switch(connectorType) {
		case OUT:
			addChild(new WidgetClip(WidgetClip.Alignment.CENTER_RIGHT));
			break;
		case IN:
		default:
			addChild(new WidgetClip(WidgetClip.Alignment.CENTER_LEFT));
			break;
		}
	}

	@Override
	public void tick(WindowManager windowManager, Window window, NanoVGContext context, WidgetAssembly rootWidgetAssembly) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(WindowManager windowManager, Window window, NanoVGContext context,
			WidgetAssembly rootWidgetAssembly) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
