package nokori.jdialogue.ui.util;

import java.io.InputStream;
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
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

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
	 * RichTextFX doesn't have TextFields
	 */
	
	public static void disableMultiLineShortcuts(InlineCssTextArea area) {
		InputMap<Event> map = InputMap.consume(anyOf(keyPressed(ENTER, SHORTCUT_ANY, SHIFT_ANY)));
		Nodes.addInputMap(area, map);
	}
}
