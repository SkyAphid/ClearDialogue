package nokori.jdialogue.ui.util;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCombination.SHIFT_ANY;
import static javafx.scene.input.KeyCombination.SHORTCUT_ANY;
import static org.fxmisc.wellbehaved.event.EventPattern.anyOf;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.util.UndoUtils;
import org.fxmisc.undo.UndoManager;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.reactfx.Subscription;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Various utilities to make the UI code cleaner
 */
public class JDialogueUtils {
	
	//In seconds
	private static final double TOOLTIP_SHOW_DELAY = 1;


	/**
	 * Utility function for clamping values to a min and max.
	 * @param value
	 * @param min
	 * @param max
	 * @return
	 */
	public static double clamp(double value, double min, double max) {

		if (Double.compare(value, min) < 0)
			return min;

		if (Double.compare(value, max) > 0)
			return max;

		return value;
	}
	
	/**
	 * Shortcut for adding Tooltips that follow JDialogue's default configurations.
	 * @param node
	 * @param tooltip
	 */
	public static Tooltip quickTooltip(Node node, int showDurationInSeconds, String string) {
		Tooltip tooltip = new Tooltip(string);
		
		tooltip.setShowDelay(javafx.util.Duration.seconds(TOOLTIP_SHOW_DELAY));
		tooltip.setShowDuration(javafx.util.Duration.seconds(showDurationInSeconds));
		Tooltip.install(node, tooltip);

		return tooltip;
	}
	
	/**
	 * Adapted from the java Rectangle API
	 */
	public static boolean rectanglesIntersect(double x1, double w1, double y1, double h1, double x2, double w2, double y2, double h2){
		double tw = w1;
		double th = h1;
		
		double rw = w2;
		double rh = h2;

		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}

		double tx = x1;
		double ty = y1;
		tw += tx;
		th += ty;
		
		double rx = x2;
		double ry = y2;
		rw += rx;
		rh += ry;

		// overflow || intersect
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty)
				&& (tw < tx || tw > rx) && (th < ty || th > ry));
	}
	
	/**
	 * Shortcut for showing basic alerts. Alerts are automatically centered on the window.
	 * 
	 * @param stage
	 * @param alertType
	 * @param title
	 * @param header
	 * @param message
	 */
	public static void showAlert(Stage stage, AlertType alertType, String title, String header, String message) {
		Alert alert = new Alert(alertType);
		
		((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
		
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		
		
		Platform.runLater(() -> {
			alert.setX(stage.getX() + stage.getWidth() / 2 - alert.getWidth() / 2);
			alert.setY(stage.getY() + stage.getHeight() / 2 - alert.getHeight() / 2);
		});
		
		alert.showAndWait();
	}
	
	public static void showAlert(Stage stage, AlertType alertType, String title, String header) {
		showAlert(stage, alertType, title, header, "");
	}
	
	/**
	 * Shortcut for showing a confirmation alert. Returns true if the user selects "yes."
	 * 
	 * @param stage
	 * @param alertType
	 * @param title
	 * @param header
	 * @param message
	 */
	public static boolean showConfirmAlert(Stage stage, AlertType alertType, String title, String header, String message) {
		Alert alert = new Alert(alertType);
		
		((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
		
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		
		//Create buttons
		ButtonType yesButtonType = new ButtonType("Yes", ButtonData.OK_DONE);
		alert.getDialogPane().getButtonTypes().clear();
		alert.getDialogPane().getButtonTypes().addAll(yesButtonType, ButtonType.CANCEL);
		
		//Center alert
		Platform.runLater(() -> {
			alert.setX(stage.getX() + stage.getWidth() / 2 - alert.getWidth() / 2);
			alert.setY(stage.getY() + stage.getHeight() / 2 - alert.getHeight() / 2);
		});
		
		//Show and wait
		Optional<ButtonType> result = alert.showAndWait();
		
		return (result.get() == yesButtonType);
	}
	
	/**
	 * Show a centered dialog with a string input field and wait for the users input. If the text field is empty or the X button is pressed, null is returned.
	 * 
	 * @param stage
	 * @param alertType
	 * @param title
	 * @param header
	 * @param message
	 * @return
	 */
	public static String showInputDialog(Stage stage, String title, String header, String defaultText, String fieldLabel) {
		TextInputDialog dialog = new TextInputDialog(defaultText);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(fieldLabel);
		
		//Set dialog icon to match the main window
		((Stage) dialog.getDialogPane().getScene().getWindow()).getIcons().addAll(stage.getIcons());
		
		//Center dialog
		Platform.runLater(() -> {
			dialog.setX(stage.getX() + stage.getWidth() / 2 - dialog.getWidth() / 2);
			dialog.setY(stage.getY() + stage.getHeight() / 2 - dialog.getHeight() / 2);
		});

		//Show and wait
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent()){
		  	return result.get();
		} else {
			return null;
		}
	}
	
	/**
	 * Return string width/height.
	 * 
	 * Hurrah for hacked functionality
	 * 
	 * Pulled from: https://stackoverflow.com/questions/32237048/javafx-fontmetrics
	 * 
	 * @param font
	 * @param s
	 * @return
	 */
	public static Bounds getStringBounds(Font font, String s) {
	    Text text = new Text(s);
	    text.setFont(font);
	    
	    Bounds textBounds = text.getBoundsInLocal();
	    
	    Rectangle stencil = new Rectangle(textBounds.getMinX(), textBounds.getMinY(), textBounds.getWidth(), textBounds.getHeight());
	    
	    Shape intersection = Shape.intersect(text, stencil);
	    
	    Bounds intersectionBounds = intersection.getBoundsInLocal();
	    //System.out.println(s + " Text size: " + intersectionBounds.getWidth() + ", " + intersectionBounds.getHeight());
	    
	    return intersectionBounds;
	}
	
	/**
	 * Quick access to a file within the JAR
	 * @param packagePath
	 * @return
	 */
	public static InputStream loadFromPackage(String packagePath) {
		return JDialogueUtils.class.getClassLoader().getResourceAsStream(packagePath);
	}
	
	/**
	 * RichTextFX doesn't have TextFields (yet)
	 */
	
	public static void disableMultiLineShortcuts(InlineCssTextArea area) {
		InputMap<Event> map = InputMap.consume(anyOf(keyPressed(ENTER, SHORTCUT_ANY, SHIFT_ANY)));
		Nodes.addInputMap(area, map);
	}
	
	/**
	 * Adds a syntax subscription to the text area that will highlight tags. Be sure to dispose it when your done with unsubscribe()!
	 * @param textArea
	 */
	public static Subscription addSyntaxSubscription(InlineCssTextArea textArea, String[] keywords, String colorFillCode) {
		
		//Sets up the UndoManager to not undo text highlighting when CTRL-Z is pressed
		UndoManager<List<PlainTextChange>> um = UndoUtils.plainTextUndoManager(textArea);
		textArea.setUndoManager(um);
		
		//Applies the subscription
		return textArea.multiPlainChanges().successionEnds(Duration.ofMillis(100))
				.subscribe(ignore -> computeHighlighting(textArea, keywords, colorFillCode));
	}
	
	/**
	 * Compute highlighting for InlineCssTextAreas
	 * @param textArea
	 */
	public static void computeHighlighting(InlineCssTextArea textArea, String[] keywords, String colorFillCode) {
		String text = textArea.getText();
		
		textArea.setStyle(0, textArea.getLength(), "");
		
		for (int i = 0; i < keywords.length; i++) {
			String keyword = keywords[i];
			
			if (keyword.trim().isEmpty() || keyword.startsWith("//")) continue;
			
			boolean containsKeywords = true;
			int lastEnd = 0;
			
			while(containsKeywords) {
				int start = text.indexOf(keyword, lastEnd);
				int end = start + keyword.length();
				
				if (start >= 0) {
					textArea.setStyle(start, end, "-fx-fill: " + colorFillCode + ";");
					lastEnd = end;
				}else {
					containsKeywords = false;
				}
			}
		}
	}
}
