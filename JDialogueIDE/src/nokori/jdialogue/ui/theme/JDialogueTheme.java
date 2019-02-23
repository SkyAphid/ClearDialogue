package nokori.jdialogue.ui.theme;

import lwjgui.Color;
import lwjgui.font.Font;
import lwjgui.theme.ThemeCoral;

public class JDialogueTheme extends ThemeCoral {

	private Font sans, serif;
	
	public JDialogueTheme() {
		sans = new Font("res/fonts/noto_sans/", "NotoSans-Regular.ttf", "NotoSans-Bold.ttf", "NotoSans-Italic.ttf", "NotoSans-Light.ttf");
		serif = new Font("res/fonts/noto_serif/", "NotoSerif-Regular.ttf", "NotoSerif-Bold.ttf", "NotoSerif-Italic.ttf", "NotoSerif-Light.ttf");
	}
	
	public Font getSansFont() {
		return sans;
	}

	public Font getSerifFont() {
		return serif;
	}
	
	@Override
	public Color getControl() {
		return Color.CORAL;
	}
}
