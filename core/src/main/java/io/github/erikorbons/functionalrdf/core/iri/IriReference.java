package io.github.erikorbons.functionalrdf.core.iri;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IriReference {
  Optional<Iri> iri();
  Optional<RelativeIri> relativeIri();

  default Iri iri(final Iri baseIri) {
    Objects.requireNonNull(baseIri, "baseIri cannot be null");

    return map(
        Function.identity(),
        baseIri::applyRelativeIri
    );
  }

  default void ifIri(final Consumer<? super Iri> consumer) {
    iri().ifPresent(consumer);
  }

  default void ifRelativeIri(final Consumer<? super RelativeIri> consumer) {
    relativeIri().ifPresent(consumer);
  }

  default <T> T map(final Function<? super Iri, T> mapIri,
      final Function<? super RelativeIri, T> mapRelativeIri) {
    final T result = iri()
        .map(mapIri)
        .orElseGet(() -> relativeIri().map(mapRelativeIri).orElse(null));

    if (result == null) {
      throw new IllegalStateException("Either IRI or relative IRI must be present");
    }

    return result;
  }
}
