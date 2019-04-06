package hurricane.rdf.core.rdf;

import hurricane.rdf.core.iri.Iri;

public interface Predicate extends Comparable<Predicate> {
  Iri iri();

  @Override
  default int compareTo(final Predicate other) {
    return iri().compareTo(other.iri());
  }
}
