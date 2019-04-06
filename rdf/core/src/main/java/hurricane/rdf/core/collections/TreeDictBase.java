package hurricane.rdf.core.collections;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public abstract class TreeDictBase<V, T extends TreeDictBase<V, T>> {

  static abstract class Element<V, T extends Element<V, T>> {

    public abstract int getSize();

    float getWeight() {
      return (float) getSize() + 1.0f;
    }

    abstract T getLeft();

    abstract T getRight();

    public boolean isEmpty() {
      return false;
    }

    @SuppressWarnings("unchecked")
    T getSelf() {
      return (T) this;
    }

    abstract V getValue();

    abstract T updateChildren(T newLeft, T newRight);

    abstract T updateValue(V newValue);

    Optional<V> doGet(final ToIntFunction<T> comparator) {
      @SuppressWarnings("unchecked")
      T current = (T) this;

      while (!current.isEmpty()) {
        final int compare = comparator.applyAsInt(current);

        if (compare == 0) {
          return Optional.of(current.getValue());
        } else if (compare < 0) {
          current = current.getLeft();
        } else {
          current = current.getRight();
        }
      }

      return Optional.empty();
    }

    final T doInsert(final ToIntFunction<T> comparator, final Function<V, T> factory,
        final V value) {

      // Inserting into an empty node replaces the node with a leaf node:
      if (isEmpty()) {
        return factory.apply(value);
      }

      @SuppressWarnings("unchecked") final int compare = comparator.applyAsInt((T) this);

      if (compare == 0) {
        // Keys are equal, return this node or overwrite depending on whether the
        // value changed:
        return this.getValue().equals(value)
            ? getSelf()
            : updateValue(value);
      }

      final T newNode;

      if (compare < 0) {
        // Key is smaller than this.key, insert left:
        newNode = updateChildren(getLeft().doInsert(comparator, factory, value), getRight());
      } else {
        // Key is larger than this.key, insert right:
        newNode = updateChildren(getLeft(), getRight().doInsert(comparator, factory, value));
      }

      return newNode.applyRotations();
    }

    final T doDelete(final ToIntFunction<T> comparator) {
      if (isEmpty()) {
        return getSelf();
      }

      final int compare = comparator.applyAsInt(getSelf());
      final T newTree;

      if (compare < 0) {
        // Delete from the left subtree:
        final T newLeft = getLeft().doDelete(comparator);
        return newLeft == getLeft()
            ? getSelf()
            : updateChildren(getLeft().doDelete(comparator), getRight());
      } else if (compare > 0) {
        // Delete from the right subtree:
        final T newRight = getRight().doDelete(comparator);
        return newRight == getRight()
            ? getSelf()
            : updateChildren(getLeft(), getRight().doDelete(comparator));
      } else if (getLeft().isEmpty()) {
        // We need to delete this node, and this node has no left element:
        newTree = getRight();
      } else if (getRight().isEmpty()) {
        // We need to delete this node, and this node has no right element:
        newTree = getLeft();
      } else {
        // Neither child is empty, rotate the heavier side of the tree:
        if (getLeft().getWeight() > getRight().getWeight()) {
          final T rotated = rotateRight();
          newTree = rotated
              .updateChildren(rotated.getLeft(), rotated.getRight().doDelete(comparator));
        } else {
          final T rotated = rotateLeft();
          newTree = rotated.updateChildren(rotated.getLeft().doDelete(comparator),
              rotated.getRight());
        }
      }

      return newTree == this
          ? getSelf()
          : newTree.applyRotations();
    }

    final T applyRotations() {
      final float balance = getLeft().getWeight() / getWeight();

      if (getLeft().isEmpty() && getRight().isEmpty()) {
        return getSelf();
      }

      if (balance > 0.707011) {
        // The left subtree is too heavy: requires right rotation.
        if (getLeft().getLeft().getWeight() / getLeft().getWeight() > 0.414213) {
          return rotateRight();
        } else {
          return updateChildren(getLeft().rotateLeft(), getRight()).rotateRight();
        }
      } else if (balance < 0.292893) {
        // The right subtree is too heavy: required left rotation.
        if (getRight().getLeft().getWeight() / getRight().getWeight() < 0.585786) {
          return rotateLeft();
        } else {
          return updateChildren(getLeft(), getRight().rotateRight()).rotateLeft();
        }
      }

      return getSelf();
    }

    final T rotateLeft() {
      if (getLeft().isEmpty() && getRight().isEmpty()) {
        return getSelf();
      }

      /*
      return makeNode(
          right.getKey(),
          right.getValue(),
          makeNode(key, value, left, right.getLeft()),
          right.getRight()
      );
      */

      return getRight().updateChildren(updateChildren(getLeft(), getRight().getLeft()),
          getRight().getRight());
    }

    @SuppressWarnings("unchecked")
    final T rotateRight() {
      if (getLeft().isEmpty() && getRight().isEmpty()) {
        return getSelf();
      }

      /*
      return makeNode(
          left.getKey(),
          left.getValue(),
          left.getLeft(),
          makeNode(key, value, left.getRight(), right)
      );
      */

      return getLeft()
          .updateChildren(getLeft().getLeft(), updateChildren(getLeft().getRight(), getRight()));
    }
  }
}
