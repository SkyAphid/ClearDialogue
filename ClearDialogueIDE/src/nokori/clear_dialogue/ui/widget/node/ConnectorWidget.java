package nokori.clear_dialogue.ui.widget.node;

import java.util.Stack;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.ClearStaticResources;
import nokori.clear.vg.transition.FillTransition;
import nokori.clear.vg.widget.HalfCircleWidget;
import nokori.clear.vg.widget.assembly.Widget;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.windows.Window;
import nokori.clear_dialogue.project.DialogueConnector;

public class ConnectorWidget extends HalfCircleWidget {
	public static final int CONNECTOR_RADIUS = 20;
	
	public enum ConnectorType {
		IN(new ClearColor(228, 80, 65, 255).immutable(true)),
		OUT(new ClearColor(52, 205, 112, 255).immutable(true));
		
		private ClearColor color;
		
		private ConnectorType(ClearColor color) {
			this.color = color;
		}
		
		public Orientation getOrientation() {
			switch(this) {
			case OUT:
				return Orientation.RIGHT;
			case IN:
			default:
				return Orientation.LEFT;
			}
		}
	};
	
	private DialogueConnector connector;
	private ConnectorType connectorType;

	private boolean selected = false;
	
	public ConnectorWidget(DialogueConnector connector, ConnectorType connectorType) {
		this(connector, connectorType, CONNECTOR_RADIUS, true);
	}
	
	public ConnectorWidget(DialogueConnector connector, ConnectorType connectorType, float radius, boolean addWidgetClip) {
		super(radius, connectorType.color.copy(), connectorType.getOrientation());
		this.connector = connector;
		this.connectorType = connectorType;
		
		if (addWidgetClip) {
			switch(connectorType) {
			case OUT:
				addChild(new WidgetClip(WidgetClip.Alignment.CENTER_RIGHT, radius, 0f));
				break;
			case IN:
			default:
				addChild(new WidgetClip(WidgetClip.Alignment.CENTER_LEFT, -radius, 0f));
				break;
			}
		}
		
		setOnMouseEnteredEvent(e -> {
			new FillTransition(200, getFill(), connectorType.color.multiply(1.2f)).play();
		});
		
		setOnMouseExitedEvent(e -> {
			new FillTransition(200, getFill(), connectorType.color).play();
		});
		
		setOnMouseButtonEvent(e -> {
			if (!e.isPressed()) {
				select(e.getWindow());
			}
		});
	}
	
	private void select(Window window) {
		/*
		 * Selecting functionality
		 */
		
		if (selected) {
			
			//Check if the mouse is hovering over a connector; if so, then ignore the de-select and let the other connector handle it.
			Stack<Widget> intersecting = parent.getParent().getChildrenWithinMouse(window);
			
			while(!intersecting.isEmpty()) {
				Widget w = intersecting.pop();

				if (w != this && w instanceof ConnectorWidget) {
					return;
				}
			}
			
			//The mouse wasn't hovering over another connector, so we can go ahead and just cancel the selection
			endSelecting();
			
		} else if (isMouseWithinThisWidget(window)) {
			
			Widget w = ClearStaticResources.getFocusedWidget();
			
			//Enable connector selection mode if there isn't a focused widget
			if (w == null) {
				connector.disconnectAll();
				selected = true;
				ClearStaticResources.setFocusedWidget(this);
			}
			
			//Connect to the focused ConenctorWidget if applicable
			if (w instanceof ConnectorWidget) {
				connect((ConnectorWidget) w);
			}
		}
	}
	
	/**
	 * Ends selecting mode for this Connector and unfocuses it
	 */
	private void endSelecting() {
		selected = false;
		ClearStaticResources.setFocusedWidget(null);
	}
	
	/**
	 * Connects this Connector to whichever one is currently selected
	 */
	private void connect(ConnectorWidget connectWith) {
		//Out Connectors can only have one connection at a time. In connectors can have as many connections as needed.
		if (connectWith.connectorType == ConnectorType.OUT) {
			connectWith.connector.disconnectAll();
		}
		
		//Connect the two connectors
		connector.connect(connectWith.connector);
		
		//End selecting
		connectWith.endSelecting();
	}
	

	public DialogueConnector getConnector() {
		return connector;
	}
}
