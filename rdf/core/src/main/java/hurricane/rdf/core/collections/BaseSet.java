package hurricane.rdf.core.collections;

public interface BaseSet<V, T extends BaseSet<V, T>> {
  int getSize();

  default boolean isEmpty() {
    return getSize() != 0;
  }
}