package views;

import inspector.Inspections;
import viewModel.ViewModel;
import inspector.ObjectInspection;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * The fr.univamu.asteroid.view handles the graphical user interface. It receives and processes
 * the keyboard events, and regularly redraws itself.
 * <p>
 * You should not modify this class, except when invited to,
 */
public class View {


  /**
   * Handles the event of a key pressed on the keyboard. Each time a key
   * is pressed, this method is activated with as parameter a value containing
   * all necessary information about which key is pressed.
   *
   * @param keyEvent a representation of the keyboard event
   */
  public void handleKeyPressed(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case UP:
        viewModel.startSpaceshipMainEngine();
        break;
      case DOWN:
        viewModel.brakeSpaceshipEngine();
        break;
      case LEFT:
        viewModel.startSpaceshipLeftEngine();
         break;
      case RIGHT:
        viewModel.startSpaceshipRightEngine();
        break;
      case SPACE:
        viewModel.fireSpaceshipGun();
        break;
    }
  }

  /**
   * Handles the event of a key released on the keyboard. Each time a key
   * is released, this method is activated with as parameter a value containing
   * all necessary information about which key is released.
   *
   * @param keyEvent a representation of the keyboard event
   */
  public void handleKeyReleased(KeyEvent keyEvent) {
    switch (keyEvent.getCode()) {
      case UP:
        viewModel.stopSpaceshipMainEngine();
        break;
      case DOWN:
        viewModel.stopSpaceship();
        break;
      case LEFT:
        viewModel.stopSpaceshipLeftEngine();
        break;
      case RIGHT:
        viewModel.stopSpaceshipRightEngine();
        break;
    }

  }


  /**
   * DO NOT MODIFY BELOW THIS LINE!
   */

  private ViewModel viewModel; // viewModel, to communicate with the game state.
  private CanvasView canvasView; // canvas, to draw the game board.
  private inspector.View inspectionView; // a tool to visualize game data in live.
  private final List<String> inspectablePackages =
    List.of(
      "game",
      "tools"
      ); // only objects from these packages are displayed in the data view
         // other objects will appear as "hidden".

  @FXML
  private Canvas canvas;
  @FXML
  private TreeView<String> treeView; // to visualize the game data
  @FXML
  private Button runButton; // start/stop the game
  @FXML
  private Button stepButton; // allows to spend a fix delay
  @FXML
  private TextField timeStepField; // defines the delay attached to the step button

  private final Timer timer = new Timer(this);


  /**
   * Method called by the animation timer when the scene must be updated.
   *
   * @param dt how much time spent since last tick, in seconds.
   */
  public void tick(double dt) {
    update(dt);
    render();
    if (isGameOver()) {
      timer.stop();
    }
  }


  /**
   * Update the state of the game by a delay of dt seconds.
   *
   * @param dt the delay since last update.
   */
  private void update(double dt) {
    viewModel.tick(dt);
  }


  /**
   * Redraw the scene.
   */
  public void render() {
    canvasView.render();
    inspectionView.render();
  }



  /**
   * Initialize the view: canvas, button and event handlers, and start the timer.
   *
   * @param viewModel the viewModel of the game.
   */
  public void initialize(ViewModel viewModel) {
    this.viewModel = viewModel;
    canvasView = new CanvasView(canvas, viewModel);
   for (String packageName : inspectablePackages) {
      Inspections.addAuthorizedPackage(packageName);
    }
     inspectionView =
      new inspector.View(treeView, new ObjectInspection("model", viewModel));
    canvas.setWidth(Main.CANVAS_WIDTH);
    canvas.setHeight(Main.CANVAS_HEIGHT);
    render();
    runButton.setOnMouseClicked(this::handleRunClicked);
    stepButton.setOnMouseClicked(this::handleStepClicked);
    timer.start();
  }


  /**
   * Bind the keyboard event to the window.
   *
   * @param scene the scene described in the window.
   */
  void setEventHandler(Scene scene) {
    scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    scene.addEventFilter(KeyEvent.KEY_RELEASED, this::handleKeyReleased);
  }

  /**
   * Defines what happens when the user click the step button.
   *
   * @param mouseEvent the click event
   */
  private void handleStepClicked(MouseEvent mouseEvent) {
    try {
      double dt = Double.parseDouble(timeStepField.getText());
      tick(dt);
    } catch (NumberFormatException e) {
      timeStepField.setText("number expected");
    }
  }


  /**
   * Defines what happens when the user click the run button.
   *
   * @param mouseEvent the click event
   */
  private void handleRunClicked(MouseEvent mouseEvent) {
    if (timer.isStopped()) {
      timer.restart();
      runButton.setText("Stop");
      stepButton.setVisible(false);
    } else {
      timer.stop();
      runButton.setText("Continue");
      stepButton.setVisible(true);
    }
  }


  /**
   * @return whether the game is over.
   */
  public boolean isGameOver() {
    return viewModel.isGameOver();
  }
}
