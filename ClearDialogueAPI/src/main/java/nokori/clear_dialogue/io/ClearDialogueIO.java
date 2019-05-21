package nokori.clear_dialogue.io;

import java.io.File;

import nokori.clear_dialogue.project.Project;

/**
 * This is the interface class that all ClearDialogue I/O systems are required to implement. This class also contains a set of I/O keys that can be used for file exports so that 
 * they won't have to be made every time a user makes a new importer/exporter - while also keeping a consistency between the different file-types.
 */
public interface ClearDialogueIO {
	
	/*
	 * Project keys
	 */
	public static final String IOKEY_PROJECT_VERSION = "projectVersion";
	public static final String IOKEY_PROJECT_NAME = "projectName";
	public static final String IOKEY_PROJECT_VIEWPORT_X = "projectViewportX";
	public static final String IOKEY_PROJECT_VIEWPORT_Y = "projectViewportY";
	public static final String IOKEY_PROJECT_VIEWPORT_SCALE = "projectViewportScale";
	
	/*
	 * Basic Node data keys
	 */
	
	public static final String IOKEY_NODES_ARRAY = "nodesArray";
	public static final String IOKEY_NODE_TYPE = "type";
	
	public static final String IOKEY_UID = "uid";
	public static final String IOKEY_TITLE = "title";
	public static final String IOKEY_TAGS = "tags";
	public static final String IOKEY_NODE_X = "nodeX";
	public static final String IOKEY_NODE_Y = "nodeY";
	public static final String IOKEY_EXPANDED = "expanded";
	
	public static final String IOKEY_IN_CONNECTOR_UID = "inConnectorUID";
	public static final String IOKEY_OUT_CONNECTOR_UID = "outConnectorUID";
	
	public static final String IOKEY_TEXT = "text";
	
	/*
	 * Connection keys
	 */
	public static final String IOKEY_CONNECTIONS_ARRAY = "connectionsArray";
	public static final String IOKEY_CONNECTOR_1_UID = "connector1UID";
	public static final String IOKEY_CONNECTOR_2_UID = "connector2UID";
	
	/*
	 * Dialogue Node Type keys
	 */
	
	public static final String IOKEY_NODE_TYPE_DIALOGUE = "typeDialogue";
	
	/*
	 * Response Node Type keys
	 */
	
	public static final String IOKEY_NODE_TYPE_RESPONSE = "typeResponse";
	public static final String IOKEY_RESPONSES_ARRAY = "responsesArray";
	
	/**
	 * Export functionality.
	 */
	public void exportProject(Project project, File f) throws Exception;
	
	/**
	 * Import functionality.
	 * 
	 * If the import fails, return null. JDialogueCore will automatically handle it correctly in that case.
	 */
	public Project importProject(File f) throws Exception;
	
	/**
	 * The name of the file type of file this exporter/importer manages.
	 */
	public String getTypeName();
}
