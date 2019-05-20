package nokori.clear_dialogue.throwable;

public class NullConnectorError extends Error {

	private static final long serialVersionUID = 5658027467367739969L;

	public NullConnectorError(String connectorParentName){
        super(connectorParentName + " tried to connect to null connector!");
    }
}
