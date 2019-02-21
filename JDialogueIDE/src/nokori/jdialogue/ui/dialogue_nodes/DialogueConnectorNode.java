package nokori.jdialogue.ui.dialogue_nodes;

import lwjgui.Color;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.transition.FillTransition;
import nokori.jdialogue.project.DialogueConnector;
import nokori.jdialogue.ui.components.HalfCircle;

public class DialogueConnectorNode extends FloatingPane {
	
	protected static final int HIGHLIGHT_SPEED_IN_MILLIS = 200;
	
	public static final int CONNECTOR_RADIUS = 20;
	
	public enum ConnectorType {
		IN(new Color(228, 80, 65), false),
		OUT(new Color(52, 205, 112), true);
		
		private Color color;
		private boolean flipped;
		
		private ConnectorType(Color color, boolean flipped) {
			this.color = color;
			this.flipped = flipped;
		}
	};
	
	private DialogueNode parent;
	private ConnectorType type;
	private DialogueConnector connector;
	
	protected HalfCircle circle;
	
	public DialogueConnectorNode(DialogueNode parent, DialogueConnector connector, ConnectorType type) {
		this(parent, connector, type, CONNECTOR_RADIUS);
	}
	
	public DialogueConnectorNode(DialogueNode parent, DialogueConnector connector, ConnectorType type, float radius) {
		this.parent = parent;
		this.connector = connector;
		this.type = type;
		
		setAlignment(Pos.CENTER);

		circle = new HalfCircle(type.color.copy(), radius, type.flipped, 50);
		getChildren().add(circle);
		
		circle.setOnMouseEntered(e -> {
			new FillTransition(HIGHLIGHT_SPEED_IN_MILLIS, circle.getFill(), type.color.brighter());
			System.out.println(type + " entered");
		});
		
		circle.setOnMouseExited(e -> {
			new FillTransition(HIGHLIGHT_SPEED_IN_MILLIS, circle.getFill(), type.color);
			System.out.println(type + " exited");
		});
	}
	
	/*
	 * 
	 * 
	 * Auto-positioning
	 * 
	 * 
	 */
	
	@Override
	public void position(Node parent) {
		syncPosition();
		super.position(parent);
	}
	
	/**
	 * Syncs this DialogueConnectorNode to the parent DialogueNode in position(). Override to add custom positioning behavior.
	 */
	protected void syncPosition() {
		switch(type) {
		case IN:
			setAbsolutePosition(parent.getInConnectorX(circle.getWidth()), parent.getCenteredConnectorY(circle.getHeight()));
			break;
		case OUT:
			setAbsolutePosition(parent.getOutConnectorX(), parent.getCenteredConnectorY(circle.getHeight()));
			break;
		}
	};
	
	/*
	 * 
	 * 
	 * Getters/Setters
	 * 
	 * 
	 */

	public DialogueNode getParent() {
		return parent;
	}

	public DialogueConnector getConnector() {
		return connector;
	}

	public ConnectorType getType() {
		return type;
	}
}
