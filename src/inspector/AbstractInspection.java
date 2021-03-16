package inspector;

import javafx.scene.control.TreeItem;

public abstract class AbstractInspection implements Inspection {

  protected TreeItem<String> item;

  @Override
  public TreeItem<String> getItem() {
    return item;
  }

  @Override
  public abstract void update();

}
