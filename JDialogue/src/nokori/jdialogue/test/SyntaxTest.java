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
	                .subscribe(ignore -> computeHighlighting(textArea));

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

	private static void computeHighlighting(InlineCssTextArea codeArea) {
		String text = codeArea.getText();
		
		codeArea.setStyle(0, codeArea.getLength(), "");
		
		for (int i = 0; i < KEYWORDS.length; i++) {
			String keyword = KEYWORDS[i];
			
			boolean containsKeywords = true;
			int lastEnd = 0;
			
			while(containsKeywords) {
				int start = text.indexOf(keyword, lastEnd);
				int end = start + keyword.length();
				
				if (start >= 0) {
					codeArea.setStyle(start, end, "-fx-fill: coral;");
					lastEnd = end;
				}else {
					containsKeywords = false;
				}
			}
		}
	}
}
