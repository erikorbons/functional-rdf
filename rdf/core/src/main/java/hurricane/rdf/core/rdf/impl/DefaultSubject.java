package hurricane.rdf.core.rdf.impl;

import hurricane.rdf.core.iri.Iri;
import hurricane.rdf.core.rdf.BlankNode;
import hurricane.rdf.core.rdf.Subject;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class DefaultSubject implements Subject {

  public static final class IriSubject extends DefaultSubject {

    private final Iri iri;

    public IriSubject(final Iri iri) {
      this.iri = Objects.requireNonNull(iri, "iri cannot be null");
    }

    @Override
    public Optional<BlankNode> blankNode() {
      return Optional.empty();
    }

    @Override
    public Optional<Iri> iri() {
      return Optional.of(iri);
    }

    @Override
    public void ifBlankNode(Consumer<? super BlankNode> consumer) {
    }

    @Override
    public void ifIri(Consumer<? super Iri> iri) {
      Objects.requireNonNull(iri, "iri cannot be null").accept(this.iri);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      IriSubject that = (IriSubject) o;

      return iri.equals(that.iri);
    }

    @Override
    public int hashCode() {
      return iri.hashCode();
    }

    @Override
    public String toString() {
      return "<" + iri.asciiString() + ">";
    }
  }

  public static final class BlankNodeSubject extends DefaultSubject {

    private final BlankNode blankNode;

    public BlankNodeSubject(final BlankNode blankNode) {
      this.blankNode = Objects.requireNonNull(blankNode, "blankNode cannot be null");
    }

    @Override
    public Optional<BlankNode> blankNode() {
      return Optional.of(blankNode);
    }

    @Override
    public Optional<Iri> iri() {
      return Optional.empty();
    }

    @Override
    public void ifBlankNode(Consumer<? super BlankNode> consumer) {
      Objects.requireNonNull(consumer, "consumer cannot be null").accept(blankNode);
    }

    @Override
    public void ifIri(Consumer<? super Iri> iri) {
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      BlankNodeSubject that = (BlankNodeSubject) o;

      return blankNode.equals(that.blankNode);
    }

    @Override
    public int hashCode() {
      return blankNode.hashCode();
    }

    @Override
    public String toString() {
      return blankNode.toString();
    }
  }
}
