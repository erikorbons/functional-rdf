package hurricane.rdf.core.rdf;

import hurricane.rdf.core.iri.Iri;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface RdfObject extends Comparable<RdfObject> {

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

  default <R> R map(final Function<Iri, R> iriVisitor, final Function<Literal, R> literalVisitor,
      final Function<BlankNode, R> blankNodeVisitor) {
    final Optional<Iri> iri = iri();
    if (iri.isPresent()) {
      return iriVisitor.apply(iri.get());
    }

    final Optional<Literal> literal = literal();
    if (literal.isPresent()) {
      return literalVisitor.apply(literal.get());
    }

    final Optional<BlankNode> blankNode = blankNode();
    if (blankNode.isPresent()) {
      return blankNodeVisitor.apply(blankNode.get());
    }

    throw new IllegalStateException("Either iri, literal or blank node must be present");
  }

  @Override
  default int compareTo(final RdfObject other) {
    Objects.requireNonNull(other, "other cannot be null");

    return map(
        iri -> other.map(
            // Compare IRI's:
            iri::compareTo,
            // IRI's come before literals:
            otherLiteral -> -1,
            // IRI's come after blank nodes:
            otherBlankNode -> 1
        ),
        literal -> other.map(
            // Literals come after IRI's:
            otherIri -> 1,
            // Compare literals:
            literal::compareTo,
            // Literals come after blank nodes:
            otherBlankNode -> 1
        ),
        blankNode -> other.map(
            // Blank nodes come before IRI's:
            otherIri -> -1,
            // Blank nodes come before literals:
            otherLiteral -> 0,
            // Compare blank nodes:
            blankNode::compareTo
        )
    );
  }
}
