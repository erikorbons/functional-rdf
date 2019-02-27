package io.github.erikorbons.functionalrdf.core.iri;

import io.github.erikorbons.functionalrdf.core.iri.Iri.Authority;
import io.github.erikorbons.functionalrdf.core.iri.Iri.Path;
import java.util.Optional;

public interface RelativeIri {
  Optional<Authority> authority();
  Path path();
  Optional<String> query();
  Optional<String> fragment();

  IriReference asIriReference();
}
