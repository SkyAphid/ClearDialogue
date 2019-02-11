package nokori.jdialogue.throwable;

public class MissingArcError extends Error {

	private static final long serialVersionUID = 5658027467367739969L;

	public MissingArcError(String information){
        super("Missing DIalogueNodeConnectorArc! " + information);
    }
}
