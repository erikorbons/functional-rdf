package io.github.erikorbons.functionalrdf.core.rdf;

import java.util.stream.Stream;

public interface Graph {

  Stream<Triple> triples();
}
