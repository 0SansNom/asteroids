package inspector;

import javafx.event.Event;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;


public class View {

  private final Inspection inspection;


  public View(TreeView treeView, Inspection inspection) {
    this.inspection = inspection;
    treeView.addEventFilter(KeyEvent.ANY, Event::consume);
    TreeItem<String> root = inspection.getItem();
    treeView.setRoot(root);
    root.setExpanded(true);
  }

  public void render() {
    inspection.update();
  }

}

