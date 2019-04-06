package hurricane.rdf.core.rdf.impl;

import hurricane.rdf.core.iri.Iri;
import hurricane.rdf.core.rdf.Predicate;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DefaultPredicate that = (DefaultPredicate) o;

    return iri != null ? iri.equals(that.iri) : that.iri == null;
  }

  @Override
  public int hashCode() {
    return iri != null ? iri.hashCode() : 0;
  }
}
