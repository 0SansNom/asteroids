package inspector;

import javafx.scene.control.TreeItem;

public class ConstantInspection extends AbstractInspection {

  public ConstantInspection(String name, String value) {
    this.item = new TreeItem<String>();
    item.setValue(name + ": " + value);
  }

  @Override
  public void update() {
    // do nothing
  }
}
