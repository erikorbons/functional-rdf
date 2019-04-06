package hurricane.rdf.core.function;

@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {
  void accept(T t, U u, V v, W w);
}
