package hurricane.rdf.core.rdf;

import hurricane.rdf.core.function.QuadConsumer;
import hurricane.rdf.core.function.TripleConsumer;
import hurricane.rdf.core.iri.Iri;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * RDF builder operations.
 */
public interface RdfBuilder<T extends RdfBuilder<T>> {

  /**
   * Creates a new blank node with an identifier that is unique to this builder.
   *
   * @return
   */
  BlankNode blankNode();
  BlankNode blankNode(String identifier);

  Subject subject(Iri iri);
  Subject subject(BlankNode blankNode);

  RdfObject object(Iri iri);
  RdfObject object(BlankNode blankNode);
  RdfObject object(Literal literal);

  Predicate predicate(Iri iri);

  /**
   * Creates a new triple.
   *
   * @param subject
   * @param predicate
   * @param object
   * @return
   */
  T triple(Subject subject, Predicate predicate, RdfObject object);

  default T triple(final Iri subject, final Iri predicate, final Iri object) {
    return triple(subject(subject), predicate(predicate), object(object));
  }

  default T triple(final Iri subject, final Iri predicate, final BlankNode object) {
    return triple(subject(subject), predicate(predicate), object(object));
  }

  default T triple(final Iri subject, final Iri predicate, final Literal object) {
    return triple(subject(subject), predicate(predicate), object(object));
  }

  default T triple(final BlankNode subject, final Iri predicate, final Iri object) {
    return triple(subject(subject), predicate(predicate), object(object));
  }

  default T triple(final BlankNode subject, final Iri predicate, final BlankNode object) {
    return triple(subject(subject), predicate(predicate), object(object));
  }

  default T triple(final BlankNode subject, final Iri predicate, final Literal object) {
    return triple(subject(subject), predicate(predicate), object(object));
  }

  default T resource(final Subject subject,
      final Consumer<ResourceBuilder> resourceBuilder) {
    Objects.requireNonNull(resourceBuilder, "resourceBuilder cannot be null");

    resourceBuilder.accept(new ResourceBuilder(this, subject));

    return self();
  }

  default T resource(final Iri subject,
      final Consumer<ResourceBuilder> resourceBuilder) {
    return resource(subject(subject), resourceBuilder);
  }

  /**
   * Create a blank node subject
   * @param subject
   * @param predicate
   * @param resourceBuilder
   * @return
   */
  default T triple(final Subject subject, Predicate predicate,
      final Consumer<ResourceBuilder> resourceBuilder) {
    final BlankNode resourceSubject = blankNode();

    triple(subject, predicate, object(resourceSubject));

    resource(subject(resourceSubject), resourceBuilder);

    return self();
  }

  default T withNamespace(final Iri iri, final Consumer<NamespaceIri> factory) {
    Objects.requireNonNull(iri, "iri cannot be null");
    Objects.requireNonNull(factory, "factory cannot be null");

    factory.accept(() -> iri);

    return self();
  }

  default T withNamespace(final Iri iri1, final Iri iri2,
      final BiConsumer<NamespaceIri, NamespaceIri> factory) {
    Objects.requireNonNull(iri1, "iri1 cannot be null");
    Objects.requireNonNull(iri2, "iri2 cannot be null");
    Objects.requireNonNull(factory, "factory cannot be null");

    factory.accept(() -> iri1, () -> iri2);

    return self();
  }

  default T withNamespace(final Iri iri1, final Iri iri2, final Iri iri3,
      final TripleConsumer<NamespaceIri, NamespaceIri, NamespaceIri> factory) {
    Objects.requireNonNull(iri1, "iri1 cannot be null");
    Objects.requireNonNull(iri2, "iri2 cannot be null");
    Objects.requireNonNull(iri3, "iri3 cannot be null");
    Objects.requireNonNull(factory, "factory cannot be null");

    factory.accept(() -> iri1, () -> iri2, () -> iri3);

    return self();
  }

  default T withNamespace(final Iri iri1, final Iri iri2, final Iri iri3, final Iri iri4,
      final QuadConsumer<NamespaceIri, NamespaceIri, NamespaceIri, NamespaceIri> factory) {
    Objects.requireNonNull(iri1, "iri1 cannot be null");
    Objects.requireNonNull(iri2, "iri2 cannot be null");
    Objects.requireNonNull(iri3, "iri3 cannot be null");
    Objects.requireNonNull(iri4, "iri3 cannot be null");
    Objects.requireNonNull(factory, "factory cannot be null");

    factory.accept(() -> iri1, () -> iri2, () -> iri3, () -> iri4);

    return self();
  }

  default T withNamespace(final String iri, final Consumer<NamespaceIri> factory) {
    return withNamespace(Iri.of(iri), factory);
  }

  default T withNamespace(final String iri1, final String iri2,
      final BiConsumer<NamespaceIri, NamespaceIri> factory) {
    return withNamespace(Iri.of(iri2), Iri.of(iri2), factory);
  }

  default T withNamespace(final String iri1, final String iri2, final String iri3,
      final TripleConsumer<NamespaceIri, NamespaceIri, NamespaceIri> factory) {
    return withNamespace(Iri.of(iri2), Iri.of(iri2), Iri.of(iri3), factory);
  }

  default T withNamespace(final String iri1, final String iri2, final String iri3,
      final String iri4,
      final QuadConsumer<NamespaceIri, NamespaceIri, NamespaceIri, NamespaceIri> factory) {
    return withNamespace(Iri.of(iri2), Iri.of(iri2), Iri.of(iri3), Iri.of(iri4), factory);
  }

  @SuppressWarnings("unchecked")
  default T self() {
    return (T) this;
  }

  class ResourceBuilder {
    private final RdfBuilder builder;
    private final Subject subject;

    public ResourceBuilder(final RdfBuilder builder, final Subject subject) {
      this.builder = Objects.requireNonNull(builder, "builder cannot be null");
      this.subject = Objects.requireNonNull(subject, "subject cannot be null");
    }

    public Subject subject() {
      return subject;
    }

    public ResourceBuilder triple(final Predicate predicate, final RdfObject object) {
      builder.triple(subject(), predicate, object);
      return this;
    }

    public ResourceBuilder triple(final Iri predicate, final RdfObject object) {
      return triple(builder.predicate(predicate), object);
    }

    public ResourceBuilder triple(final Predicate predicate, final Iri object) {
      return triple(predicate, builder.object(object));
    }

    public ResourceBuilder triple(final Predicate predicate, final Literal object) {
      return triple(predicate, builder.object(object));
    }

    public ResourceBuilder triple(final Iri predicate, final Iri object) {
      return triple(builder.predicate(predicate), builder.object(object));
    }

    public ResourceBuilder triple(final Iri predicate, final Literal object) {
      return triple(builder.predicate(predicate), builder.object(object));
    }

    public ResourceBuilder triple(final Predicate predicate,
        final Consumer<ResourceBuilder> resourceBuilder) {
      builder.triple(subject(), predicate, resourceBuilder);
      return this;
    }
  }
}
