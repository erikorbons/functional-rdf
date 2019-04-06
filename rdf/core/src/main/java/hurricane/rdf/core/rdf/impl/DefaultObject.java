package hurricane.rdf.core.rdf.impl;

import hurricane.rdf.core.rdf.formatters.NQuads;
import hurricane.rdf.core.iri.Iri;
import hurricane.rdf.core.rdf.BlankNode;
import hurricane.rdf.core.rdf.Literal;
import hurricane.rdf.core.rdf.RdfObject;
import java.util.Objects;
import java.util.Optional;

public abstract class DefaultObject implements RdfObject {

  @Override
  public Optional<Literal> literal() {
    return Optional.empty();
  }

  public static final class IriObject extends DefaultObject {
    private final Iri iri;

    public IriObject(final Iri iri) {
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
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      IriObject iriObject = (IriObject) o;

      return iri.equals(iriObject.iri);
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

  public static final class LiteralObject extends DefaultObject {
    private final Literal literal;

    public LiteralObject(final Literal literal) {
      this.literal = Objects.requireNonNull(literal, "literal cannot be null");
    }

    @Override
    public Optional<BlankNode> blankNode() {
      return Optional.empty();
    }

    @Override
    public Optional<Iri> iri() {
      return Optional.empty();
    }

    @Override
    public Optional<Literal> literal() {
      return Optional.of(literal);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      LiteralObject that = (LiteralObject) o;

      return literal != null ? literal.equals(that.literal) : that.literal == null;
    }

    @Override
    public int hashCode() {
      return literal != null ? literal.hashCode() : 0;
    }

    @Override
    public String toString() {
      final StringBuilder builder = new StringBuilder();

      builder.append("\"");
      NQuads.escapeLiteralValue(builder, literal.lexicalValue());
      builder.append("\"");

      if (literal.languageTag().isPresent()) {
        builder.append("@").append(literal.languageTag().get().toLanguageTag());
      } else {
        builder.append("^^<").append(literal.datatypeIri().asciiString()).append(">");
      }

      return builder.toString();
    }
  }

  public static final class BlankNodeObject extends DefaultObject {
    private final BlankNode blankNode;

    public BlankNodeObject(final BlankNode blankNode) {
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
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      BlankNodeObject that = (BlankNodeObject) o;

      return blankNode.equals(that.blankNode);
    }

    @Override
    public int hashCode() {
      return blankNode.hashCode();
    }
  }
}
