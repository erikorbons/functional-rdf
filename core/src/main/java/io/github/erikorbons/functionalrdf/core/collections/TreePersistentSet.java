package io.github.erikorbons.functionalrdf.core.collections;

import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class TreePersistentSet<V> extends TreeDictBase<V, TreePersistentSet<V>> implements
    PersistentSet<V> {

  private final Comparator<V> comparator;
  private final Element<V> root;

  private TreePersistentSet(final Comparator<V> comparator, final Element<V> root) {
    this.comparator = comparator;
    this.root = root;
  }

  static <V extends Comparable<V>> TreePersistentSet<V> empty() {
    return new TreePersistentSet<>(Comparator.naturalOrder(), Element.<V>empty());
  }

  static <V> TreePersistentSet<V> empty(final Comparator<V> comparator) {
    return new TreePersistentSet<>(
        Objects.requireNonNull(comparator, "comparator cannot be null"),
        Element.empty()
    );
  }

  @Override
  public boolean contains(final V value) {
    return root.doGet(makeComparator(value)).isPresent();
  }

  @Override
  public PersistentSet<V> add(final V value) {
    Objects.requireNonNull(value, "value cannot be null");

    return replaceRoot(root.doInsert(
        makeComparator(value),
        val -> new Leaf<>(val),
        value));
  }

  @Override
  public PersistentSet<V> delete(final V value) {
    if (value == null) {
      return this;
    }

    return replaceRoot(root.doDelete(makeComparator(value)));
  }

  @Override
  public Comparator<V> getComparator() {
    return comparator;
  }

  @Override
  public int getSize() {
    return root.getSize();
  }

  @Override
  public Stream<V> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  private PersistentSet<V> replaceRoot(final Element<V> newRoot) {
    return newRoot == root
        ? this
        : new TreePersistentSet<>(comparator, newRoot);
  }

  private ToIntFunction<Element<V>> makeComparator(final V value) {
    return node -> comparator.compare(value, node.getKey());
  }

  private ElementSpliterator<V> spliterator() {
    return new ElementSpliterator<>(root);
  }

  static abstract class Element<V> extends TreeDictBase.Element<V, Element<V>> {

    abstract V getKey();

    @Override
    V getValue() {
      return getKey();
    }

    public static <V> Element<V> empty() {
      return Empty.value();
    }
  }

  final static class Empty<V> extends Element<V> {
    private static final Empty<?> VALUE = new Empty<>();

    @SuppressWarnings("unchecked")
    public static <V> Empty<V> value() {
      return (Empty<V>) VALUE;
    }


    @Override
    V getKey() {
      throw new UnsupportedOperationException();
    }

    @Override
    Element<V> updateValue(final V newValue) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int getSize() {
      return 0;
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
    public boolean isEmpty() {
      return true;
    }

    @Override
    Element<V> updateChildren(final Element<V> newLeft, final Element<V> newRight) {
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

  final static class Leaf<V> extends Element<V> {
    private final V value;

    Leaf(final V value) {
      this.value = value;
    }

    @Override
    Element<V> updateValue(V newValue) {
      return new Leaf<>(newValue);
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
    public V getKey() {
      return value;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    Element<V> updateChildren(final Element<V> newLeft, final Element<V> newRight) {
      if (newLeft.isEmpty() && newRight.isEmpty()) {
        return this;
      }

      return new Node<>(value, newLeft, newRight);
    }

    @Override
    public String toString() {
      return "{\"value\":\"" + value + "\"}";
    }
  }

  final static class Node<V> extends Element<V> {
    private final V value;
    private final Element<V> left;
    private final Element<V> right;
    private final int size;

    Node(final V value, final Element<V> left, final Element<V> right) {
      this.value = value;
      this.left = left;
      this.right = right;
      this.size = left.getSize() + right.getSize() + 1;
    }

    @Override
    Element<V> updateValue(V newValue) {
      return new Node<>(newValue, left, right);
    }

    @Override
    public int getSize() {
      return size;
    }

    @Override
    Element<V> getLeft() {
      return left;
    }

    @Override
    Element<V> getRight() {
      return right;
    }

    @Override
    public V getKey() {
      return value;
    }

    @Override
    public V getValue() {
      return value;
    }

    @Override
    Element<V> updateChildren(
        Element<V> newLeft, Element<V> newRight) {
      return newLeft.isEmpty() && newRight.isEmpty()
          ? new Leaf<>(value)
          : new Node<>(value, newLeft, newRight);
    }

    @Override
    public String toString() {
      return "{\"value\":\"" + value + "\",\"weight\":" + getWeight() + ",\"left\":" + left + ",\"right\":" + right + "}";
    }
  }

  private static final class ElementSpliterator<V> implements Spliterator<V> {

    private final Stack<Element<V>> stack;

    private ElementSpliterator(final Element<V> root) {
      // Locate the left most element, while keeping track of the:
      this.stack = new Stack<>();
      expandLeftPath(root);
    }

    private void expandLeftPath(Element<V> current) {
      while (!current.isEmpty()) {
        this.stack.push(current);
        current = current.getLeft();
      }
    }

    @Override
    public boolean tryAdvance(final Consumer<? super V> consumer) {
      if (stack.empty()) {
        return false;
      }

      final Element<V> currentElement = stack.pop();

      // Offer the current element to the consumer:
      consumer.accept(currentElement.getValue());

      // If the element has a right child, move down that path:
      final Element<V> right = currentElement.getRight();
      if (!right.isEmpty()) {
        expandLeftPath(right);
      }

      // If there is no right branch, return and offer the value of the parent element at the
      // next invocation of advance:
      return true;
    }

    @Override
    public Spliterator<V> trySplit() {
      return null;
    }

    @Override
    public long estimateSize() {
      return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
      return IMMUTABLE | ORDERED | NONNULL;
    }
  }
}
