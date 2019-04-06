package hurricane.rdf.core.rdf;

import java.util.stream.Stream;

public interface Graph {

  Stream<Triple> triples();
}
