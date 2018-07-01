package nokori.jdialogue.ui;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import nokori.jdialogue.JDialogueCore;

/**
 * This is a basic editor that is compatible with any type of DialogueNode.
 * 
 * While you can make a new editor from scratch, it may be useful to just extend this one.
 *
 */
public class TextViewerMenu extends StackPane {
	
	public static final int FADE_TIME = 300;
	
	private static final int EDITOR_SIZE_OFFSET_X = 200;
	private static final int EDITOR_SIZE_OFFSET_Y = 150;
	
	private boolean disposing = false;
	
	public TextViewerMenu(JDialogueCore core, Font textFont, String[] array) {
		this(core, textFont, toString(array));
	}

	private static String toString(String[] array) {
		StringBuilder s = new StringBuilder();
		
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				s.append("\n");
			}
			
			s.append(array[i]);
		}
		
		return s.toString();
	}
	
	public TextViewerMenu(JDialogueCore core, Font textFont, String text) {
		Scene scene = core.getScene();
		
		/*
		 * Fade out everything behind
		 */
		Rectangle background = new Rectangle();
		
		background.widthProperty().bind(scene.widthProperty());
		background.heightProperty().bind(scene.heightProperty());
		background.setFill(Color.BLACK);

		background.setOnMousePressed(event -> {
			if (!disposing) {
				dispose(core, background);
			}
		});
		
		background.setOnMouseDragged(event -> {
			if (!disposing) {
				dispose(core, background);
			}
		});

		FadeTransition backgroundFaderTransition = new FadeTransition(Duration.millis(FADE_TIME), background);
		backgroundFaderTransition.setFromValue(0.0);
		backgroundFaderTransition.setToValue(0.5);
		backgroundFaderTransition.play();
		
		core.getUIPane().getChildren().add(background);
		
		/*
		 * Editor
		 */
		
		//Editor background
		Rectangle editorBackground = new Rectangle();
		
		editorBackground.widthProperty().bind(scene.widthProperty().subtract(EDITOR_SIZE_OFFSET_X));
		editorBackground.heightProperty().bind(scene.heightProperty().subtract(EDITOR_SIZE_OFFSET_Y));
		
		editorBackground.setFill(Color.WHITE);
		editorBackground.setStroke(Color.BLACK);
		editorBackground.setArcHeight(JDialogueCore.ROUNDED_RECTANGLE_ARC);
		editorBackground.setArcWidth(JDialogueCore.ROUNDED_RECTANGLE_ARC);

		getChildren().add(editorBackground);
		
		/*
		 * Text Viewer
		 */
		
		InlineCssTextArea textArea = new InlineCssTextArea();
		textArea.replaceText(text);
		textArea.setStyle("-fx-font-family: '" + textFont.getFamily() + "'; -fx-font-size: " + textFont.getSize());
		textArea.setEditable(false);
		textArea.setWrapText(true);

		VirtualizedScrollPane<InlineCssTextArea> scrollPane = new VirtualizedScrollPane<InlineCssTextArea>(textArea);
		
		StackPane.setAlignment(scrollPane, Pos.CENTER);
		StackPane.setMargin(scrollPane, new Insets(20, 20, 20, 20));
		
		getChildren().add(scrollPane);
		
		/*
		 * Fade in
		 */
		
		double tarBackgroundX = scene.getWidth()/2 - editorBackground.getWidth()/2;
		double tarBackgroundY = scene.getHeight()/2 - editorBackground.getHeight()/2;
		
		setTranslateX(tarBackgroundX);
		
		TranslateTransition slideIn = new TranslateTransition(Duration.millis(FADE_TIME), this);
		slideIn.setFromY(tarBackgroundY - 70);
		slideIn.setToY(tarBackgroundY);
		slideIn.play();
		
		FadeTransition backgroundTransition = new FadeTransition(Duration.millis(FADE_TIME), this);
		backgroundTransition.setFromValue(0.0);
		backgroundTransition.setToValue(1.0);
		backgroundTransition.play();
		
		/*
		 * 
		 */
		
		requestFocus();
	}
	
	/**
	 * Begin "disposing" this editor: animate closing it and then remove it from the core
	 */
	protected void dispose(JDialogueCore core, Rectangle background) {
		setMouseTransparent(true);
		
		//Fade out the grayed out background and dispose it
		FadeTransition backgroundFadeTransition = new FadeTransition(Duration.millis(FADE_TIME/2), background);
		backgroundFadeTransition.setToValue(0.0);
		backgroundFadeTransition.play();
		backgroundFadeTransition.setOnFinished(event -> {
			core.getUIPane().getChildren().remove(background);
		});
		
		//Fade out this editor and dispose it
		FadeTransition editorFadeTransition = new FadeTransition(Duration.millis(FADE_TIME/2), this);
		editorFadeTransition.setToValue(0.0);
		editorFadeTransition.play();
		
		editorFadeTransition.setOnFinished(event -> {
			core.getUIPane().getChildren().remove(this);
		});
		
		//Set disposing to true so that this isn't called again
		disposing = true;
	}
}
