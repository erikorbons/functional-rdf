package hurricane.rdf.core.rdf;

import java.util.Comparator;
import java.util.Optional;

public interface Quad extends Triple {
  Optional<Subject> graphName();

  static int hashCode(final Quad quad) {
    return quad.graphName().hashCode()
        + 31 * quad.subject().hashCode()
        + 31 * quad.predicate().hashCode()
        + 31 * quad.object().hashCode();
  }

  static boolean equals(final Quad a, final Quad b) {
    return a.graphName().equals(b.graphName())
        && a.subject().equals(b.subject())
        && a.predicate().equals(b.predicate())
        && a.object().equals(b.object());
  }

  static int compare(final Quad a, final Quad b) {
    // Compare graphs first:
    final Optional<Subject> aGraph = a.graphName();
    final Optional<Subject> bGraph = b.graphName();

    if (aGraph.isEmpty() && bGraph.isPresent()) {
      // No graph is smaller than having a graph:
      return -1;
    } else if (aGraph.isPresent() && bGraph.isEmpty()) {
      // Having a graph is larger than not having a graph:
      return 1;
    } else if (aGraph.isPresent() && bGraph.isPresent()) {
      final int graphCompare = aGraph.get().compareTo(bGraph.get());
      if (graphCompare != 0) {
        return graphCompare;
      }
    }

    // Compare triple:
    return Triple.compare(a, b);
  }

  static Comparator<Quad> comparator() {
    return Quad::compare;
  }
}
