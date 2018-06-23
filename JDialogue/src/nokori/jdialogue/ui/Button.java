package nokori.jdialogue.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Button extends ButtonSkeleton{
	
	public static final int FADE_TIME = 200;
	public static final Color TEXT_COLOR = Color.rgb(240, 240, 240);
	
	private Rectangle highlight;
	
	public Button(int x, int y, int w, int h, int arc, DropShadow shadow, String buttonTitle, Font font) {
		super(x, y, w, h, arc, shadow);
		
		//Fade Rectangle
		highlight = new Rectangle(w, h);
		highlight.setFill(Color.WHITE);
		highlight.setOpacity(0.0);
		highlight.setArcHeight(arc);
		highlight.setArcWidth(arc);
		highlight.setMouseTransparent(true);
		
		//Text style
		Text text = new Text(buttonTitle);
		text.setFont(font);
		text.setFill(TEXT_COLOR);
		text.setMouseTransparent(true);
		
		//Compile to StackPane
		stackPane.getChildren().addAll(highlight, text);

		//Alignments
		StackPane.setMargin(text, new Insets(13, 0, 0, 0));
		StackPane.setAlignment(text, Pos.TOP_CENTER);
		
		StackPane.setAlignment(highlight, Pos.TOP_CENTER);
	}
	
	@Override
	public void mouseEntered(MouseEvent event, Rectangle background) {
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME), highlight);
		fadeTransition.setFromValue(highlight.getOpacity());
		fadeTransition.setToValue(0.3);
		fadeTransition.play();
	}
	
	@Override
	public void mouseExited(MouseEvent event, Rectangle background) {
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME), highlight);
		fadeTransition.setFromValue(highlight.getOpacity());
		fadeTransition.setToValue(0.0);
		fadeTransition.play();
	}
	
	public Rectangle getHighlightRectangle() {
		return highlight;
	}
	
	public StackPane getStackPane() {
		return stackPane;
	}
	
	public static String getTextColorCode() {
		return "rgb(" + (int) (255 * TEXT_COLOR.getRed()) + "," + (int)(255 * TEXT_COLOR.getGreen()) + "," + (int) (255 * TEXT_COLOR.getBlue()) + ")";
	}
}
