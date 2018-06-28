package nokori.jdialogue.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.stage.FileChooser.ExtensionFilter;
import nokori.jdialogue.project.Project;

public class JDialogueSerializerIO implements JDialogueIO {

	@Override
	public void exportProject(Project project, File f) throws Exception {
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(f));
		output.writeObject(project);
		output.close();
	}

	@Override
	public Project importProject(File f) throws Exception {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(f));
		Project project = (Project) input.readObject();
		input.close();

		return project;
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
