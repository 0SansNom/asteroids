package inspector;

import javafx.beans.Observable;
import javafx.scene.control.TreeItem;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

public class ObjectInspection extends AbstractInspection {

  private final String name;
  private final List<Inspection> observableFields = new ArrayList<>();
  private final Object inspected;

  public ObjectInspection(Object object) {
    this.name = object.getClass().getName();
    this.inspected = object;
    initialize();
  }

  public ObjectInspection(String name, Object object) {
    this.inspected = object;
    this.name = name;
    initialize();
  }

  private void initialize() {
    item = new TreeItem<>(name);
    item.setExpanded(false);
    item.getChildren().add(new TreeItem<String>("..."));
    item.expandedProperty().addListener(this::expand);
  }


  @Override
  public void update() {
    for (Inspection inspect : observableFields) {
      inspect.update();
    }
  }

  private boolean isComplete = false;
  private void expand(Observable observable) {
    if (isComplete) {
      return;
    }
    isComplete = true;
    item.getChildren().remove(0);
    initializeObservableFields();
    for (Inspection inspection : observableFields) {
      item.getChildren().add(inspection.getItem());
    }
    item.setExpanded(true);
  }


  private void initializeObservableFields() {
    List<Field> declaredFields =
      Arrays.asList(inspected.getClass().getDeclaredFields());
    declaredFields.sort(Comparator.comparing(Field::getName));
    for (Field field : declaredFields) {
      if (isObservable(field)) {
        initializeField(field);
      }
    }
  }

  private boolean isObservable(Field field) {
    if (field.isAnnotationPresent(Hidden.class)) {
      return false;
    }
    field.setAccessible(true);
    return !Modifier.isStatic(field.getModifiers());
  }


  private void initializeField(Field field) {
    Class<?> subClass = field.getType();
    Supplier getter = Inspections.fieldSupplier(inspected, field);
    String name = field.getName();
    Inspection fieldInspection = Inspections.buildInspector(subClass, getter, name);
    observableFields.add(fieldInspection);
  }


}
