package hurricane.core.collections;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.function.ToIntFunction;

/**
 * A dictionary implemented as a weight balanced tree.
 *
 * <p>https://xlinux.nist.gov/dads/HTML/bbalphatree.html</p>
 *
 * @param <K>
 * @param <V>
 */
public final class TreeDict<K, V> extends TreeDictBase<V, TreeDict<K, V>> implements Dict<K, V> {

  private final Comparator<K> comparator;
  private final Element<K, V> root;

  private TreeDict(final Comparator<K> comparator, final Element<K, V> root) {
    this.comparator = comparator;
    this.root = root;
  }

  static <K extends Comparable<K>, V> TreeDict<K, V> empty() {
    return new TreeDict<>(Comparator.naturalOrder(), Element.<K, V>empty());
  }

  static <K, V> TreeDict<K, V> empty(final Comparator<K> comparator) {
    return new TreeDict<>(
        Objects.requireNonNull(comparator, "comparator cannot be null"),
        Element.empty()
    );
  }

  @Override
  public Optional<V> lookup(final K key) {
    return root.doGet(makeComparator(key));
  }

  @Override
  public boolean containsKey(K key) {
    return lookup(key).isPresent();
  }

  @Override
  public Dict<K, V> put(final K key, final V value) {
    Objects.requireNonNull(key, "key cannot be null");
    Objects.requireNonNull(value, "value cannot be null");

    return replaceRoot(root.doInsert(
        makeComparator(key),
        val -> new Leaf<>(key, val),
        value));
  }

  @Override
  public Dict<K, V> delete(final K key) {
    if (key == null) {
      return this;
    }

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

  @Override
  public Comparator<K> getComparator() {
    return comparator;
  }

  private Dict<K, V> replaceRoot(final Element<K, V> newRoot) {
    return newRoot == root
        ? this
        : new TreeDict<>(comparator, newRoot);
  }

  private ToIntFunction<Element<K, V>> makeComparator(final K key) {
    return node -> comparator.compare(key, node.getKey());
  }

  static abstract class Element<K, V> extends TreeDictBase.Element<V, Element<K, V>> {

    abstract K getKey();

    @Override
    V getValue() {
      throw new UnsupportedOperationException();
    }

    public static <K, V> Element<K, V> empty() {
      return Empty.value();
    }
  }

  final static class Empty<K, V> extends Element<K, V> {

    private static final Empty<?, ?> VALUE = new Empty<>();

    @SuppressWarnings("unchecked")
    public static <K, V> Empty<K, V> value() {
      return (Empty<K, V>) VALUE;
    }


    @Override
    K getKey() {
      throw new UnsupportedOperationException();
    }

    @Override
    Element<K, V> updateValue(final V newValue) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getSize() {
      return 0;
    }

    @Override
    Element<K, V> getLeft() {
      return this;
    }

    @Override
    Element<K, V> getRight() {
      return this;
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    Element<K, V> updateChildren(final Element<K, V> newLeft, final Element<K, V> newRight) {
      if (newLeft != value() || newRight != value()) {
        throw new IllegalArgumentException(
            "Empty node children can only be updated to empty nodes");
      }

      return value();
    }

    @Override
    public String toString() {
      return "null";
    }
  }

  final static class Leaf<K, V> extends Element<K, V> {
    private final K key;
    private final V value;

    Leaf(final K key, final V value) {
      this.key = key;
      this.value = value;
    }

    @Override
    Element<K, V> updateValue(V newValue) {
      return new Leaf<>(key, newValue);
    }

    @Override
    public int getSize() {
      return 1;
    }

    @Override
    Element<K, V> getLeft() {
      return empty();
    }

    @Override
    Element<K, V> getRight() {
      return empty();
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    Element<K, V> updateChildren(final Element<K, V> newLeft, final Element<K, V> newRight) {
      if (newLeft.isEmpty() && newRight.isEmpty()) {
        return this;
      }

      return new Node<>(key, value, newLeft, newRight);
    }

    @Override
    public String toString() {
      return "{\"key\":\"" + key + "\",\"value\":\"" + value + "\"}";
    }
  }

  final static class Node<K, V> extends Element<K, V> {
    private final K key;
    private final V value;
    private final Element<K, V> left;
    private final Element<K, V> right;
    private final int size;

    Node(final K key, final V value, final Element<K, V> left, final Element<K, V> right) {
      this.key = key;
      this.value = value;
      this.left = left;
      this.right = right;
      this.size = left.getSize() + right.getSize() + 1;
    }

    @Override
    Element<K, V> updateValue(V newValue) {
      return new Node<>(key, newValue, left, right);
    }

    @Override
    public int getSize() {
      return size;
    }

    @Override
    Element<K, V> getLeft() {
      return left;
    }

    @Override
    Element<K, V> getRight() {
      return right;
    }

    @Override
    public K getKey() {
      return key;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    Element<K, V> updateChildren(Element<K, V> newLeft, Element<K, V> newRight) {
      return newLeft.isEmpty() && newRight.isEmpty()
          ? new Leaf<>(key, value)
          : new Node<>(key, value, newLeft, newRight);
    }

    @Override
    public String toString() {
      return "{\"key\":\"" + key + "\",\"value\":\"" + value + "\",\"weight\":" + getWeight() + ",\"left\":" + left + ",\"right\":" + right + "}";
    }
  }
}
