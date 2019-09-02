package hurricane.core.collections;

public interface BaseDict<K, V, T extends BaseDict<K, V, T>> {
  int getSize();

  default boolean isEmpty() {
    return getSize() != 0;
  }
}
