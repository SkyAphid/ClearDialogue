package nokori.clear_dialogue.io;

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

import nokori.clear_dialogue.project.Connection;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.DialogueConnector;
import nokori.clear_dialogue.project.DialogueResponse;
import nokori.clear_dialogue.project.DialogueText;
import nokori.clear_dialogue.project.Project;
import nokori.clear_dialogue.project.DialogueResponse.Response;
import nokori.clear_dialogue.throwable.FailedToFindConnectorsException;
import nokori.clear_dialogue.throwable.FailedToInstantiateNodeException;

public class ClearDialogueJsonIO implements ClearDialogueIO {



	
	@Override
	public void exportProject(Project project, File f) throws Exception {

		/*
		 * Basic Project Information
		 */
		
		JsonObjectBuilder projectBuilder = Json.createObjectBuilder();
		
		projectBuilder.add(IOKEY_PROJECT_NAME, project.getName());
		projectBuilder.add(IOKEY_PROJECT_VERSION, Project.CURRENT_VERSION);
		
		projectBuilder.add(IOKEY_PROJECT_VIEWPORT_X, project.getViewportX());
		projectBuilder.add(IOKEY_PROJECT_VIEWPORT_Y, project.getViewportY());
		
		/*
		 * Save Nodes
		 */
		
		JsonArrayBuilder nodes = Json.createArrayBuilder();
		
		for (int i = 0; i < project.getNumDialogue(); i++) {
			Dialogue node = project.getDialogue(i);
			
			JsonObjectBuilder nodeBuilder = Json.createObjectBuilder();
			
			//Record basic data
			nodeBuilder.add(IOKEY_UID, node.getUID());
			nodeBuilder.add(IOKEY_TITLE, node.getTitle());
			nodeBuilder.add(IOKEY_TAGS, node.getTags());
			
			nodeBuilder.add(IOKEY_NODE_X, node.getX());
			nodeBuilder.add(IOKEY_NODE_Y, node.getY());
			
			nodeBuilder.add(IOKEY_EXPANDED, node.isExpanded());
			
			nodeBuilder.add(IOKEY_IN_CONNECTOR_UID, node.getInConnector().getUID());
			
			/*
			 * Text Node Data
			 */
			
			if (node instanceof DialogueText) {
				//Store node-type for easy access in the importer
				nodeBuilder.add(IOKEY_NODE_TYPE, IOKEY_NODE_TYPE_DIALOGUE);
				
				//Store text of node
				DialogueText textNode = (DialogueText) node;
				nodeBuilder.add(IOKEY_TEXT, textNode.getText());
				
				//Store the out-connector
				nodeBuilder.add(IOKEY_OUT_CONNECTOR_UID, textNode.getOutConnector().getUID());
			}
			
			/*
			 * Dialogue Node Data 
			 */
			
			if (node instanceof DialogueResponse) {
				//Store node-type
				nodeBuilder.add(IOKEY_NODE_TYPE, IOKEY_NODE_TYPE_RESPONSE);
				
				JsonArrayBuilder responsesBuilder = Json.createArrayBuilder();
				
				//Insert each response into the responsesBuilder
				ArrayList<Response> responses = ((DialogueResponse) node).getResponses();
				
				for (int j = 0; j < responses.size(); j++) {
					Response response = responses.get(j);
					
					JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
					
					//Response text
					responseBuilder.add(IOKEY_TEXT, response.getText());
					
					//Response out-connector
					responseBuilder.add(IOKEY_OUT_CONNECTOR_UID, response.getOutConnector().getUID());
					
					//Store in responsesBuilder
					responsesBuilder.add(responseBuilder);
				}
				
				nodeBuilder.add(IOKEY_RESPONSES_ARRAY, responsesBuilder);
			}
			
			nodes.add(nodeBuilder);
		}
		
		projectBuilder.add(IOKEY_NODES_ARRAY, nodes);
		
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
			connectionBuilder.add(IOKEY_CONNECTOR_1_UID, connection.getConnector1().getUID());
			connectionBuilder.add(IOKEY_CONNECTOR_2_UID, connection.getConnector2().getUID());
			
			connections.add(connectionBuilder);
		}
		
		projectBuilder.add(IOKEY_CONNECTIONS_ARRAY, connections);
		
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

		Files.write(new File(f.getAbsolutePath() + ".json").toPath(), stringWriter.toString().getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE);
	}
	
	@Override
	public Project importProject(File f) throws Exception {

		FileInputStream inputStream = new FileInputStream(f);
		JsonReader jsonReader = Json.createReader(inputStream);

		/*
		 * Project Data
		 */
		JsonObject projectObject = jsonReader.readObject();

		int projectVersion = projectObject.getInt(IOKEY_PROJECT_VERSION);
		
		String projectName = projectObject.getString(IOKEY_PROJECT_NAME);

		float viewportX = (float) projectObject.getJsonNumber(IOKEY_PROJECT_VIEWPORT_X).doubleValue();
		float viewportY = (float) projectObject.getJsonNumber(IOKEY_PROJECT_VIEWPORT_Y).doubleValue();

		Project project = new Project(projectVersion, projectName, viewportX, viewportY);

		/*
		 * Node Data
		 */

		JsonArray nodeArray = projectObject.getJsonArray(IOKEY_NODES_ARRAY);

		for (int i = 0; i < nodeArray.size(); i++) {
			JsonObject nodeObject = nodeArray.getJsonObject(i);

			// Load basic shared data
			String uid = nodeObject.getString(IOKEY_UID);
			String name = nodeObject.getString(IOKEY_TITLE);
			String tag = nodeObject.getString(IOKEY_TAGS);

			float nodeX = (float) nodeObject.getJsonNumber(IOKEY_NODE_X).doubleValue();
			float nodeY = (float) nodeObject.getJsonNumber(IOKEY_NODE_Y).doubleValue();
			
			boolean expanded = nodeObject.getBoolean(IOKEY_EXPANDED);

			Dialogue node = null;

			// Load type-specific data
			String type = nodeObject.getString(IOKEY_NODE_TYPE);

			// Dialogue-type
			if (type.equals(IOKEY_NODE_TYPE_DIALOGUE)) {
				node = new DialogueText(project, uid, name, tag, nodeX, nodeY, expanded, nodeObject.getString(IOKEY_TEXT));

				String outConnectorUID = nodeObject.getString(IOKEY_OUT_CONNECTOR_UID);
				((DialogueText) node).setOutConnector(new DialogueConnector(project, node, outConnectorUID));
			}

			// Response-type
			if (type.equals(IOKEY_NODE_TYPE_RESPONSE)) {
				node = new DialogueResponse(project, uid, name, tag, nodeX, nodeY, expanded);
				DialogueResponse responseNode = (DialogueResponse) node;

				JsonArray responseArray = nodeObject.getJsonArray(IOKEY_RESPONSES_ARRAY);

				for (int j = 0; j < responseArray.size(); j++) {
					JsonObject responseObject = responseArray.getJsonObject(j);

					String text = responseObject.getString(IOKEY_TEXT);
					String outConnectorUID = responseObject.getString(IOKEY_OUT_CONNECTOR_UID);

					responseNode.addResponse(text, outConnectorUID);
				}
			}

			// Finalize
			if (node != null) {
				// Set in-connector now that node is instantiated
				String inConnectorUID = nodeObject.getString(IOKEY_IN_CONNECTOR_UID);
				node.setInConnector(new DialogueConnector(project, node, inConnectorUID));

				// Add node to project
				project.addDialogue(node);
			} else {
				throw new FailedToInstantiateNodeException(name);
			}
		}

		/*
		 * Connection data
		 */

		JsonArray connectionsArray = projectObject.getJsonArray(IOKEY_CONNECTIONS_ARRAY);

		for (int i = 0; i < connectionsArray.size(); i++) {
			JsonObject connectionObject = connectionsArray.getJsonObject(i);

			// Get the connector UIDs of the Connection
			String connector1UID = connectionObject.getString(IOKEY_CONNECTOR_1_UID);
			String connector2UID = connectionObject.getString(IOKEY_CONNECTOR_2_UID);

			// Build a Connection from the UIDs
			DialogueConnector connector1 = project.getDialogueConnector(connector1UID);
			DialogueConnector connector2 = project.getDialogueConnector(connector2UID);

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
	public String getTypeName() {
		return "JSON";
	}
}
