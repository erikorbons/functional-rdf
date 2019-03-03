package io.github.erikorbons.functionalrdf.core.rdf.impl;

import io.github.erikorbons.functionalrdf.core.rdf.Predicate;
import io.github.erikorbons.functionalrdf.core.rdf.Quad;
import io.github.erikorbons.functionalrdf.core.rdf.RdfObject;
import io.github.erikorbons.functionalrdf.core.rdf.Subject;
import java.util.Objects;
import java.util.Optional;

public final class DefaultQuad implements Quad {

  private final Subject graphName;
  private final Subject subject;
  private final Predicate predicate;
  private final RdfObject object;

  public DefaultQuad(final Subject graphName, final Subject subject, final Predicate predicate,
      final RdfObject object) {
    this.graphName = graphName;
    this.subject = Objects.requireNonNull(subject, "subject cannot be null");
    this.predicate = Objects.requireNonNull(predicate, "predicate cannot be null");
    this.object = Objects.requireNonNull(object, "object cannot be null");
  }

  @Override
  public Optional<Subject> graphName() {
    return Optional.ofNullable(graphName);
  }

  @Override
  public Subject subject() {
    return subject;
  }

  @Override
  public Predicate predicate() {
    return predicate;
  }

  @Override
  public RdfObject object() {
    return object;
  }

  @Override
  public int hashCode() {
    return Quad.hashCode(this);
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null || !(obj instanceof Quad)) {
      return false;
    }

    return Quad.equals(this, (Quad) obj);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();

    builder.append(subject().toString()).append(" ");
    builder.append(predicate().toString()).append(" ");
    builder.append(object().toString()).append(" ");

    graphName().ifPresent(graph -> builder.append(graph.toString()).append(" "));

    builder.append(".");

    return builder.toString();
  }
}
