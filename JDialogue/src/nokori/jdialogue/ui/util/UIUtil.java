package nokori.jdialogue.ui.util;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCombination.*;
import static org.fxmisc.wellbehaved.event.EventPattern.*;

import javafx.event.Event;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.PlainTextChange;
import org.fxmisc.richtext.util.UndoUtils;
import org.fxmisc.undo.UndoManager;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.reactfx.Subscription;

/**
 * Various utilities to make the UI code cleaner
 */
public class UIUtil {
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
		return UIUtil.class.getClassLoader().getResourceAsStream(packagePath);
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
	public static  Subscription addSyntaxSubscription(InlineCssTextArea textArea, String[] keywords, String colorFillCode) {
		
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
