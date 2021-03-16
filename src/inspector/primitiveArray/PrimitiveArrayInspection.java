package inspector.primitiveArray;

import inspector.AbstractInspection;
import inspector.Inspection;
import javafx.scene.control.TreeItem;

import java.util.function.Supplier;

public abstract class PrimitiveArrayInspection extends AbstractInspection {

  public static PrimitiveArrayInspection build(Class<?> arrayClass, Supplier getter, String name) {
   Class<?> eltClass = arrayClass.getComponentType();
   return (int.class.equals(eltClass)) ? new IntArrayInspection(getter,name) :
          (long.class.equals(eltClass)) ? new LongArrayInspection(getter,name) :
          (float.class.equals(eltClass)) ? new FloatArrayInspection(getter,name) :
          (double.class.equals(eltClass)) ? new DoubleArrayInspection(getter,name) :
          (boolean.class.equals(eltClass)) ? new BooleanArrayInspection(getter,name) :
          (char.class.equals(eltClass)) ? new CharArrayInspection(getter, name) :
          (short.class.equals(eltClass)) ? new ShortArrayInspection(getter,name) :
          (byte.class.equals(eltClass)) ? new ByteArrayInspection(getter, name) :
          raiseError.get();
  }
  private static final Supplier<PrimitiveArrayInspection> raiseError =
    () -> { throw new IllegalArgumentException("not a primitive array"); };


  protected abstract boolean arrayHasChanged();
  protected abstract Inspection makeInspection(int i, String toString);
  protected abstract int getLength();
  protected abstract void resetArray();
  protected abstract boolean arrayIsNull();

  private final String name;
  Inspection[] inspectors;


  public PrimitiveArrayInspection(String name) {
    this.name = name;
    this.item = new TreeItem<>(name);
  }



  @Override
  public void update() {
    if (arrayHasChanged()) {
      initialize();
      return;
    }
    for (int i = 0; i < getLength(); i++) {
      inspectors[i].update();
    }
  }


  protected void initialize() {
    resetArray();
    if (arrayIsNull()) {
      item.setValue(name + ": null");
      return;
    }
    item.setValue(name + " (" + getLength() + ")");
    inspectors = new Inspection[getLength()];
    item.getChildren().clear();
    for (int i = 0; i < getLength(); i++) {
      inspectors[i] = makeInspection(i, Integer.toString(i));
      item.getChildren().add(inspectors[i].getItem());
    }
  }

}
