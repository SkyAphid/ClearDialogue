package nokori.clear_dialogue.throwable;

public class FailedToInstantiateNodeException extends Exception {

	private static final long serialVersionUID = 5658027467367739969L;

	public FailedToInstantiateNodeException(String nodeName){
        super("Failed to instantiate node \"" + nodeName + "!\"");
    }
}
