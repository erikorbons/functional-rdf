package hurricane.rdf.core.rdf.impl;

import hurricane.rdf.core.rdf.BlankNode;
import java.util.Objects;

public final class DefaultBlankNode implements BlankNode {
  private final String identifier;

  public DefaultBlankNode(final String identifier) {
    Objects.requireNonNull(identifier, "identifier cannot be null");
    this.identifier = identifier;
  }

  public String identifier() {
    return identifier;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DefaultBlankNode that = (DefaultBlankNode) o;

    return identifier.equals(that.identifier);
  }

  @Override
  public int hashCode() {
    return identifier.hashCode();
  }

  @Override
  public int compareTo(final BlankNode blankNode) {
    Objects.requireNonNull(blankNode, "blankNode cannot be null");

    if (!(blankNode instanceof DefaultBlankNode)) {
      return -1;
    }

    return identifier.compareTo(((DefaultBlankNode) blankNode).identifier);
  }

  @Override
  public String toString() {
    return "_:" + identifier();
  }
}
