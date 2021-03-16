package inspector;

import inspector.primitiveArray.PrimitiveArrayInspection;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Supplier;

public class Inspections {

  static Set<String> authorizedPackages = new HashSet<>();

  public static void addAuthorizedPackage(String packageName) {
    authorizedPackages.add(packageName);
  }


  static Inspection buildInspector(Class<?> subClass, Supplier getter, String name) {
    return (isPrimitive(subClass)) ?
             new PrimitiveInspection(getter, name) :
           (subClass.isAnnotationPresent(InspectWithToString.class)) ?
             new FormattedInspection(getter, name) :
           (Collection.class.isAssignableFrom(subClass)) ?
             new CollectionInspection(makeCollectionGetter(getter), name) :
           (isPrimitiveArray(subClass)) ?
             PrimitiveArrayInspection.build(subClass, getter, name) :
           (subClass.isArray()) ?
             new ArrayInspection(getter, name) :
           (isPackageAuthorized(subClass.getPackage())) ?
             new FieldInspection(getter, name) :
             new ConstantInspection(name, "hidden");
  }

  private static boolean isPrimitiveArray(Class<?> subClass) {
    return subClass.isArray() && subClass.getComponentType().isPrimitive();
  }


  static boolean isPackageAuthorized(Package pack) {
    return pack == null || authorizedPackages.contains(pack.getName());
  }


  private static final List<Class> primitiveClasses =
    List.of(Double.class, Integer.class, Float.class, Long.class, Boolean.class, Character.class);

  static boolean isPrimitive(Class<?> subClass) {
    return subClass.isPrimitive()
      || primitiveClasses.contains(subClass);
  }


  static <O> Supplier fieldSupplier(O object, Field d) {
    return () -> {
      try {
        return d.get(object);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
        return null;
      }
    };
  }


  private static Supplier<Collection> makeCollectionGetter(Supplier<Collection> getter) {
    return () -> {
      Collection c = getter.get();
      return (c == null) ? Collections.EMPTY_LIST : c;
    };
  }

}
