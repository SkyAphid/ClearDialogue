package nokori.jdialogue.ui.editor;

import org.fxmisc.richtext.InlineCssTextArea;
import org.reactfx.Subscription;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import nokori.jdialogue.ui.util.JDialogueUtils;

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
	
	private Subscription nameFieldSub, tagFieldSub;
	
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
		
		background.setOnMouseDragged(event -> {
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
		
		InlineCssTextArea nameField = new InlineCssTextArea();
		nameField.setBackground(Background.EMPTY);
		nameField.setMaxHeight(30);
		
		//Update node name 
		nameField.textProperty().addListener((o, oldText, newText) -> {
			node.setName(newText);
		});
		
		//having enter cancel out the focus gives a feeling of confirmation
		nameField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				core.getUIPane().requestFocus();
			}
		});
		
		JDialogueUtils.disableMultiLineShortcuts(nameField);
		
		//Style/Syntax
		nameField.setStyle("-fx-font-family: '" + titleFont.getFamily() + "'; -fx-font-size: " + titleFont.getSize() + ";"
			 	 		 + "-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
		
		nameFieldSub = JDialogueUtils.addSyntaxSubscription(nameField, core.getSyntax(), JDialogueCore.SYNTAX_HIGHLIGHT_COLOR);
		
		//Set text
		nameField.replaceText(node.getName());
		
		//Alignmnet/Placement
		StackPane.setAlignment(nameField, Pos.TOP_LEFT);
		StackPane.setMargin(nameField, new Insets(20, 20, 20, 20));
		
		getChildren().add(nameField);
		
		/*
		 * Tag field
		 */
		
		InlineCssTextArea tagField = new InlineCssTextArea();
		tagField.setBackground(Background.EMPTY);
		tagField.setMaxHeight(30);
		
		//Update node name 
		tagField.textProperty().addListener((o, oldText, newText) -> {
			node.setTag(newText);
		});
		
		//having enter cancel out the focus gives a feeling of confirmation
		tagField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				node.setTag(tagField.getText());
				core.getUIPane().requestFocus();
			}
		});
		
		JDialogueUtils.disableMultiLineShortcuts(tagField);
		
		//Style/Syntax
		tagField.setStyle("-fx-font-family: '" + titleFont.getFamily() + "'; -fx-font-size: " + titleFont.getSize() + ";"
						+ "-fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
		
		tagFieldSub = JDialogueUtils.addSyntaxSubscription(tagField, core.getSyntax(), JDialogueCore.SYNTAX_HIGHLIGHT_COLOR);
		
		//Set text
		tagField.replaceText(node.getTag());
		
		//Alignment
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
		nameFieldSub.unsubscribe();
		tagFieldSub.unsubscribe();
		
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
