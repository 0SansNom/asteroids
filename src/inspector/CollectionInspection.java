package inspector;

import javafx.scene.control.TreeItem;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class CollectionInspection<T> extends AbstractInspection {

  private final Supplier<Collection<T>> getter;
  private Collection<T> values;
  private final Map<T, Inspection> inspections = new HashMap<>();
  private final String name;



  public <P> CollectionInspection(Supplier<Collection<T>> getter, String name) {
    this.getter = getter;
    this.name = name;
    this.item = new TreeItem<>("");
    item.setExpanded(false);
    update();
  }

  private void initCollection() {
    Collection<T> collection = getter.get();
    if (this.values == collection) {
      return;
    }
    this.values = collection;
    this.item.getChildren().clear();
    this.inspections.clear();
  }

  private String getDescription(Collection<T> values) {
    return name + "(" + values.size() + ")";
  }


  @Override
  public void update() {
    initCollection();
    item.setValue(getDescription(values));
    Set<T> seen = new HashSet<>();
    for (T value : values) {
      updateValue(value);
      seen.add(value);
    }
    removeUnseen(seen);

  }

  private void removeUnseen(Set<T> seen) {
    List<Map.Entry<T, Inspection>> entriesToRemove = new ArrayList<>();
    for (Map.Entry<T, Inspection> entry : inspections.entrySet()) {
      if (!seen.contains(entry.getKey())) {
        entriesToRemove.add(entry);
      }
    }
    entriesToRemove.forEach(this::remove);
  }


  private void remove(Map.Entry<T, Inspection> entry) {
    inspections.remove(entry.getKey());
    TreeItem<String> item = entry.getValue().getItem();
    item.getParent().getChildren().remove(item);
  }


  private void updateValue(T value) {
    if (inspections.containsKey(value)) {
      inspections.get(value).update();
      return;
    }
    createValue(value);
  }

  private void createValue(T value) {
    Class<?> subClass = value.getClass();
    Inspection inspection =
      Inspections.buildInspector(
        subClass,
        () -> value,
        subClass.getName()
        );
    inspections.put(value, inspection);
    inspection.update();
    item.getChildren().add(inspection.getItem());
  }

}

