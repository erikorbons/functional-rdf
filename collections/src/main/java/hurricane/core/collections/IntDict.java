package hurricane.core.collections;

import java.util.Optional;

public interface IntDict<V> extends BaseDict<Integer, V, IntDict<V>> {
  Optional<V> lookup(int key);
  boolean containsKey(int key);
  IntDict<V> put(int key, V value);
  IntDict<V> delete(int key);

  static <V> IntDict<V> empty() {
    return TreeDictInt.empty();
  }
}
