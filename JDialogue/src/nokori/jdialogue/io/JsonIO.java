package nokori.jdialogue.io;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser.ExtensionFilter;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueNodeConnector;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueResponseNode.Response;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.project.Project;

public class JsonIO implements JDialogueIO{

	/*
	 * Project JSON tags
	 */
	public static final String JSON_PROJECT_NAME = "projectName";
	public static final String JSON_PROJECT_VIEWPORT_X = "projectViewportX";
	public static final String JSON_PROJECT_VIEWPORT_Y = "projectViewportY";
	public static final String JSON_PROJECT_VIEWPORT_SCALE = "projectViewportScale";
	
	/*
	 * Basic Node data JSON tags
	 */
	public static final String JSON_NODES = "nodes";
	public static final String JSON_NODE_TYPE = "type";
	
	public static final String JSON_UID = "uid";
	public static final String JSON_NAME = "name";
	public static final String JSON_TAG = "tag";
	public static final String JSON_NODE_X = "nodeX";
	public static final String JSON_NODE_Y = "nodeY";
	
	public static final String JSON_TEXT = "text";
	
	/*
	 * Connection JSON tags
	 */
	public static final String JSON_CONNECTION = "connection";
	public static final String JSON_CONNECTOR_1 = "connector1";
	public static final String JSON_CONNECTOR_2 = "connector2";
	
	/*
	 * Dialogue Node Type JSON tags
	 */
	
	public static final String JSON_NODE_TYPE_DIALOGUE = "typeDialogue";
	
	/*
	 * Response Node Type JSON tags
	 */
	
	public static final String JSON_NODE_TYPE_RESPONSE = "typeResponse";
	public static final String JSON_RESPONSES = "responses";

	
	@Override
	public void exportProject(Project project, File f) {
		
		//TODO: Add Connection support
		
		/*
		 * Basic Project Information
		 */
		
		JsonObjectBuilder projectBuilder = Json.createObjectBuilder();
		
		projectBuilder.add(JSON_PROJECT_NAME, project.getName());
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
			
			/*
			 * Text Node Data
			 */
			
			if (node instanceof DialogueTextNode) {
				//Store node-type for easy access in the importer
				nodeBuilder.add(JSON_NODE_TYPE, JSON_NODE_TYPE_DIALOGUE);
				
				//Store text of node
				DialogueTextNode textNode = (DialogueTextNode) node;
				nodeBuilder.add(JSON_TEXT, textNode.getText());
				
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
					
					//Store in responsesBuilder
					responsesBuilder.add(responseBuilder);
				}
				
				nodeBuilder.add("responses", responsesBuilder);
			}
			
			nodes.add(nodeBuilder);
		}
		
		projectBuilder.add(JSON_NODES, nodes);
		
		JsonObject export = projectBuilder.build();
		
		try {
			StringWriter stringWriter = new StringWriter();
			
			HashMap<String, Object> properties = new HashMap<>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);
			
			JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
			JsonWriter jsonWriter = writerFactory.createWriter(stringWriter);
			jsonWriter.writeObject(export);
			jsonWriter.close();
			
			Files.write(f.toPath(), stringWriter.toString().getBytes(), StandardOpenOption.CREATE);
		} catch (Exception e) {
			e.printStackTrace();
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Caught Exception");
			alert.setHeaderText("Failed to export project.");
			alert.showAndWait();
		}
	}
	
	@Override
	public Project importProject(File f) {
		//GsonBuilder builder = new GsonBuilder();
		//Gson gson = builder.create();
		
		
		try {
			//Project project = gson.fromJson(new String(Files.readAllBytes(f.toPath())), Project.class);
			return null;
		}catch(Exception e) {
			e.printStackTrace();
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Caught Exception");
			alert.setHeaderText("Failed to import project.");
			alert.showAndWait();
			
			return null;
		}
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter("JDialogue JSON Files (*.json)", "*.json");
	}

	@Override
	public String getTypeName() {
		return "json";
	}
}
