package hurricane.rdf.core.rdf;

public interface TypedTriple<S extends Subject, P extends Predicate, O extends RdfObject> extends
    Triple {

  S subject();
  P predicate();
  O object();
}
