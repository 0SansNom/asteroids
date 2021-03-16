package inspector;

import javafx.scene.control.TreeItem;

import java.util.function.Supplier;

public class PrimitiveInspection<S> extends AbstractInspection {

  private final String name;
  private final Supplier<S> getter;

  public PrimitiveInspection(Supplier<S> getter, String name) {
    this.name = name;
    this.getter = getter;
    item = new TreeItem<>(name);
    update();
  }


  @Override
  public void update() {
    item.setValue(name + ": " + getter.get());
  }
}
