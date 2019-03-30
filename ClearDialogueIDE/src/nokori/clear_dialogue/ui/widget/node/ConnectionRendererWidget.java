package nokori.clear_dialogue.ui.widget.node;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.util.BezierLineRenderer;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.windows.Window;
import nokori.clear.windows.WindowManager;
import nokori.clear_dialogue.project.Connection;
import nokori.clear_dialogue.project.DialogueConnector;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.ClearDialogueCanvas;
import nokori.clear_dialogue.ui.SharedResources;

public class ConnectionRendererWidget extends Widget {
	
	private SharedResources sharedResources;
	
	private BezierLineRenderer bezierLine = new BezierLineRenderer(ClearColor.LIGHT_BLACK);
	
	public ConnectionRendererWidget(SharedResources sharedResources) {
		this.sharedResources = sharedResources;
	}

	@Override
	public void tick(WindowManager windowManager, Window window, NanoVGContext context, WidgetAssembly rootWidgetAssembly) {}

	@Override
	public void render(WindowManager windowManager, Window window, NanoVGContext context, WidgetAssembly rootWidgetAssembly) {
		Project project = sharedResources.getProject();
		
		//Render all connections
		for (int i = 0; i < project.getNumConnections(); i++) {
			renderConnection(context, project.getConnection(i));
		}
		
		//Render a line connected to the mouse when the user is connecting dialogue nodes
		if (ClearStaticResources.getFocusedWidget() instanceof ConnectorWidget) {
			renderConnectionInProgress(window, context, ((ConnectorWidget) ClearStaticResources.getFocusedWidget()));
		}
	}
	
	private void renderConnectionInProgress(Window window, NanoVGContext context, ConnectorWidget connector) {
		float sx = connector.getClippedX() + connector.getWidth()/2;
		float sy = connector.getClippedY() + connector.getHeight()/2;
		
		float ex = (float) window.getMouseX();
		float ey = (float) window.getMouseY();
		
		renderLine(context, sx, sy, ex, ey);
	}
	
	private void renderConnection(NanoVGContext context, Connection connection) {
		DialogueConnector connector1 = connection.getConnector1();
		DialogueConnector connector2 = connection.getConnector2();
		
		ConnectorWidget connector1Widget = findConnectorWidget(connector1);
		ConnectorWidget connector2Widget = findConnectorWidget(connector2);
			
		float sx = connector1Widget.getClippedX() + connector1Widget.getWidth()/2; 
		float sy = connector1Widget.getClippedY() + connector1Widget.getHeight()/2;
			
		float ex = connector2Widget.getClippedX() + connector2Widget.getWidth()/2;
		float ey = connector2Widget.getClippedY() + connector2Widget.getHeight()/2;

		renderLine(context, sx, sy, ex, ey);
	}
	
	private void renderLine(NanoVGContext context, float sx, float sy, float ex, float ey) {
		float lineDX = (ex - sx);
		
		float lineSControlX = (lineDX > 0 ? 50 : -50);
		float lineSControlY = 0;
		
		float lineEControlX = (ex - sx) + (lineDX < 0 ? 50 : -50);
		float lineEControlY = (ey - sy);
		
		bezierLine.setStartAndControl1Position(sx, sy, lineSControlX, lineSControlY);
		bezierLine.setEndAndControl2Position(ex, ey, lineEControlX, lineEControlY);
		
		bezierLine.setStrokeThickness(2f);
		bezierLine.render(context);
	}
	
	/**
	 * Find the ConnectorWidget that's linked to this one for rendering purposes
	 */
	public ConnectorWidget findConnectorWidget(DialogueConnector connector) {
		ClearDialogueCanvas canvas = sharedResources.getCanvas();
		
		for (int i = 0; i < canvas.getNumChildren(); i++) {
			Widget widget = canvas.getChild(i);
			
			if (widget instanceof DraggableDialogueWidget) {
				DraggableDialogueWidget w = (DraggableDialogueWidget) widget;
				ConnectorWidget c = w.findConnectorWidget(connector);
				
				if (c != null) {
					return c;
				}
			}
		}
		
		return null;
	}


	@Override
	public void dispose() {}

}
