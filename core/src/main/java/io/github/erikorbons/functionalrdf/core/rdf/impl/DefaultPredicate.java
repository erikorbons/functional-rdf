package io.github.erikorbons.functionalrdf.core.rdf.impl;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import io.github.erikorbons.functionalrdf.core.rdf.Predicate;
import java.util.Objects;

public final class DefaultPredicate implements Predicate {
  private final Iri iri;

  public DefaultPredicate(final Iri iri) {
    this.iri = Objects.requireNonNull(iri, "iri cannot be null");
  }


  @Override
  public Iri iri() {
    return iri;
  }

  @Override
  public String toString() {
    return "<" + iri.asciiString() + ">";
  }
}
