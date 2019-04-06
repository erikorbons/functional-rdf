package hurricane.rdf.core.rdf;

import hurricane.rdf.core.rdf.literals.XsdString;
import hurricane.rdf.core.iri.Iri;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public interface Literal extends Comparable<Literal> {

  String lexicalValue();
  Iri datatypeIri();
  Optional<Locale> languageTag();

  static XsdString of(final String value) {
    return new XsdString(value);
  }

  @Override
  default int compareTo(final Literal other) {
    Objects.requireNonNull(other, "other cannot be null");

    return lexicalValue().compareTo(other.lexicalValue());
  }
}
