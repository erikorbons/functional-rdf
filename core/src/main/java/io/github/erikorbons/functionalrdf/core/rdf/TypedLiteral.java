package io.github.erikorbons.functionalrdf.core.rdf;

public interface TypedLiteral<T> extends Literal {
  T value();
}
