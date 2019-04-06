package hurricane.rdf.core.rdf.impl;

import hurricane.rdf.core.iri.Iri;
import hurricane.rdf.core.rdf.BlankNode;
import hurricane.rdf.core.rdf.Dataset;
import hurricane.rdf.core.rdf.DatasetBuilder;
import hurricane.rdf.core.rdf.Literal;
import hurricane.rdf.core.rdf.Predicate;
import hurricane.rdf.core.rdf.Quad;
import hurricane.rdf.core.rdf.RdfObject;
import hurricane.rdf.core.rdf.Subject;
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
  public DatasetBuilder quad(final Quad quad) {
    dataset = dataset.add(Objects.requireNonNull(quad, "quad cannot be null"));
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
