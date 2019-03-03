package io.github.erikorbons.functionalrdf.core.rdf;

import io.github.erikorbons.functionalrdf.core.iri.Iri;

public interface Predicate extends Comparable<Predicate> {
  Iri iri();

  @Override
  default int compareTo(final Predicate other) {
    return iri().compareTo(other.iri());
  }
}
