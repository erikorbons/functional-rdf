package io.github.erikorbons.functionalrdf.core.rdf;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import io.github.erikorbons.functionalrdf.core.rdf.literals.XsdString;
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
