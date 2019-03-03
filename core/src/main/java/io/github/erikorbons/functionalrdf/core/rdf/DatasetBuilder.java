package io.github.erikorbons.functionalrdf.core.rdf;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import java.util.Objects;
import java.util.function.Consumer;

public interface DatasetBuilder extends RdfBuilder<DatasetBuilder> {

  DatasetBuilder quad(Subject graphName, Subject subject, Predicate predicate, RdfObject object);
  Dataset build();

  default DatasetBuilder namedGraph(final Subject graphName, final Consumer<GraphBuilder> graphBuilder) {
    graphBuilder.accept(new GraphBuilder(this, graphName));
    return this;
  }

  class GraphBuilder implements RdfBuilder<GraphBuilder> {
    private final DatasetBuilder datasetBuilder;
    private final Subject graphName;

    public GraphBuilder(final DatasetBuilder datasetBuilder, final Subject graphName) {
      this.datasetBuilder = Objects.requireNonNull(datasetBuilder, "datasetBuilder cannot be null");
      this.graphName = Objects.requireNonNull(graphName, "graphName cannot be null");
    }

    @Override
    public BlankNode blankNode() {
      return datasetBuilder.blankNode();
    }

    @Override
    public Subject subject(Iri iri) {
      return datasetBuilder.subject(iri);
    }

    @Override
    public Subject subject(BlankNode blankNode) {
      return datasetBuilder.subject(blankNode);
    }

    @Override
    public RdfObject object(Iri iri) {
      return datasetBuilder.object(iri);
    }

    @Override
    public RdfObject object(BlankNode blankNode) {
      return datasetBuilder.object(blankNode);
    }

    @Override
    public RdfObject object(Literal literal) {
      return datasetBuilder.object(literal);
    }

    @Override
    public BlankNode blankNode(String identifier) {
      return datasetBuilder.blankNode(identifier);
    }

    @Override
    public Predicate predicate(Iri iri) {
      return datasetBuilder.predicate(iri);
    }

    @Override
    public GraphBuilder triple(final Subject subject, final Predicate predicate, final RdfObject object) {
      datasetBuilder.quad(graphName, subject, predicate, object);
      return self();
    }
  }
}
