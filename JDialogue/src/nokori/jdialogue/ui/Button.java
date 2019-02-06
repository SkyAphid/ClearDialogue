package nokori.jdialogue.ui;

import javafx.animation.FadeTransition;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.ui.util.JDialogueUtils;

public class Button extends ButtonSkeleton{
	
	public static final Color TEXT_COLOR = Color.rgb(240, 240, 240);
	
	public static final int BUTTON_MARGIN_X = 20;
	
	public static final int FADE_TIME = 200;
	public static final double FADE_MAX_OPACITY = 0.3;
	
	private Scene scene;
	
	private Rectangle highlight;
	
	public Button(Scene scene, int w, int h, DropShadow shadow, String buttonTitle, Font font) {
		super(w, h, shadow);
		
		this.scene = scene;
		
		//Fade Rectangle
		highlight = new Rectangle(w, h);
		highlight.setFill(Color.WHITE);
		highlight.setOpacity(0.0);
		highlight.setArcHeight(JDialogueCore.ROUNDED_RECTANGLE_ARC);
		highlight.setArcWidth(JDialogueCore.ROUNDED_RECTANGLE_ARC);
		highlight.setMouseTransparent(true);
		
		//Text style
		Text text = new Text(buttonTitle);
		text.setFont(font);
		text.setFill(TEXT_COLOR);
		text.setMouseTransparent(true);
		
		Bounds bounds = JDialogueUtils.getStringBounds(font, buttonTitle);
		text.setLayoutX(BUTTON_MARGIN_X);
		text.setLayoutY(h/2 + bounds.getHeight()/2);
		
		//Compile to pane
		getChildren().addAll(highlight, text);
	}
	
	@Override
	public void mouseEntered(MouseEvent event) {
		scene.setCursor(Cursor.HAND);
		
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME), highlight);
		fadeTransition.setFromValue(highlight.getOpacity());
		fadeTransition.setToValue(FADE_MAX_OPACITY);
		fadeTransition.play();
	}
	
	@Override
	public void mouseExited(MouseEvent event) {
		scene.setCursor(Cursor.DEFAULT);
		
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME), highlight);
		fadeTransition.setFromValue(highlight.getOpacity());
		fadeTransition.setToValue(0.0);
		fadeTransition.play();
	}
	
	public Rectangle getHighlightRectangle() {
		return highlight;
	}
	
	public static String getTextColorCode() {
		return "rgb(" + (int) (255 * TEXT_COLOR.getRed()) + "," + (int)(255 * TEXT_COLOR.getGreen()) + "," + (int) (255 * TEXT_COLOR.getBlue()) + ")";
	}
}
