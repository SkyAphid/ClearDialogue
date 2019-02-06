package nokori.jdialogue.ui.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import nokori.jdialogue.io.JDialogueIO;
import nokori.jdialogue.io.JDialogueJsonIO;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.project.DialogueResponseNode;
import nokori.jdialogue.project.DialogueResponseNode.Response;
import nokori.jdialogue.project.DialogueTextNode;
import nokori.jdialogue.project.Project;

/**
 * 
 * A useful tool for mass find/replace of text in dialogue files.
 * 
 * It supports all supported filetypes of JDialogue, including .dialogue and .json.
 *
 */
public class ReplaceTool {
	public enum ReplaceMode {
		MULTI("Each Project will be imported, and the contained DialogueNodes will be modified."
				+ "\nAll exact instances of the \"Find\" input will be replaced with the \"Replace with\" input."
				+ "\nThe modified Project files will be backed up before saving the new versions."
				+ "\n\nExample of backup: YourProject.dialogue.backup"), 
		LOCAL("Each DialogueNode within this single Project will be modified."
				+"\nAll exact instances of the \"Find\" input will be replaced with the \"Replace with\" input.");
		
		private String desc;
		
		private ReplaceMode(String desc) {
			this.desc = desc;
		}
		
		public String getDesc() {
			return desc;
		}
	};
	
	public static void run(Stage stage, File projectDir, Project project, ReplaceMode mode) {
		switch(mode) {
		case MULTI:
			multiReplace(stage, projectDir);
			break;
		case LOCAL:
		default:
			projectReplace(stage, projectDir, project);
			break;
		}
	}
	
	private static void projectReplace(Stage stage, File projectDir, Project project) {
		Pair<String, String> refactorInfo = openReplaceDialog(stage, ReplaceMode.LOCAL);
		
		if (refactorInfo != null) {
			replace(project, refactorInfo.getKey(), refactorInfo.getValue());
			JDialogueUtils.showAlert(stage, AlertType.INFORMATION, "Replace Information", "Replace successful.", "");
		}
	}
	
	private static void multiReplace(Stage stage, File projectDir) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Project Files");
		fileChooser.setInitialDirectory(projectDir);
		
		List<File> files = fileChooser.showOpenMultipleDialog(stage);

		if (files != null && !files.isEmpty()) {
			Pair<String, String> refactorInfo = openReplaceDialog(stage, ReplaceMode.MULTI);
			
			if (refactorInfo == null) {
				return;
			}
			
			JDialogueIO[] ioTypes = {
					new JDialogueJsonIO()
			};
			
			//Searches files, for each file, check if it's compatible with the ioType, if so, refactor, then continue
			fileLoop:
			for (File f : files) {
				for (int i = 0; i < ioTypes.length; i++) {
					
					List<String> fileExtensions = ioTypes[i].getExtensionFilter().getExtensions();
					
					for (int j = 0; j < fileExtensions.size(); j++) {
						//Removes the * at the beginning
						String extension = fileExtensions.get(j).substring(1, fileExtensions.get(j).length());
						
						if (f.getName().endsWith(extension)) {
							replace(f, ioTypes[i], refactorInfo.getKey(), refactorInfo.getValue());
							continue fileLoop;
						}
					}
				}
			}
			
			JDialogueUtils.showAlert(stage, AlertType.INFORMATION, "Replace Information", "Multi-Replace successful.", "");
		}
	}
	
	/**
	 * Refactors the project file with the following parameters
	 */
	private static void replace(File f, JDialogueIO io, String find, String replace) {
		try {
			Project project = io.importProject(f);
			
			replace(project, find, replace);
			
			//It should exist but I'm just being thorough
			if(f.exists()) {
				File output = new File(f.getParentFile(), f.getName() + ".backup");
				Files.copy(f.toPath(), output.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			
			io.exportProject(project, f);
			
		} catch(Exception e) {
			e.printStackTrace();
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Caught " + e.getClass().getSimpleName());
			alert.setHeaderText("Failed to import projects.");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
		}
	}
	
	private static void replace(Project project, String find, String replace) {
		for (int i = 0; i < project.getNumNodes(); i++) {
			DialogueNode node = project.getNode(i);
			
			node.setName(node.getName().replace(find, replace));
			node.setTag(node.getTag().replace(find, replace));
			
			if (node instanceof DialogueTextNode) {
				DialogueTextNode textNode = (DialogueTextNode) node;
				textNode.setText(textNode.getText().replace(find, replace));
			}
			
			if (node instanceof DialogueResponseNode) {
				DialogueResponseNode responseNode = (DialogueResponseNode) node;
				
				for (int j = 0; j < responseNode.getResponses().size(); j++) {
					Response response = responseNode.getResponses().get(j);
					
					response.setText(response.getText().replace(find, replace));
				}
			}
		}
	}
	
	/**
	 * Opens a dialog that asks what the user wants to be found and replaced in the various project files
	 * 
	 * Pulled from: http://code.makery.ch/blog/javafx-dialogs-official/
	 */
	private static Pair<String, String> openReplaceDialog(Stage stage, ReplaceMode mode) {
		/*
		 * Create the dialog
		 */
		
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Replace");
		dialog.setHeaderText(mode.getDesc());
		
		/*
		 * Match the dialog icons to the main window
		 */
		((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
		
		/*
		 * Set the button types
		 */
		ButtonType confirmButtonType = new ButtonType("Start", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

		/*
		 * Create the find/replace fields
		 */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField find = new TextField();
		TextField replace = new TextField();

		grid.add(new Label("Find:"), 0, 0);
		grid.add(find, 1, 0);
		grid.add(new Label("Replace with:"), 0, 1);
		grid.add(replace, 1, 1);

		/*
		 * Confirm button disabled by default until valid entries are inputted
		 */
		Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
		confirmButton.setDisable(true);

		/*
		 * Set the confirm button to only be enabled for valid find values
		 */
		find.textProperty().addListener((observable, oldValue, newValue) -> {
			confirmButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		/*
		 * Request focus on the Find field 
		 */
		Platform.runLater(() -> find.requestFocus());

		/*
		 * Convert the values into a String Pair
		 */
		dialog.setResultConverter(dialogButton -> {
			
			if (dialogButton == confirmButtonType) {
				return new Pair<>(find.getText(), replace.getText());
			}
			
			return null;
		});

		/*
		 * Show the UI and return the values
		 */
		try {
			//Center the dialog once we show it
			//A workaround to the problem in this post: https://stackoverflow.com/questions/19025935/javafx-2-how-to-get-window-size-if-it-wasnt-set-manually
			Platform.runLater(() -> {
				dialog.setX(stage.getX() + stage.getWidth() / 2 - dialog.getWidth() / 2);
				dialog.setY(stage.getY() + stage.getHeight() / 2 - dialog.getHeight() / 2);
			});
			
			return dialog.showAndWait().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
}
