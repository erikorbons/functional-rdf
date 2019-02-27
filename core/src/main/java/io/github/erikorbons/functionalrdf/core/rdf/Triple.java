package io.github.erikorbons.functionalrdf.core.rdf;

public interface Triple {
  Subject subject();
  Predicate predicate();
  RdfObject object();
}
