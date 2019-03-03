package io.github.erikorbons.functionalrdf.core.rdf.impl;

import io.github.erikorbons.functionalrdf.core.rdf.Dataset;
import io.github.erikorbons.functionalrdf.core.rdf.DatasetBuilder;
import io.github.erikorbons.functionalrdf.core.rdf.Rdf;
import java.util.Objects;
import java.util.function.Consumer;

public class DefaultRdf implements Rdf {

  @Override
  public Dataset dataset(final Consumer<DatasetBuilder> datasetBuilder) {
    Objects.requireNonNull(datasetBuilder, "datasetBuilder cannot be null");

    final DefaultDatasetBuilder builder = new DefaultDatasetBuilder();

    datasetBuilder.accept(builder);

    return builder.build();
  }
}
