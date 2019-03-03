package io.github.erikorbons.functionalrdf.core.function;

@FunctionalInterface
public interface TripleConsumer<T, U, V> {
  void accept(T t, U u, V v);
}
