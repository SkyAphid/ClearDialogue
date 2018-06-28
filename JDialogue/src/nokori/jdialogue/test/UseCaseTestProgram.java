package nokori.jdialogue.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import nokori.jdialogue.io.JDialogueSerializerIO;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueNodeConnector;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueResponseNode.Response;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.project.Project;

/**
 * This is a basic command line program that'll show a use-case of JDialogue and how it could theoretically
 * be used in a Java game context.
 */
public class UseCaseTestProgram {
	public static void main(String[] args) {
		
		//This is a complex example of how conversations can be made with JDialogue
		File testDialogue = new File("Hello World.dialogue");
		
		//This is a basic example of how conversations can be made to loop on themselves, like classic jRPGs
		//File testDialogue = new File("ButThouMust.dialogue");
		
		/*
		 * Start program
		 */
		
		System.out.println("Starting Use-Case Test Program. Answer by inputting numbers (I.E. to select Response 1, input \"1\").");
		
		Scanner scanner = new Scanner(System.in);
		
		try{
			//Load the project
			Project project = new JDialogueSerializerIO().importProject(testDialogue);
			
			//Find the starting node (where the dialogue starts)
			//This tag is arbitrary, but you can use this system to start from any node in the project.
			DialogueNode start = project.findNodeWithTag("start", true);
			
			//If start found, begin dialogue.
			if (start != null) {
				new UseCaseTestProgram().runNode(scanner, start);
			}else {
				System.err.println("Couldn't find starting node. Is the correct dialogue file being loaded?");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("End of dialogue.");
	}
	
	/**
	 * The intention is that you'll read the nodes until you run out of connections.
	 */
	public void runNode(Scanner scanner, DialogueNode node) {
		
		if (node == null) return;
		
		//If the node is a DialogueTextNode, display its text and display the next node it's connected to
		if (node instanceof DialogueTextNode) {
			DialogueTextNode textNode = (DialogueTextNode) node;
			
			System.out.println(textNode.getText());
			
			runNode(scanner, getNextNode(textNode.getOutConnector()));
		}
		
		//If a response node, print its responses and let the player input a selection
		if (node instanceof DialogueResponseNode) {
			DialogueResponseNode responseNode = (DialogueResponseNode) node;
			ArrayList<Response> responses = responseNode.getResponses();
			
			//Print all the responses and wait on user input
			for (int i = 0; i < responses.size(); i++) {
				System.out.println(i + ": " + responseNode.getResponses().get(i).getText());
			}
			
			String response = scanner.nextLine();
			
			try {
				int responseIndex = Integer.parseInt(response);
				
				//If the responseIndex is valid, use it, otherwise, run the node again
				if (responseIndex >= 0 && responseIndex < responses.size()) {
					
					runNode(scanner, getNextNode(responses.get(responseIndex).getOutConnector()));
					
				} else {
					
					System.out.println("Invalid response selection.");
					runNode(scanner, node);
					
				}
				
			}catch (NumberFormatException e) {
				System.out.println("Invalid response.");
				
				//Run the same node again if the input was invalid.
				runNode(scanner, node);
			}
		}
	}
	
	/**
	 * Gets the DialogueNode of the connector that the passed in connector is connected to. Tongue twister. Lol
	 * 
	 * @param connector
	 * @return
	 */
	public DialogueNode getNextNode(DialogueNodeConnector connector) {
		if (connector != null && connector.getConnectedTo() != null) {
			return connector.getConnectedTo().getParent();
		} else {
			return null;
		}
	}
}
