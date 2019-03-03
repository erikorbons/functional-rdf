package io.github.erikorbons.functionalrdf.core.rdf.impl;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import io.github.erikorbons.functionalrdf.core.rdf.BlankNode;
import io.github.erikorbons.functionalrdf.core.rdf.Dataset;
import io.github.erikorbons.functionalrdf.core.rdf.DatasetBuilder;
import io.github.erikorbons.functionalrdf.core.rdf.Literal;
import io.github.erikorbons.functionalrdf.core.rdf.Predicate;
import io.github.erikorbons.functionalrdf.core.rdf.Quad;
import io.github.erikorbons.functionalrdf.core.rdf.RdfObject;
import io.github.erikorbons.functionalrdf.core.rdf.Subject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DefaultDatasetBuilder implements DatasetBuilder {

  private final Map<String, BlankNode> blankNodeMap = new HashMap<>();
  private final Map<Iri, Subject> iriSubjectMap = new HashMap<>();
  private final Map<BlankNode, Subject> blankNodeSubjectMap = new HashMap<>();
  private final Map<Iri, RdfObject> iriObjectMap = new HashMap<>();
  private final Map<BlankNode, RdfObject> blankNodeObjectMap = new HashMap<>();
  private final Map<Literal, RdfObject> literalObjectMap = new HashMap<>();
  private final Map<Iri, Predicate> predicateMap = new HashMap<>();
  private DefaultDataset dataset = DefaultDataset.empty();

  @Override
  public DatasetBuilder quad(final Subject graphName, final Subject subject,
      final Predicate predicate, final RdfObject object) {
    final Quad quad = new DefaultQuad(graphName, subject, predicate, object);

    dataset = dataset.add(quad);

    return this;
  }


  @Override
  public BlankNode blankNode() {
    return blankNode(UUID.randomUUID().toString());
  }

  @Override
  public BlankNode blankNode(final String identifier) {
    Objects.requireNonNull(identifier, "identifier cannot be null");

    return blankNodeMap.computeIfAbsent(identifier, DefaultBlankNode::new);
  }

  @Override
  public Subject subject(final Iri iri) {
    Objects.requireNonNull(iri, "iri cannot be null");

    return iriSubjectMap.computeIfAbsent(iri, DefaultSubject.IriSubject::new);
  }

  @Override
  public Subject subject(final BlankNode blankNode) {
    Objects.requireNonNull(blankNode, "blankNode cannot be null");

    return blankNodeSubjectMap.computeIfAbsent(blankNode, DefaultSubject.BlankNodeSubject::new);
  }

  @Override
  public RdfObject object(final Iri iri) {
    Objects.requireNonNull(iri, "iri cannot be null");

    return iriObjectMap.computeIfAbsent(iri, DefaultObject.IriObject::new);
  }

  @Override
  public RdfObject object(final BlankNode blankNode) {
    Objects.requireNonNull(blankNode, "blankNode cannot be null");

    return blankNodeObjectMap.computeIfAbsent(blankNode, DefaultObject.BlankNodeObject::new);
  }

  @Override
  public RdfObject object(final Literal literal) {
    Objects.requireNonNull(literal, "literal cannot be null");

    return literalObjectMap.computeIfAbsent(literal, DefaultObject.LiteralObject::new);
  }

  @Override
  public Predicate predicate(final Iri iri) {
    return predicateMap.computeIfAbsent(iri, DefaultPredicate::new);
  }

  @Override
  public DatasetBuilder triple(final Subject subject, final Predicate predicate,
      final RdfObject object) {
    return quad(null, subject, predicate, object);
  }

  public Dataset build() {
    return dataset;
  }
}
