package hurricane.rdf.core.rdf.literals;

import hurricane.rdf.core.iri.Iri;
import java.util.Locale;
import java.util.Optional;

public final class XsdString extends AbstractTypedLiteral<String> {
  private static final Iri iri = Iri.of("http://www.w3.org/2001/XMLSchema#string");

  public XsdString(final String value) {
    super(value);
  }

  @Override
  public String lexicalValue() {
    return value();
  }

  @Override
  public Iri datatypeIri() {
    return iri;
  }

  @Override
  public Optional<Locale> languageTag() {
    return Optional.empty();
  }
}
