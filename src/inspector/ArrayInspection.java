package inspector;


import javafx.scene.control.TreeItem;

import java.util.function.Supplier;

public class ArrayInspection<T,S> extends AbstractInspection {

  private final Supplier<S[]> getter;
  private final String name;
  private S[] array;
  private Inspection[] inspections;

  public ArrayInspection(Supplier<S[]> getter, String name) {
    this.getter = getter;
    this.name = name;
    item = new TreeItem<>(name);
    initialize();
  }

  private void initialize() {
    array = getter.get();
    getItem().getChildren().clear();
    if (array == null) {
      getItem().setValue(name + ": null");
      return;
    }
    inspections = new Inspection[array.length];
    for (int i = 0; i < array.length; i++) {
      createValue(i);
    }
  }

  private void createValue(int index) {
    S value = array[index];
    Class<?> subClass = value.getClass();
    String indexName = Integer.toString(index);
    Inspection inspection =
      Inspections.buildInspector(
        subClass,
        () -> array[index],
        indexName
      );
    inspections[index] = inspection;
    inspection.update();
    item.getChildren().add(inspection.getItem());
  }


  @Override
  public void update() {
    S[] currentArray = getter.get();
    if (currentArray != array) {
      initialize();
      return;
    }
    for (Inspection inspection : inspections) {
      inspection.update();
    }
  }
}
