package hurricane.rdf.core.rdf.impl;

import hurricane.rdf.core.rdf.Dataset;
import hurricane.rdf.core.rdf.DatasetBuilder;
import hurricane.rdf.core.rdf.Rdf;
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
