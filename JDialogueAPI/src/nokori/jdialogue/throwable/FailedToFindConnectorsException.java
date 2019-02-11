package nokori.jdialogue.throwable;

public class FailedToFindConnectorsException extends Exception {

	private static final long serialVersionUID = 5658027467367739969L;

	public FailedToFindConnectorsException(String uids){
        super("Failed to find connectors with UIDs: " + uids);
    }
}
