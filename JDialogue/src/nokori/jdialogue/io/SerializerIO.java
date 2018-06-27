package nokori.jdialogue.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser.ExtensionFilter;
import nokori.jdialogue.project.Project;

public class SerializerIO implements JDialogueIO {

	@Override
	public void exportProject(Project project, File f) {
		try {
			ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(f));
			output.writeObject(project);
			output.close();
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
		try {
			ObjectInputStream input = new ObjectInputStream(new FileInputStream(f));
			Project project = (Project) input.readObject();
			input.close();
			
			return project;
		} catch (Exception e) {
			e.printStackTrace();
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Caught Exception");
			alert.setHeaderText("Failed to import project.");
			alert.showAndWait();
			
			return new Project(0.0, 0.0, 1.0);
		}
	}

	@Override
	public ExtensionFilter getExtensionFilter() {
		return new ExtensionFilter("JDialogue Files (*.dialogue)", "*.dialogue");
	}

	@Override
	public String getTypeName() {
		return "dialogue";
	}

}
