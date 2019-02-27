package io.github.erikorbons.functionalrdf.core.rdf;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import java.util.Optional;
import java.util.function.Consumer;

public interface RdfObject {

  Optional<BlankNode> blankNode();
  Optional<Iri> iri();
  Optional<Literal> literal();

  default void ifBlankNode(final Consumer<? super BlankNode> consumer) {
    blankNode().ifPresent(consumer);
  }

  default void ifIri(final Consumer<? super Iri> consumer) {
    iri().ifPresent(consumer);
  }

  default void ifLiteral(final Consumer<? super Literal> consumer) {
    literal().ifPresent(consumer);
  }
}
