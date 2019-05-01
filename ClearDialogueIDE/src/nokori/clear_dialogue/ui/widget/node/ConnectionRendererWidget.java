package nokori.clear_dialogue.ui.widget.node;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.NanoVGContext;
import nokori.clear.vg.util.BezierLineRenderer;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear.vg.widget.assembly.WidgetAssembly;
import nokori.clear.windows.Window;
import nokori.clear_dialogue.project.Connection;
import nokori.clear_dialogue.project.DialogueConnector;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.ui.ClearDialogueCanvas;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.ConnectorWidget.ConnectorType;

public class ConnectionRendererWidget extends Widget {
	
	private SharedResources sharedResources;
	
	private BezierLineRenderer bezierLine = new BezierLineRenderer(ClearColor.LIGHT_BLACK);
	
	public ConnectionRendererWidget(SharedResources sharedResources) {
		this.sharedResources = sharedResources;
	}

	@Override
	public void tick(NanoVGContext context, WidgetAssembly rootWidgetAssembly) {}

	@Override
	public void render(NanoVGContext context, WidgetAssembly rootWidgetAssembly) {
		Project project = sharedResources.getProject();

		//Render all connections
		for (int i = 0; i < project.getNumConnections(); i++) {
			renderConnection(context, project.getConnection(i));
		}
		
		//Render a line connected to the mouse when the user is connecting dialogue nodes
		if (ClearStaticResources.getFocusedWidget() instanceof ConnectorWidget) {
			renderConnectionInProgress(context, ((ConnectorWidget) ClearStaticResources.getFocusedWidget()));
		}
	}
	
	private void renderConnectionInProgress(NanoVGContext context, ConnectorWidget connector) {
		float sx = connector.getClippedX() + connector.getWidth()/2;
		float sy = connector.getClippedY() + connector.getHeight()/2;
		
		float scale = sharedResources.getScaler().getScale();
		
		Window window = sharedResources.getWindow();
		
		float ex = (float) window.getScaledMouseX(scale);
		float ey = (float) window.getScaledMouseY(scale);
		
		renderLine(context, connector, sx, sy, null, ex, ey);
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

		renderLine(context, connector1Widget, sx, sy, connector2Widget, ex, ey);
	}
	
	private void renderLine(NanoVGContext context, ConnectorWidget startWidget, float sx, float sy, ConnectorWidget endWidget, float ex, float ey) {
		float lineDX = (ex - sx);
		
		float lineSControlX = (lineDX > 0 ? 50 : -50);
		float lineSControlY = 0;
		
		float lineEControlX = (ex - sx) + (lineDX < 0 ? 50 : -50);
		float lineEControlY = (ey - sy);
		
		boolean looping = (startWidget != null && endWidget != null) && 
				(  lineDX > 0 && startWidget.getConnectorType() == ConnectorType.IN && endWidget.getConnectorType() == ConnectorType.OUT 
				|| lineDX < 0 && startWidget.getConnectorType() == ConnectorType.OUT && endWidget.getConnectorType() == ConnectorType.IN);
		
		//Curve the connector so that it doesn't end up behind nodes in cases where nodes loop back to the start
		if (looping) {
			//System.out.println("Start: " + startWidget.getConnectorType() + " End: " + endWidget.getConnectorType() + " " + lineDX);
			float offset = 200;
			
			lineSControlX = (lineDX < 0 ? lineSControlX + offset : lineSControlX - offset);
			lineEControlX = (lineDX < 0 ? lineEControlX - offset : lineEControlX + offset);
			
			float mult = 1.5f;
			
			lineSControlY += (startWidget.getParent().getHeight() * mult);
			lineEControlY += (endWidget.getParent().getHeight() * mult);
		}

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
