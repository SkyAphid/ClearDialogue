package nokori.clear_dialogue.ui.throwable;

public class MissingConnectorError extends Error {
	private static final long serialVersionUID = 5658027467367739969L;

	public MissingConnectorError(String information) {
		super("Missing DialogueNodeConnector! " + information);
	}
}
