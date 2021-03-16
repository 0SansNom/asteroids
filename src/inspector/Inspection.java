package inspector;

import javafx.scene.control.TreeItem;

public interface Inspection {

  TreeItem<String> getItem();
  void update();
}
