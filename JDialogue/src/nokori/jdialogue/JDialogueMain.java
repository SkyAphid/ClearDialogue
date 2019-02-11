package nokori.jdialogue;

import javax.swing.UIManager;

public class JDialogueMain {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		JDialogueCore.launch(args);
	}
}
