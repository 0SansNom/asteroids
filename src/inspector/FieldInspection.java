package inspector;

import javafx.scene.control.TreeItem;

import java.util.List;
import java.util.function.Supplier;

public class FieldInspection<S> extends AbstractInspection {

  private Inspection childInspection;
  private S childValue;
  private final Supplier<S> getter;
  private final String name;


  public FieldInspection(Supplier<S> getter, String name) {
    this.getter = getter;
    this.name = name;
    renewInspection();
  }


  private void reinitialize() {
    List<TreeItem<String>> itemList = item.getParent().getChildren();
    int index = itemList.indexOf(item);
    renewInspection();
    itemList.set(index,item);
  }

  private void renewInspection() {
    boolean isExpanded = item != null && item.isExpanded();
    childValue = getter.get();
    if (childValue == null) {
      childInspection = new ConstantInspection(name, "null");
    } else {
      childInspection = new ObjectInspection(name, childValue);
    }
    this.item = childInspection.getItem();
    this.item.setExpanded(isExpanded);
  }

  @Override
  public void update() {
    S currentChild = getter.get();
    if (currentChild == null || !currentChild.equals(childValue)) {
      reinitialize();
      return;
    }
    childInspection.update();
  }
}
