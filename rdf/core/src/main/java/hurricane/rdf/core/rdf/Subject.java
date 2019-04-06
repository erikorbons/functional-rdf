package hurricane.rdf.core.rdf;

import hurricane.rdf.core.iri.Iri;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public interface Subject extends Comparable<Subject> {

  Optional<BlankNode> blankNode();
  Optional<Iri> iri();

  default void ifBlankNode(final Consumer<? super BlankNode> consumer) {
    blankNode().ifPresent(consumer);
  }

  default void ifIri(final Consumer<? super Iri> iri) {
    iri().ifPresent(iri);
  }

  @Override
  default int compareTo(final Subject other) {
    Objects.requireNonNull(other, "other cannot be null");

    return
        blankNode().map(a -> other.blankNode().map(a::compareTo).orElse(-1))
            .orElseGet(() -> iri().map(a -> other.iri().map(a::compareTo).orElse(1)).orElse(1));
  }
}
