package io.github.erikorbons.functionalrdf.core.collections;

import java.util.Comparator;
import java.util.stream.Stream;

public interface PersistentSet<V> extends BaseSet<V, PersistentSet<V>> {
  boolean contains(V value);
  PersistentSet<V> add(V value);
  PersistentSet<V> delete(V value);

  Comparator<V> getComparator();

  Stream<V> stream();

  static <V extends Comparable<V>> PersistentSet<V> empty() {
    return TreePersistentSet.empty();
  }

  static <V> PersistentSet<V> empty(final Comparator<V> comparator) {
    return TreePersistentSet.empty(comparator);
  }
}
