package hurricane.core.collections;

import java.util.Comparator;
import java.util.Optional;

public interface Dict<K, V> extends BaseDict<K, V, Dict<K, V>> {
  Optional<V> lookup(K key);
  boolean containsKey(K key);
  Dict<K, V> put(K key, V value);
  Dict<K, V> delete(K key);

  Comparator<K> getComparator();

  static <K extends Comparable<K>, V> Dict<K, V> empty() {
    return TreeDict.empty();
  }

  static <K, V> Dict<K, V> empty(final Comparator<K> comparator) {
    return TreeDict.empty(comparator);
  }

  /*
  <K, V, R> Dict<K, R> mapValues(Function<V, R> mapper);

  default <K, V, R extends Comparable<R>> Dict<R, V> mapKeys(Function<K, R> mapper) {
    return mapKeys(mapper, Comparator.naturalOrder());
  }

  <K, V, R> Dict<R, V> mapKeys(Function<K, R> mapper, Comparator<R> comparator);
  */
}
