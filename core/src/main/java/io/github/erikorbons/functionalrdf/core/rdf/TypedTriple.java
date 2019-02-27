package io.github.erikorbons.functionalrdf.core.rdf;

public interface TypedTriple<S extends Subject, P extends Predicate, O extends RdfObject> extends
    Triple {

  S subject();
  P predicate();
  O object();
}
