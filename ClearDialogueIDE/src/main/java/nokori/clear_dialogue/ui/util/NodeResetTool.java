package nokori.clear_dialogue.ui.util;

import java.io.File;

import nokori.clear.windows.util.TinyFileDialog;
import nokori.clear_dialogue.io.ClearDialogueJsonIO;
import nokori.clear_dialogue.project.Dialogue;
import nokori.clear_dialogue.project.Project;

/**
 * This is just a small programmer utility that can be used to recursively edit a large amount of Projects automatically. Only really useful for situations where Projects need to be re-exported
 * into a newer version.
 * 
 */
public class NodeResetTool {
	public void run() {
		File f = TinyFileDialog.showOpenFolderDialog("Open", new File(""));
		recursiveLoad(f.listFiles());
		System.out.println("Finish");
	}
	
	private void recursiveLoad(File[] files) {
		ClearDialogueJsonIO io = new ClearDialogueJsonIO();
		
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			
			if (f.isDirectory()) {
				recursiveLoad(f.listFiles());
			} else {
				Project p;
				
				try {
					p = io.importProject(f);
					
					p.setViewportX(0);
					p.setViewportY(0);
					
					for (int j = 0; j < p.getNumDialogue(); j++) {
						Dialogue d = p.getDialogue(j);
						
						d.setX(0);
						d.setY(0);
					}
					
					io.exportProject(p, f);
					
					System.out.println("Resetted and exported the settings for " + p.getName());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
