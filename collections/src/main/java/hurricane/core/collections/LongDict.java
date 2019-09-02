package hurricane.core.collections;

import java.util.Optional;

public interface LongDict<V> extends BaseDict<Long, V, LongDict<V>> {
  Optional<V> lookup(long key);
  boolean containsKey(long key);
  LongDict<V> put(long key, V value);
  LongDict<V> delete(long key);

  static <V> LongDict<V> empty() {
    return TreeDictLong.empty();
  }
}
