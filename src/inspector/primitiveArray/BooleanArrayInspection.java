package inspector.primitiveArray;

import inspector.Inspection;
import inspector.PrimitiveInspection;

import java.util.function.Supplier;

public class BooleanArrayInspection extends PrimitiveArrayInspection {

  private boolean[] array;
  private final Supplier<boolean[]> getter;

  public BooleanArrayInspection(Supplier<boolean[]> getter, String name) {
    super(name);
    this.getter = getter;
    initialize();
  }


  protected void resetArray() {
    array = getter.get();
  }

  @Override
  protected boolean arrayIsNull() {
    return array == null;
  }

  protected boolean arrayHasChanged() {
    return getter.get() != array;
  }

  @Override
  protected Inspection makeInspection(int i, String indexName) {
    return new PrimitiveInspection(() -> array[i], indexName);
  }

  @Override
  protected int getLength() {
    return array == null ? 0 : array.length;
  }

}
