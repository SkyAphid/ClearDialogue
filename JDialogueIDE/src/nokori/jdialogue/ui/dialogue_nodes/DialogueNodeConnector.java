package nokori.jdialogue.ui.dialogue_nodes;

import lwjgui.Color;
import lwjgui.geometry.Pos;
import lwjgui.scene.layout.floating.FloatingPane;
import nokori.jdialogue.project.DialogueConnector;
import nokori.jdialogue.ui.components.HalfCircle;

public class DialogueNodeConnector extends FloatingPane {
	
	private static final int CONNECTOR_RADIUS = 20;
	
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
	
	public DialogueNodeConnector(DialogueConnector connector, ConnectorType type) {
		this(connector, type, CONNECTOR_RADIUS);
	}
	
	public DialogueNodeConnector(DialogueConnector connector, ConnectorType type, float radius) {
		setAlignment(Pos.CENTER);

		HalfCircle circle = new HalfCircle(type.color, radius, type.flipped, 50);
		getChildren().add(circle);
		
		circle.setOnMouseEntered(e -> {
			System.err.println("Entered connector: " + type + " " + cached_context.getMouseX() + " / " + cached_context.getMouseY());
		});
		
		circle.setOnMouseExited(e -> {
			System.err.println("Exited connector: " + type + " " + cached_context.getMouseX() + " / " + cached_context.getMouseY());
		});
	}

}
