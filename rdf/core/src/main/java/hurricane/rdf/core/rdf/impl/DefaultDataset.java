package hurricane.rdf.core.rdf.impl;

import hurricane.rdf.core.collections.PersistentSet;
import hurricane.rdf.core.rdf.Dataset;
import hurricane.rdf.core.rdf.Graph;
import hurricane.rdf.core.rdf.Quad;
import hurricane.rdf.core.rdf.Subject;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;

public final class DefaultDataset implements Dataset {
  private final PersistentSet<Quad> quads;

  private DefaultDataset(final PersistentSet<Quad> quads) {
    this.quads = Objects.requireNonNull(quads, "quads cannot be null");
  }

  public static DefaultDataset empty() {
    return new DefaultDataset(PersistentSet.empty(Quad.comparator()));
  }

  public static DefaultDataset of(final PersistentSet<Quad> quads) {
    return new DefaultDataset(quads);
  }

  public DefaultDataset add(final Quad quad) {
    return replaceQuads(quads.add(Objects.requireNonNull(quad, "quad cannot be null")));
  }

  private DefaultDataset replaceQuads(final PersistentSet<Quad> newQuads) {
    return quads == newQuads ? this : new DefaultDataset(newQuads);
  }

  @Override
  public Stream<Quad> quads() {
    return quads.stream();
  }

  @Override
  public Graph defaultGraph() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Graph namedGraph(Subject graphName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    final StringJoiner joiner = new StringJoiner("\n");

    quads.stream().forEach(quad -> joiner.add(quad.toString()));

    return joiner.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    DefaultDataset that = (DefaultDataset) o;

    return quads != null ? quads.equals(that.quads) : that.quads == null;
  }

  @Override
  public int hashCode() {
    return quads != null ? quads.hashCode() : 0;
  }
}
