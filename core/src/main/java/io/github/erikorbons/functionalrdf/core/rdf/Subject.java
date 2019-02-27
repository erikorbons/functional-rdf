package io.github.erikorbons.functionalrdf.core.rdf;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import java.util.Optional;
import java.util.function.Consumer;

public interface Subject {

  Optional<BlankNode> blankNode();
  Optional<Iri> iri();

  default void ifBlankNode(final Consumer<? super BlankNode> consumer) {
    blankNode().ifPresent(consumer);
  }

  default void ifIri(final Consumer<? super Iri> iri) {
    iri().ifPresent(iri);
  }
}
