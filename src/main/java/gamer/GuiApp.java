package gamer;

import gamer.def.State;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GuiApp extends Application {
  private Font font;
  private StackPane statePane;
  private StackPane reportPane;

  private Font selectFont() {
    return new Font("Ubuntu Mono", 20);
  }

  private void updateState(State<?, ?> state) {
    Text text = new Text(state.toString());
    text.setFont(font);
    statePane.getChildren().clear();
    statePane.getChildren().add(text);
  }

  private void updateReport(String report) {
    Text text = new Text(report);
    text.setFont(font);
    reportPane.getChildren().clear();
    reportPane.getChildren().add(text);
  }

  @Override
  public void start(Stage primaryStage) {
    font = selectFont();

    statePane = new StackPane();
    reportPane = new StackPane();

    HBox.setHgrow(statePane, Priority.ALWAYS);
    HBox.setHgrow(reportPane, Priority.ALWAYS);

    HBox root = new HBox();
    root.setSpacing(2);
    root.getChildren().add(statePane);
    root.getChildren().add(reportPane);
    root.setMaxHeight(Double.MAX_VALUE);
    root.setMaxWidth(Double.MAX_VALUE);

    Scene scene = new Scene(root, 800, 450, Color.ALICEBLUE);

    scene.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ESCAPE) primaryStage.close();
    });

    primaryStage.setTitle("Gamer 0.2");
    primaryStage.setScene(scene);
    primaryStage.show();

    GamerTask gameTask = new GamerTask();
    gameTask.valueProperty().addListener((observable, oldValue, newValue) -> {
      updateState(newValue.state);
      updateReport(newValue.report);
    });

    Thread gameTaskThread = new Thread(gameTask);
    gameTaskThread.setDaemon(true);
    gameTaskThread.start();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
