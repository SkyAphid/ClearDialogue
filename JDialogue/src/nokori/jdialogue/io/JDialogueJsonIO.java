package nokori.jdialogue.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import javafx.stage.FileChooser.ExtensionFilter;
import nokori.jdialogue.project.Connection;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueNodeConnector;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueResponseNode.Response;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.throwable.FailedToFindConnectorsException;
import nokori.jdialogue.throwable.FailedToInstantiateNodeException;

public class JDialogueJsonIO implements JDialogueIO{

	/*
	 * Project JSON tags
	 */
	public static final String JSON_PROJECT_VERSION = "projectVersion";
	public static final String JSON_PROJECT_NAME = "projectName";
	public static final String JSON_PROJECT_CANVAS_WIDTH = "projectCanvasWidth";
	public static final String JSON_PROJECT_CANVAS_HEIGHT = "projectCanvasHeight";
	
	public static final String JSON_PROJECT_VIEWPORT_X = "projectViewportX";
	public static final String JSON_PROJECT_VIEWPORT_Y = "projectViewportY";
	public static final String JSON_PROJECT_VIEWPORT_SCALE = "projectViewportScale";
	
	/*
	 * Basic Node data JSON tags
	 */
	public static final String JSON_NODES_ARRAY = "nodesArray";
	public static final String JSON_NODE_TYPE = "type";
	
	public static final String JSON_UID = "uid";
	public static final String JSON_NAME = "name";
	public static final String JSON_TAG = "tag";
	public static final String JSON_NODE_X = "nodeX";
	public static final String JSON_NODE_Y = "nodeY";
	
	public static final String JSON_IN_CONNECTOR_UID = "inConnectorUID";
	public static final String JSON_OUT_CONNECTOR_UID = "outConnectorUID";
	
	public static final String JSON_TEXT = "text";
	
	/*
	 * Connection JSON tags
	 */
	public static final String JSON_CONNECTIONS_ARRAY = "connectionsArray";
	public static final String JSON_CONNECTOR_1_UID = "connector1UID";
	public static final String JSON_CONNECTOR_2_UID = "connector2UID";
	
	/*
	 * Dialogue Node Type JSON tags
	 */
	
	public static final String JSON_NODE_TYPE_DIALOGUE = "typeDialogue";
	
	/*
	 * Response Node Type JSON tags
	 */
	
	public static final String JSON_NODE_TYPE_RESPONSE = "typeResponse";
	public static final String JSON_RESPONSES_ARRAY = "responsesArray";

	
	@Override
	public void exportProject(Project project, File f) throws Exception {

		/*
		 * Basic Project Information
		 */
		
		JsonObjectBuilder projectBuilder = Json.createObjectBuilder();
		
		projectBuilder.add(JSON_PROJECT_NAME, project.getName());
		projectBuilder.add(JSON_PROJECT_VERSION, Project.CURRENT_VERSION);
		
		projectBuilder.add(JSON_PROJECT_CANVAS_WIDTH, project.getCanvasWidth());
		projectBuilder.add(JSON_PROJECT_CANVAS_HEIGHT, project.getCanvasHeight());
		
		projectBuilder.add(JSON_PROJECT_VIEWPORT_X, project.getViewportX());
		projectBuilder.add(JSON_PROJECT_VIEWPORT_Y, project.getViewportY());
		projectBuilder.add(JSON_PROJECT_VIEWPORT_SCALE, project.getViewportScale());
		
		/*
		 * Save Nodes
		 */
		
		JsonArrayBuilder nodes = Json.createArrayBuilder();
		
		for (int i = 0; i < project.getNumNodes(); i++) {
			DialogueNode node = project.getNode(i);
			
			JsonObjectBuilder nodeBuilder = Json.createObjectBuilder();
			
			//Record basic data
			nodeBuilder.add(JSON_UID, node.getUID());
			nodeBuilder.add(JSON_NAME, node.getName());
			nodeBuilder.add(JSON_TAG, node.getTag());
			
			nodeBuilder.add(JSON_NODE_X, node.getX());
			nodeBuilder.add(JSON_NODE_Y, node.getY());
			
			nodeBuilder.add(JSON_IN_CONNECTOR_UID, node.getInConnector().getUID());
			
			/*
			 * Text Node Data
			 */
			
			if (node instanceof DialogueTextNode) {
				//Store node-type for easy access in the importer
				nodeBuilder.add(JSON_NODE_TYPE, JSON_NODE_TYPE_DIALOGUE);
				
				//Store text of node
				DialogueTextNode textNode = (DialogueTextNode) node;
				nodeBuilder.add(JSON_TEXT, textNode.getText());
				
				//Store the out-connector
				nodeBuilder.add(JSON_OUT_CONNECTOR_UID, textNode.getOutConnector().getUID());
			}
			
			/*
			 * Dialogue Node Data 
			 */
			
			if (node instanceof DialogueResponseNode) {
				//Store node-type
				nodeBuilder.add(JSON_NODE_TYPE, JSON_NODE_TYPE_RESPONSE);
				
				JsonArrayBuilder responsesBuilder = Json.createArrayBuilder();
				
				//Insert each response into the responsesBuilder
				ArrayList<Response> responses = ((DialogueResponseNode) node).getResponses();
				
				for (int j = 0; j < responses.size(); j++) {
					Response response = responses.get(j);
					
					JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
					
					//Response text
					responseBuilder.add(JSON_TEXT, response.getText());
					
					//Response out-connector
					responseBuilder.add(JSON_OUT_CONNECTOR_UID, response.getOutConnector().getUID());
					
					//Store in responsesBuilder
					responsesBuilder.add(responseBuilder);
				}
				
				nodeBuilder.add(JSON_RESPONSES_ARRAY, responsesBuilder);
			}
			
			nodes.add(nodeBuilder);
		}
		
		projectBuilder.add(JSON_NODES_ARRAY, nodes);
		
		/*
		 * 
		 * Connections
		 * 
		 */
		
		JsonArrayBuilder connections = Json.createArrayBuilder();
		
		for (int i = 0; i < project.getNumConnections(); i++) {
			Connection connection = project.getConnection(i);
			
			JsonObjectBuilder connectionBuilder = Json.createObjectBuilder();
			
			//Record basic data
			connectionBuilder.add(JSON_CONNECTOR_1_UID, connection.getConnector1().getUID());
			connectionBuilder.add(JSON_CONNECTOR_2_UID, connection.getConnector2().getUID());
			
			connections.add(connectionBuilder);
		}
		
		projectBuilder.add(JSON_CONNECTIONS_ARRAY, connections);
		
		/*
		 * Final Export / Write file
		 */
		
		JsonObject export = projectBuilder.build();

		StringWriter stringWriter = new StringWriter();

		HashMap<String, Object> properties = new HashMap<>(1);
		properties.put(JsonGenerator.PRETTY_PRINTING, true);

		JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
		JsonWriter jsonWriter = writerFactory.createWriter(stringWriter);
		jsonWriter.writeObject(export);
		jsonWriter.close();

		Files.write(f.toPath(), stringWriter.toString().getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE);
	}
	
	@Override
	public Project importProject(File f) throws Exception {

		FileInputStream inputStream = new FileInputStream(f);
		JsonReader jsonReader = Json.createReader(inputStream);

		/*
		 * Project Data
		 */
		JsonObject projectObject = jsonReader.readObject();

		int projectVersion = projectObject.getInt(JSON_PROJECT_VERSION);
		projectObject = compatibilityUpdates(projectVersion, projectObject);
		
		String projectName = projectObject.getString(JSON_PROJECT_NAME);
		
		int canvasWidth = projectObject.getInt(JSON_PROJECT_CANVAS_WIDTH);
		int canvasHeight = projectObject.getInt(JSON_PROJECT_CANVAS_HEIGHT);
		
		double viewportX = projectObject.getJsonNumber(JSON_PROJECT_VIEWPORT_X).doubleValue();
		double viewportY = projectObject.getJsonNumber(JSON_PROJECT_VIEWPORT_Y).doubleValue();
		double viewportScale = projectObject.getJsonNumber(JSON_PROJECT_VIEWPORT_SCALE).doubleValue();

		Project project = new Project(projectVersion, projectName, canvasWidth, canvasHeight, viewportX, viewportY, viewportScale);

		/*
		 * Node Data
		 */

		JsonArray nodeArray = projectObject.getJsonArray(JSON_NODES_ARRAY);

		for (int i = 0; i < nodeArray.size(); i++) {
			JsonObject nodeObject = nodeArray.getJsonObject(i);

			// Load basic shared data
			String uid = nodeObject.getString(JSON_UID);
			String name = nodeObject.getString(JSON_NAME);
			String tag = nodeObject.getString(JSON_TAG);

			double nodeX = nodeObject.getJsonNumber(JSON_NODE_X).doubleValue();
			double nodeY = nodeObject.getJsonNumber(JSON_NODE_Y).doubleValue();

			DialogueNode node = null;

			// Load type-specific data
			String type = nodeObject.getString(JSON_NODE_TYPE);

			// Dialogue-type
			if (type.equals(JSON_NODE_TYPE_DIALOGUE)) {
				node = new DialogueTextNode(project, uid, name, tag, nodeX, nodeY, nodeObject.getString(JSON_TEXT));

				String outConnectorUID = nodeObject.getString(JSON_OUT_CONNECTOR_UID);
				((DialogueTextNode) node).setOutConnector(new DialogueNodeConnector(project, node, outConnectorUID));
			}

			// Response-type
			if (type.equals(JSON_NODE_TYPE_RESPONSE)) {
				node = new DialogueResponseNode(project, uid, name, tag, nodeX, nodeY);
				DialogueResponseNode responseNode = (DialogueResponseNode) node;

				JsonArray responseArray = nodeObject.getJsonArray(JSON_RESPONSES_ARRAY);

				for (int j = 0; j < responseArray.size(); j++) {
					JsonObject responseObject = responseArray.getJsonObject(j);

					String text = responseObject.getString(JSON_TEXT);
					String outConnectorUID = responseObject.getString(JSON_OUT_CONNECTOR_UID);

					responseNode.addResponse(text, outConnectorUID);
				}
			}

			// Finalize
			if (node != null) {
				// Set in-connector now that node is instantiated
				String inConnectorUID = nodeObject.getString(JSON_IN_CONNECTOR_UID);
				node.setInConnector(new DialogueNodeConnector(project, node, inConnectorUID));

				// Add node to project
				project.addNode(node);
			} else {
				throw new FailedToInstantiateNodeException(name);
			}
		}

		/*
		 * Connection data
		 */

		JsonArray connectionsArray = projectObject.getJsonArray(JSON_CONNECTIONS_ARRAY);

		for (int i = 0; i < connectionsArray.size(); i++) {
			JsonObject connectionObject = connectionsArray.getJsonObject(i);

			// Get the connector UIDs of the Connection
			String connector1UID = connectionObject.getString(JSON_CONNECTOR_1_UID);
			String connector2UID = connectionObject.getString(JSON_CONNECTOR_2_UID);

			// Build a Connection from the UIDs
			DialogueNodeConnector connector1 = project.getDialogueNodeConnector(connector1UID);
			DialogueNodeConnector connector2 = project.getDialogueNodeConnector(connector2UID);

			if (connector1 != null && connector2 != null) {
				Connection connection = new Connection(connector1, connector2);

				project.addConnection(connection);

			} else {
				String missingUIDs = "";

				if (connector1 == null) {
					missingUIDs += "\n" + connector1UID;
				}

				if (connector2 == null) {
					missingUIDs += "\n" + connector2UID;
				}

				throw new FailedToFindConnectorsException(missingUIDs);
			}
		}

		/*
		 * Finish
		 */

		return project;
			
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter("JDialogue JSON Files (*.json)", "*.json");
	}

	@Override
	public String getTypeName() {
		return "json";
	}
	
	/**
	 * This function runs through the JSON object on import and updates it with any new features added since the first version of JDialogue was released.
	 */
	private static JsonObject compatibilityUpdates(int version, JsonObject projectObject) {
		JsonObject updatedProjectObject = projectObject;
		
		//Breaks purposefully left out so that it'll start at the current version and roll down all the way until the latest.
		switch(version) {
		case 1:
			updatedProjectObject = version1CompatibilityUpdate(projectObject);
		}
		
		return updatedProjectObject;
	}
	
	/**
	 * Updates the given project to Version 2 by adding the new canvas size settings and setting them to the old constant values.
	 * 
	 * @param projectObject
	 */
	private static JsonObject version1CompatibilityUpdate(JsonObject projectObject) {
		JsonObjectBuilder projectBuilder = Json.createObjectBuilder(projectObject);
		
		projectBuilder.add(JSON_PROJECT_CANVAS_WIDTH, 20_000);
		projectBuilder.add(JSON_PROJECT_CANVAS_HEIGHT, 10_000);
		
		System.out.println("Updated Project to Version 2");
		
		return projectBuilder.build();
	}
}
