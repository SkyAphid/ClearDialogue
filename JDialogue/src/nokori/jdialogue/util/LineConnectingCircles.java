package nokori.jdialogue.util;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LineConnectingCircles extends Application{
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        Circle circle1=new Circle(10, Color.GREEN);
        root.getChildren().add(circle1);
        Circle circle2=new Circle(10, Color.RED);
        root.getChildren().add(circle2);

        // move circles so we can see them:

        circle1.setTranslateX(100);

        circle2.setTranslateY(50);


        Line line = new Line();

        // bind ends of line:
        line.startXProperty().bind(circle1.centerXProperty().add(circle1.translateXProperty()));
        line.startYProperty().bind(circle1.centerYProperty().add(circle1.translateYProperty()));
        line.endXProperty().bind(circle2.centerXProperty().add(circle2.translateXProperty()));
        line.endYProperty().bind(circle2.centerYProperty().add(circle2.translateYProperty()));

        root.getChildren().add(line);

        // create some animations for the circles to test the line binding:

        Button button = new Button("Animate");

        TranslateTransition circle1Animation = new TranslateTransition(Duration.seconds(1), circle1);
        circle1Animation.setByY(150);


        TranslateTransition circle2Animation = new TranslateTransition(Duration.seconds(1), circle2);
        circle2Animation.setByX(150);

        ParallelTransition animation = new ParallelTransition(circle1Animation, circle2Animation);

        animation.setAutoReverse(true);
        animation.setCycleCount(2);
        button.disableProperty().bind(animation.statusProperty().isEqualTo(Animation.Status.RUNNING));
        button.setOnAction(e -> animation.play());

        BorderPane.setAlignment(button, Pos.CENTER);
        BorderPane.setMargin(button, new Insets(10));

        Scene scene = new Scene(new BorderPane(root, null, null, button, null), 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    public static void main(String[] args) {
        launch(args);
    }
}
