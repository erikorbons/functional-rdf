package hurricane.rdf.core.iri;

import hurricane.rdf.core.iri.Iri.Authority;
import hurricane.rdf.core.iri.Iri.Path;
import java.util.Objects;
import java.util.Optional;

public interface RelativeIri {
  Optional<Authority> authority();
  Path path();
  Optional<String> query();
  Optional<String> fragment();

  IriReference asIriReference();

  static RelativeIri of(final String relativeIri) {
    try {
      return IriParser
          .parseRelativeIri(Objects.requireNonNull(relativeIri, "relativeIri cannot be null"));
    } catch (IriSyntaxException e) {
      throw new IllegalArgumentException("Invalid relative IRI", e);
    }
  }
}
