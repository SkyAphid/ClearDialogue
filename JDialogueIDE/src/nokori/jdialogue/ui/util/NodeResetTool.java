package nokori.jdialogue.ui.util;

import java.io.File;

import lwjgui.LWJGUIDialog;
import nokori.jdialogue.io.JDialogueJsonIO;
import nokori.jdialogue.project.Dialogue;
import nokori.jdialogue.project.Project;

/**
 * This is just a small utility that can be used to recursively edit a large amount of Projects automatically. Only really useful for situations where Projects need to be re-exported
 * into a newer version.
 * 
 */
public class NodeResetTool {
	public void run() {
		File f = LWJGUIDialog.showOpenFolderDialog("Open", new File(""));
		recursiveLoad(f.listFiles());
		System.out.println("Finish");
	}
	
	private void recursiveLoad(File[] files) {
		JDialogueJsonIO io = new JDialogueJsonIO();
		
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
