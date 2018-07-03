package nokori.jdialogue.test;

import java.time.Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.Subscription;

public class SyntaxTest extends Application {

	private static final String[] KEYWORDS = new String[] { "[[test]]" };

	private static final String sampleCode = "this is a syntax [[test]]";

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		InlineCssTextArea textArea = new InlineCssTextArea();
		
		 Subscription cleanupWhenNoLongerNeedIt = textArea
	                .multiPlainChanges()
	                .successionEnds(Duration.ofMillis(100))
	                .subscribe(ignore -> computeHighlighting(textArea, KEYWORDS, "coral"));

		textArea.setParagraphGraphicFactory(LineNumberFactory.get(textArea));
		
		textArea.replaceText(0, 0, sampleCode);

		Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(textArea)), 600, 400);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Syntax Test");
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(event ->{
			cleanupWhenNoLongerNeedIt.unsubscribe();
		});
	}

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
