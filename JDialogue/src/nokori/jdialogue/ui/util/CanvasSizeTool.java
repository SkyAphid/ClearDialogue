package nokori.jdialogue.ui.util;

import java.util.NoSuchElementException;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import nokori.jdialogue.JDialogueCore;
import nokori.jdialogue.project.Project;
import nokori.jdialogue.ui.pannable_pane.PannablePane;

public class CanvasSizeTool {
	private boolean canvasWidthValid = true, canvasHeightValid = true;
	
	public void openCanvasSizeDialog(Stage stage, JDialogueCore core, Project project, PannablePane pannablePane) {
		/*
		 * Create the custom dialog.
		 */
		Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
		dialog.setTitle("Canvas Size");
		dialog.setHeaderText("The canvas is the area in which you can create and move nodes."
				+ "\nIf needed, a larger work area can be created by modifying the following fields.");
		/*
		 * Set dialog icon to match the main window
		 */
		((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
		
		/*
		 * Add button types
		 */
		ButtonType confirmButtonType = new ButtonType("Change Size", ButtonData.OK_DONE);
		ButtonType resetButtonType = new ButtonType("Reset Canvas Size to Default", ButtonData.BACK_PREVIOUS);
		dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, resetButtonType, ButtonType.CANCEL);

		/*
		 * Create the canvas size fields
		 */
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField canvasWidthField = new TextField(Integer.toString(project.getCanvasWidth()));
		TextField canvasHeightField = new TextField(Integer.toString(project.getCanvasHeight()));

		grid.add(new Label("Canvas Width:"), 0, 0);
		grid.add(canvasWidthField, 1, 0);
		grid.add(new Label("Canvas Height:"), 0, 1);
		grid.add(canvasHeightField, 1, 1);

		/*
		 * Confirm button disabled by default until the values are changed at least once and they're valid
		 */
		Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
		confirmButton.setDisable(true);

		/*
		 * Check if the new value is a valid canvas size
		 */
		canvasWidthField.textProperty().addListener((observable, oldValue, newValue) -> {
			canvasWidthValid = validChange(Project.MINIMUM_CANVAS_WIDTH, newValue);
			confirmButton.setDisable(!canvasWidthValid || !canvasHeightValid);
		});
		
		canvasHeightField.textProperty().addListener((observable, oldValue, newValue) -> {
			canvasHeightValid = validChange(Project.MINIMUM_CANVAS_HEIGHT, newValue);
			confirmButton.setDisable(!canvasWidthValid || !canvasHeightValid);
		});

		dialog.getDialogPane().setContent(grid);

		/*
		 * Convert the values to a Pair for easy parsing
		 */
		dialog.setResultConverter(dialogButton -> {
			
			if (dialogButton == confirmButtonType) {
				return new Pair<>(Integer.parseInt(canvasWidthField.getText()), Integer.parseInt(canvasHeightField.getText()));
			}
			
			if (dialogButton == resetButtonType) {
				return new Pair<>(Project.DEFAULT_CANVAS_WIDTH, Project.DEFAULT_CANVAS_HEIGHT);
			}
			
			return null;
		});
		
		/*
		 * Change the canvas size when the confirm button is pressed
		 */
		try {
			//Center the dialog once we show it
			//A workaround to the problem in this post: https://stackoverflow.com/questions/19025935/javafx-2-how-to-get-window-size-if-it-wasnt-set-manually
			Platform.runLater(() -> {
				dialog.setX(stage.getX() + stage.getWidth() / 2 - dialog.getWidth() / 2);
				dialog.setY(stage.getY() + stage.getHeight() / 2 - dialog.getHeight() / 2);
			});

			//Show the dialog and wait for the input
			Pair<Integer, Integer> canvasSize = dialog.showAndWait().get();
			
			int canvasWidth = canvasSize.getKey();
			int canvasHeight = canvasSize.getValue();
			
			//Set the project and pannablePane to the new canvas size
			project.setCanvasWidth(canvasWidth);
			project.setCanvasHeight(canvasHeight);
			project.setViewportX(0.0);
			project.setViewportY(-canvasHeight/2);
			project.setViewportScale(1.0);
			
			core.refreshUI();
		} catch (NoSuchElementException e) {
			//This means the value wasn't changed, so we do nothing.
		}
	}
	
	/**
	 * Check for valid canvas sizes
	 * 
	 * @param oldSize
	 * @param newValue
	 * @return
	 */
	private static boolean validChange(int minSize, String newValue) {
		boolean valueEmpty = newValue.trim().isEmpty();
		boolean valueValid = false;
		
		try {
			int valueInt = Integer.parseInt(newValue);
			valueValid = (valueInt >= minSize);
		} catch (NumberFormatException e) {
			valueValid = false;
		}
		
		return (!valueEmpty && valueValid);
	}
}
