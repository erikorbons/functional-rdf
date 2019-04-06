package hurricane.rdf.core.rdf.literals;

import hurricane.rdf.core.iri.Iri;
import hurricane.rdf.core.rdf.Literal;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class RawLiteral implements Literal {

  private final String lexicalValue;
  private final Iri datatypeIri;

  public RawLiteral(final String lexicalValue, final Iri datatypeIri) {
    this.lexicalValue = Objects.requireNonNull(lexicalValue, "lexicalValue cannot be null");
    this.datatypeIri = Objects.requireNonNull(datatypeIri, "datatypeIri cannot be null");
  }

  @Override
  public String lexicalValue() {
    return lexicalValue;
  }

  @Override
  public Iri datatypeIri() {
    return datatypeIri;
  }

  @Override
  public Optional<Locale> languageTag() {
    return Optional.empty();
  }
}
