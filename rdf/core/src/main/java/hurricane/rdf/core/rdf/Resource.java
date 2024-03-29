package hurricane.rdf.core.rdf;

import java.util.stream.Stream;

public interface Resource {
  Subject id();

  Stream<Triple> triples();
}
