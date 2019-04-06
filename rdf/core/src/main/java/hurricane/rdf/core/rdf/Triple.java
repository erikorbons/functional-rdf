package hurricane.rdf.core.rdf;

public interface Triple {
  Subject subject();
  Predicate predicate();
  RdfObject object();

  static int compare(final Triple a, final Triple b) {
    final int subjectCompare = a.subject().compareTo(b.subject());
    if (subjectCompare != 0) {
      return subjectCompare;
    }

    final int predicateCompare = a.predicate().compareTo(b.predicate());
    if (predicateCompare != 0) {
      return predicateCompare;
    }

    return a.object().compareTo(b.object());
  }
}
