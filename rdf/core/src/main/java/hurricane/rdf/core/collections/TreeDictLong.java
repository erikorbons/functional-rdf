package hurricane.rdf.core.collections;

import java.util.Objects;
import java.util.Optional;
import java.util.function.ToIntFunction;

public final class TreeDictLong<V> extends TreeDictBase<V, TreeDictLong<V>> implements
    LongDict<V> {

  private final Element<V> root;

  private TreeDictLong(final Element<V> root) {
    this.root = root;
  }

  static <V> TreeDictLong<V> empty() {
    return new TreeDictLong<>(Empty.value());
  }

  @Override
  public Optional<V> lookup(final long key) {
    return root.doGet(makeComparator(key));
  }

  @Override
  public boolean containsKey(long key) {
    return lookup(key).isPresent();
  }

  @Override
  public LongDict<V> put(long key, V value) {
    Objects.requireNonNull(value, "value cannot be null");

    return replaceRoot(root.doInsert(
        makeComparator(key),
        val -> new Leaf<>(key, val),
        value
    ));
  }

  @Override
  public LongDict<V> delete(long key) {
    return replaceRoot(root.doDelete(makeComparator(key)));
  }

  @Override
  public int getSize() {
    return root.getSize();
  }

  @Override
  public boolean isEmpty() {
    return root.isEmpty();
  }

  private ToIntFunction<Element<V>> makeComparator(final long key) {
    return node -> Long.compare(key, node.getKey());
  }

  private LongDict<V> replaceRoot(final Element<V> newRoot) {
    return newRoot == root
        ? this
        : new TreeDictLong<>(newRoot);
  }

  static abstract class Element<V> extends TreeDictBase.Element<V, Element<V>> {

    public static <V> Element<V> empty() {
      return Empty.value();
    }

    abstract long getKey();
  }

  static class Empty<V> extends Element<V> {
    private final static Empty<?> VALUE = new Empty<>();

    @SuppressWarnings("unchecked")
    static <V> Element<V> value() {
      return (Empty<V>) VALUE;
    }

    @Override
    long getKey() {
      throw new UnsupportedOperationException();
    }

    @Override
    Element<V> getLeft() {
      return this;
    }

    @Override
    Element<V> getRight() {
      return this;
    }

    @Override
    V getValue() {
      throw new UnsupportedOperationException();
    }

    @Override
    Element<V> updateChildren(final Element<V> newLeft, final Element<V> newRight) {
      if (!newLeft.isEmpty() || !newRight.isEmpty()) {
        throw new IllegalArgumentException(
            "Empty node children can only be updated to empty values");
      }

      return this;
    }

    @Override
    Element<V> updateValue(V newValue) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getSize() {
      return 0;
    }
  }

  static class Leaf<V> extends Element<V> {
    private final long key;
    private final V value;

    Leaf(final long key, final V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public int getSize() {
      return 1;
    }

    @Override
    Element<V> getLeft() {
      return empty();
    }

    @Override
    Element<V> getRight() {
      return empty();
    }

    @Override
    V getValue() {
      return value;
    }

    @Override
    Element<V> updateChildren(final Element<V> newLeft, final Element<V> newRight) {
      if (newLeft.isEmpty() && newRight.isEmpty()) {
        return this;
      }

      return new Node<>(key, value, newLeft, newRight);
    }

    @Override
    Element<V> updateValue(V newValue) {
      return new Leaf<>(key, newValue);
    }

    @Override
    long getKey() {
      return key;
    }

  }

  static class Node<V> extends Element<V> {
    private final long key;
    private final V value;
    private final Element<V> left;
    private final Element<V> right;
    private final int size;

    Node(final long key, final V value, final Element<V> left, final Element<V> right) {
      this.key = key;
      this.value = value;
      this.left = left;
      this.right = right;
      this.size = left.getSize() + right.getSize() + 1;
    }

    @Override
    public int getSize() {
      return size;
    }

    @Override
    public Element<V> getLeft() {
      return left;
    }

    @Override
    public Element<V> getRight() {
      return right;
    }

    @Override
    V getValue() {
      return value;
    }

    @Override
    Element<V> updateChildren(final Element<V> newLeft, final Element<V> newRight) {
      return new Node<>(key, value, newLeft, newRight);
    }

    @Override
    Element<V> updateValue(final V newValue) {
      return new Node<>(key, newValue, left, right);
    }

    @Override
    long getKey() {
      return key;
    }
  }
}
