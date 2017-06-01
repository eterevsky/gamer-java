package gamer;

import gamer.util.Version;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GuiApp extends Application {
  @Override
  public void start(Stage primaryStage) {
    Button exitBtn = new Button();
    exitBtn.setText("Exit 1");
    exitBtn.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            primaryStage.close();
          }
        });

    Button exit2Btn = new Button();
    exit2Btn.setText("Exit 2");
    exit2Btn.setOnAction(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            primaryStage.close();
          }
        });

    HBox root = new HBox();

    HBox.setHgrow(exitBtn, Priority.ALWAYS);
    HBox.setHgrow(exit2Btn, Priority.ALWAYS);

    exitBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    exit2Btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);


    root.getChildren().add(exitBtn);
    root.getChildren().add(exit2Btn);


    Scene scene = new Scene(root, Color.BLACK);

    primaryStage.setTitle("Gamer 0.2");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
