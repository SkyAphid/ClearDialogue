package nokori.jdialogue.ui.util;

import java.io.InputStream;
import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Various utilities to make the UI code cleaner
 */
public class UIUtil {
	/**
	 * Return string width/height.
	 * 
	 * Because JavaFX is arbitrarily difficult to use
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
}
