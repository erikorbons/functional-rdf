package hurricane.rdf.core.rdf;

import java.util.stream.Stream;

public interface Dataset {

  Stream<Quad> quads();

  Graph defaultGraph();

  Graph namedGraph(Subject graphName);
}
