package nokori.jdialogue.ui;

import javafx.animation.FadeTransition;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import nokori.jdialogue.util.FontUtil;
import nokori.jdialogue.util.RectangleResizeHeightTransition;

public class MenuButton extends Button{
	
	private int defaultH;
	private int extendedH;
	
	private boolean menuButtonSelected = false;
	private Group optionGroup = new Group();
	
	public MenuButton(Scene scene, int w, int h, int arc, DropShadow shadow, String buttonTitle, Font titleFont, Font optionsFont, String[] options, int optionIncrementH) {
		super(scene, w, h, arc, shadow, buttonTitle, titleFont);
		defaultH = h;
		
		extendedH = (int) ((options.length + 1) * optionIncrementH);
		
		for (int i = 0; i < options.length; i++) {
			double y = h + (i * (optionIncrementH));
			
			/*
			 * Text
			 */
			Text option = new Text(options[i]);
			option.setFont(optionsFont);
			option.setFill(Button.TEXT_COLOR);
			
			option.setOnMouseEntered(event -> {
				
			});
			
			option.setOnMouseExited(event -> {
				
			});
			
			option.setOnMouseClicked(event -> {
				
			});
			
			option.setMouseTransparent(true);
			
			option.setX(BUTTON_MARGIN_X);
			option.setY(y + optionIncrementH/2 + FontUtil.getStringBounds(optionsFont, options[i]).getHeight()/2);

			/*
			 * Highlight
			 */
			
			Rectangle highlight = new Rectangle(w, optionIncrementH);
			highlight.setFill(Color.WHITE);
			highlight.setOpacity(0.0);
			highlight.setX(0);
			highlight.setY(y);
			
			highlight.setOnMouseEntered(event -> {
				FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME), highlight);
				fadeTransition.setFromValue(highlight.getOpacity());
				fadeTransition.setToValue(FADE_MAX_OPACITY - 0.15f);
				fadeTransition.play();
			});
			
			highlight.setOnMouseExited(event -> {
				FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADE_TIME), highlight);
				fadeTransition.setFromValue(highlight.getOpacity());
				fadeTransition.setToValue(0.0);
				fadeTransition.play();
			});
			
			int index = i;
			
			highlight.setOnMouseClicked(event -> {
				optionClicked(event, options[index], index);
			});
			
			optionGroup.getChildren().addAll(highlight, option);
		}
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		super.mouseEntered(event);
		
		var transition = new RectangleResizeHeightTransition(Duration.millis(Button.FADE_TIME), getBackgroundRectangle(), extendedH);
		transition.setOnFinished(fin -> {
			if (menuButtonSelected) {
				getPane().getChildren().add(optionGroup);
			}
		});
		transition.play();
		
		menuButtonSelected = true;
	}
	
	@Override
	public void mouseExited(MouseEvent event) {
		super.mouseExited(event);
		
		getPane().getChildren().remove(optionGroup);
		new RectangleResizeHeightTransition(Duration.millis(Button.FADE_TIME), getBackgroundRectangle(), defaultH).play();
		
		menuButtonSelected = false;
	}

	public void optionClicked(MouseEvent event, String optionName, int optionIndex) {
		
	}
}
