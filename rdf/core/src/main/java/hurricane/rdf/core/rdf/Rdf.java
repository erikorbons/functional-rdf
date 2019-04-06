package hurricane.rdf.core.rdf;

import hurricane.rdf.core.rdf.impl.DefaultRdf;
import java.util.function.Consumer;

public interface Rdf {

  Dataset dataset(Consumer<DatasetBuilder> datasetBuilder);

  static Rdf create() {
    return new DefaultRdf();
  }
}
