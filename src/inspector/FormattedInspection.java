package inspector;

import javafx.scene.control.TreeItem;

import java.util.function.Supplier;

public class FormattedInspection<T> extends AbstractInspection {

  private final Supplier<T> supplier;
  private final String name;

  public FormattedInspection(Supplier<T> supplier, String name) {
    this.name = name;
    this.supplier = supplier;
    this.item = new TreeItem<>(name);
    update();
  }

  @Override
  public void update() {
    item.setValue(name + ": " + supplier.get());
  }
}
