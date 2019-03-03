package io.github.erikorbons.functionalrdf.core.rdf;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import io.github.erikorbons.functionalrdf.core.iri.Iri.Path;
import io.github.erikorbons.functionalrdf.core.iri.RelativeIri;
import java.util.Objects;

public interface NamespaceIri {

  Iri iri();

  default Iri expand(final String suffix) {
    Objects.requireNonNull(suffix, "suffix cannot be null");

    final Iri iri = iri();

    // Add the suffix to the fragment if present:
    if (iri.fragment().isPresent()) {
      return iri.withFragment(iri.fragment().get() + suffix);
    }

    // Otherwise, add the suffix to the path:
    final Path path = iri.path();

    if (path.length() == 0) {
      // Replace the path:
      return iri.applyRelativeIri(RelativeIri.of(suffix));
    }

    // Replace the last element of the path:
    return iri.applyRelativeIri(RelativeIri.of(path.at(path.length() - 1) + suffix));
  }
}
