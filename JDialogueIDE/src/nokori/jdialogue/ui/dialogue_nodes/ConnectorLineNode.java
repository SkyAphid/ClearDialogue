package nokori.jdialogue.ui.dialogue_nodes;

import lwjgui.scene.Context;
import lwjgui.scene.shape.BezierLine;
import lwjgui.theme.Theme;
import nokori.jdialogue.ui.dialogue_nodes.DialogueConnectorNode.ConnectorType;

public class ConnectorLineNode extends BezierLine {
	
	private DialogueConnectorNode outConnector, inConnector;
	
	public ConnectorLineNode(DialogueConnectorNode outConnector, DialogueConnectorNode inConnector) {
		this.outConnector = outConnector;
		this.inConnector = inConnector;
		updateLinePositions();
		
		setFill(Theme.current().getText());
		setMouseTransparent(true);
	}
	
	private void updateLinePositions() {
		double outX = outConnector.getX();
		double outY = outConnector.getY();
		
		double inX = inConnector.getX();
		double inY = inConnector.getY();
		
		double ndx = (inX - outX);
		
		double control1X = (ndx > 0 ? outX + 50 : outX - 50);
		double control1Y = outY;
		
		double control2X = (ndx < 0 ? inX + 50 : inX - 50);
		double control2Y = inY;
		
		if (outConnector != null && inConnector != null) {
			boolean looping = (ndx > 0 && outConnector.getType() == ConnectorType.IN && inConnector.getType() == ConnectorType.OUT 
					|| ndx < 0 && outConnector.getType() == ConnectorType.OUT && inConnector.getType() == ConnectorType.IN);
			
			//Curve the connector so that it doesn't end up behind nodes in cases where nodes loop back to the start
			if (looping) {
				control1X = (ndx > 0 ? outX - 100 : outX + 100);
				control2X = (ndx < 0 ? inX - 100 : inX + 100);
				control1Y = outY + outConnector.getParent().getHeight() * 1.5;
				control2Y = inY + inConnector.getParent().getHeight() * 1.5;
			}
		}
		
		setStartPosition(outX, outY);
		setControl1Position(control1X, control1Y);
		
		setEndPosition(inX, inY);
		setControl2Position(control2X, control2Y);
	}
	
	@Override
	public void render(Context context) {
		updateLinePositions();
		super.render(context);
	}
}
