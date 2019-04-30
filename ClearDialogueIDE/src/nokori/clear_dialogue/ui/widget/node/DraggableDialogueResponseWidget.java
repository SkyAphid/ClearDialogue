package nokori.clear_dialogue.ui.widget.node;

import java.util.ArrayList;

import nokori.clear.vg.ClearColor;
import nokori.clear.vg.font.FontStyle;
import nokori.clear.vg.widget.assembly.WidgetClip;
import nokori.clear.vg.widget.text.TextAreaWidget;
import nokori.clear_dialogue.project.DialogueResponse;
import nokori.clear_dialogue.project.DialogueResponse.Response;
import nokori.clear_dialogue.ui.SharedResources;
import nokori.clear_dialogue.ui.widget.node.ConnectorWidget.ConnectorType;

public class DraggableDialogueResponseWidget extends DraggableDialogueWidget {

	private ArrayList<ConnectorWidget> outConnectors = new ArrayList<ConnectorWidget>();
	
	public DraggableDialogueResponseWidget(SharedResources sharedResources, DialogueResponse dialogue) {
		super(sharedResources, dialogue);
		
		//Configure the content settings. Line numbers are toggled on and off depending on the mode.
		content.setWordWrappingEnabled(false);
		content.setLineNumberRightPadding(10);
		content.setLineNumberLeftPadding(10);
		content.setLineNumberBackgroundFill(ClearColor.WHITE);
		
		refreshConnectors();
	}
	
	private void refreshConnectors() {
		ArrayList<Response> responses = ((DialogueResponse) dialogue).getResponses();
		
		float responseHeight = getResponseHeight();
		
		if (outConnectors.size() != responses.size()) {
			for (int i = 0; i < outConnectors.size(); i++) {
				removeChild(outConnectors.get(i));
			}
			
			for (int i = 0; i < responses.size(); i++) {
				float connectorRadius = responseHeight/2;
				float connectorOffsetY = content.getY() + (responseHeight * i);
				
				ConnectorWidget outConnector = new ConnectorWidget(sharedResources.getScaler(), responses.get(i).getOutConnector(), ConnectorType.OUT, connectorRadius, false);
				outConnector.addChild(new WidgetClip(WidgetClip.Alignment.TOP_RIGHT, 10f, connectorOffsetY));
				
				addChildInFrontOf(inConnector, outConnector);
				outConnectors.add(outConnector);
			}
		}
	}
	
	private float getResponseHeight() {
		return content.getFont().getHeight(sharedResources.getNanoVGContext(), content.getFontSize(), TextAreaWidget.TEXT_AREA_ALIGNMENT, FontStyle.REGULAR);
	}
	
	@Override
	protected float getMinHeight() {
		if (mode != Mode.DELETION && content != null && dialogue != null) {
			float responseHeight = getResponseHeight();
			return (content.getY() + responseHeight + (responseHeight * ((DialogueResponse) dialogue).getResponses().size() + 1));
		} else {
			return super.getMinHeight();
		}
	}
	
	@Override
	protected void keyEventCallback() {
		super.keyEventCallback();
		refreshConnectors();
	}
	
	@Override
	public void requestRemoval(boolean flagForDeletion) {
		super.requestRemoval(flagForDeletion);	
		
		for (int i = 0; i < outConnectors.size(); i++) {
			fadeOutConnector(outConnectors.get(i));
		}
	}
	
	@Override
	public void transitionMode(Mode mode) {
		super.transitionMode(mode);
		content.setLineNumbersEnabled(mode != Mode.COLLAPSED);
	}
}
