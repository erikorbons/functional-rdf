package hurricane.rdf.core.function;

@FunctionalInterface
public interface TripleConsumer<T, U, V> {
  void accept(T t, U u, V v);
}
