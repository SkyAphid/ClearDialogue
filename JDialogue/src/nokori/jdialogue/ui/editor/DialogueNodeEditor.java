package nokori.jdialogue.ui.editor;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.DialogueNode;
import nokori.jdialogue.ui.node.DialogueNodePane;

/**
 * This is a basic editor that is compatible with any type of DialogueNode.
 * 
 * While you can make a new editor from scratch, it may be useful to just extend this one.
 *
 */
public abstract class DialogueNodeEditor extends StackPane {
	
	public static final int FADE_TIME = 300;
	
	private static final int EDITOR_SIZE_OFFSET_X = 200;
	private static final int EDITOR_SIZE_OFFSET_Y = 150;
	
	public static final int START_BODY_Y = 130;
	
	private boolean disposing = false;

	public DialogueNodeEditor(JDialogueCore core, DialogueNode node, DialogueNodePane pane, Font titleFont) {
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
				dispose(core, node, pane, background);
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
		 * Name field
		 */
		
		TextField textField = new TextField(node.getName());
		textField.setFont(titleFont);
		textField.setBackground(Background.EMPTY);
		textField.setStyle("-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
		
		//Update node name 
		textField.textProperty().addListener((o, oldText, newText) -> {
			node.setName(newText);
		});
		
		//having enter cancel out the focus gives a feeling of confirmation
		textField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				core.getUIPane().requestFocus();
			}
		});
		
		StackPane.setAlignment(textField, Pos.TOP_LEFT);
		StackPane.setMargin(textField, new Insets(20, 20, 20, 20));
		
		getChildren().add(textField);
		
		/*
		 * Tag field
		 */
		
		TextField tagField = new TextField(node.getTag());
		tagField.setFont(titleFont);
		tagField.setBackground(Background.EMPTY);
		tagField.setStyle("-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
		
		//Update node name 
		tagField.setOnKeyPressed(event -> {
			node.setTag(tagField.getText());
		});
		
		//having enter cancel out the focus gives a feeling of confirmation
		tagField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				node.setTag(tagField.getText());
				core.getUIPane().requestFocus();
			}
		});
		
		StackPane.setAlignment(tagField, Pos.TOP_LEFT);
		StackPane.setMargin(tagField, new Insets(70, 20, 20, 20));
		
		getChildren().add(tagField);
		
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
	}
	
	/**
	 * Begin "disposing" this editor: animate closing it and then remove it from the core
	 */
	protected void dispose(JDialogueCore core, DialogueNode node, DialogueNodePane dialogueNodePane, Rectangle background) {
		setMouseTransparent(true);
		
		//Update the node pane with the new data
		dialogueNodePane.refresh(core);
		
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
