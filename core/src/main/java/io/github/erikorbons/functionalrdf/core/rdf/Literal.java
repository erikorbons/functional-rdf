package io.github.erikorbons.functionalrdf.core.rdf;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import java.util.Locale;
import java.util.Optional;

public interface Literal {

  String lexicalValue();
  Iri datatypeIri();
  Optional<Locale> languageTag();
}
