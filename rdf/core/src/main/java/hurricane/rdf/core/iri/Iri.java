package hurricane.rdf.core.iri;

import hurricane.rdf.core.encoding.CharacterUtils;
import hurricane.rdf.core.encoding.PercentEncoding;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Stream;

/**
 * A full absolute IRI. https://tools.ietf.org/html/rfc3987
 */
public interface Iri extends Comparable<Iri> {
  String scheme();
  Optional<Authority> authority();
  Path path();
  Optional<String> query();
  Optional<String> fragment();

  Iri normalize();
  boolean isNormalized();

  IriReference asIriReference();

  Iri applyRelativeIri(RelativeIri relativeIri);

  Iri withFragment(String fragment);

  static Iri createNormalized(final String iri) {
    try {
      return IriParser.parseIri(iri);
    } catch (IriSyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  default Iri applyReference(final IriReference reference) {
    Objects.requireNonNull(reference, "reference cannot be null");

    return reference.map(
        Function.identity(),
        this::applyRelativeIri
    );
  }

  default URI toUri() {
    return URI.create(asciiString());
  }

  default Optional<String> escapedQuery() {
    return query()
        .map(s -> PercentEncoding.encode(
            s,
            ((IntPredicate) CharacterUtils::isIPChar)
                .or(CharacterUtils::isIPrivate)
                .or(cp -> cp == '/' || cp == '?')
                .negate()
        ));
  }

  default Optional<String> escapedFragment() {
    return fragment()
        .map(s -> PercentEncoding.encode(
            s,
            ((IntPredicate) CharacterUtils::isIPChar)
                .or(cp -> cp == '/' || cp == '?')
                .negate()
        ));
  }

  default String escapedString() {
    final StringBuilder builder = new StringBuilder();

    builder.append(scheme()).append(":");
    authority().map(Authority::escapedString)
        .ifPresent(authority -> builder.append("//").append(authority));
    builder.append(path().escapedString());
    escapedQuery().ifPresent(query -> builder.append("?").append(query));
    escapedFragment().ifPresent(fragment -> builder.append("#").append(fragment));

    return builder.toString();
  }

  default Optional<String> asciiQuery() {
    return query()
        .map(s -> PercentEncoding.encode(
            s,
            ((IntPredicate) CharacterUtils::isPChar)
                .or(cp -> cp == '/' || cp == '?')
                .negate()
        ));
  }

  default Optional<String> asciiFragment() {
    return fragment()
        .map(s -> PercentEncoding.encode(
            s,
            ((IntPredicate) CharacterUtils::isPChar)
                .or(cp -> cp == '/' || cp == '?')
                .negate()
        ));
  }

  default String asciiString() {
    final StringBuilder builder = new StringBuilder();

    builder.append(scheme()).append(":");
    authority().map(Authority::asciiString)
        .ifPresent(authority -> builder.append("//").append(authority));
    builder.append(path().asciiString());
    asciiQuery().ifPresent(query -> builder.append("?").append(query));
    asciiFragment().ifPresent(fragment -> builder.append("#").append(fragment));

    return builder.toString();

  }

  @Override
  default int compareTo(final Iri other) {
    Objects.requireNonNull(other, "other cannot be null");

    if (other == this) {
      return 0;
    }

    final Iri a = this.normalize();
    final Iri b = other.normalize();

    final int schemeCompare = a.scheme().compareTo(b.scheme());
    if (schemeCompare != 0) {
      return schemeCompare;
    }

    final int authorityCompare = a.authority()
        .flatMap(aAuth -> b.authority().map(aAuth::compareTo)).orElse(0);
    if (authorityCompare != 0) {
      return authorityCompare;
    }

    final int pathCompare = a.path().compareTo(b.path());
    if (pathCompare != 0) {
      return pathCompare;
    }

    final int queryCompare = a.query()
        .flatMap(aQuery -> b.query().map(aQuery::compareTo)).orElse(0);
    if (queryCompare != 0) {
      return queryCompare;
    }

    return a.fragment()
        .flatMap(aFragment -> b.fragment().map(aFragment::compareTo)).orElse(0);
  }

  static Iri of(final String iri) {
    try {
      return IriParser.parseIri(iri);
    } catch (IriSyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }

  interface Authority extends Comparable<Authority> {
    Optional<String> userinfo();
    String host();
    OptionalInt port();

    default Optional<String> escapedUserinfo() {
      return userinfo()
          .map(s -> PercentEncoding.encode(
              s,
              ((IntPredicate) CharacterUtils::isIUnreserved)
                  .or(CharacterUtils::isSubDelimiter)
                  .or(cp -> cp == ':')
                  .negate()
          ));
    }

    default String escapedHost() {
      final String host = host();

      if (host.startsWith("[")) {
        // IP6 addresses are returned as-is:
        return host;
      }

      return PercentEncoding.encode(
          host,
          ((IntPredicate) CharacterUtils::isIUnreserved)
              .or(CharacterUtils::isSubDelimiter)
              .negate()
      );
    }

    default Optional<String> asciiUserinfo() {
      return userinfo()
          .map(s -> PercentEncoding.encode(
              s,
              ((IntPredicate) CharacterUtils::isUnreserved)
                  .or(CharacterUtils::isSubDelimiter)
                  .or(cp -> cp == ':')
                  .negate()
          ));
    }

    default String asciiHost() {
      final String host = host();

      if (host.startsWith("[")) {
        // IP6 addresses are returned as-is:
        return host;
      }

      return PercentEncoding.encode(
          host,
          ((IntPredicate) CharacterUtils::isUnreserved)
              .or(CharacterUtils::isSubDelimiter)
              .negate()
      );
    }

    default String escapedString() {
      final StringBuilder builder = new StringBuilder();

      escapedUserinfo().ifPresent(userInfo -> builder.append(userInfo).append("@"));
      builder.append(escapedHost());
      port().ifPresent(port -> builder.append(":").append(Integer.toString(port)));

      return builder.toString();
    }

    default String asciiString() {
      final StringBuilder builder = new StringBuilder();

      asciiUserinfo().ifPresent(userInfo -> builder.append(userInfo).append("@"));
      builder.append(asciiHost());
      port().ifPresent(port -> builder.append(":").append(Integer.toString(port)));

      return builder.toString();
    }

    @Override
    default int compareTo(final Authority other) {
      Objects.requireNonNull(other, "other cannot be null");

      if (other == this) {
        return 0;
      }

      final int userinfoCompare = userinfo()
          .flatMap(userinfo -> other.userinfo().map(userinfo::compareTo)).orElse(0);
      if (userinfoCompare != 0) {
        return userinfoCompare;
      }

      final int hostCompare = host().compareTo(other.host());
      if (hostCompare != 0) {
        return hostCompare;
      }

      final OptionalInt aPort = port();
      final OptionalInt bPort = other.port();

      if (aPort.isEmpty() && bPort.isEmpty()) {
        return 0;
      } else if (!aPort.isPresent() && bPort.isPresent()) {
        return -1;
      } else if (aPort.isPresent() && !bPort.isPresent()) {
        return 1;
      }

      return Integer.compare(aPort.getAsInt(), bPort.getAsInt());
    }
  }

  interface Path extends Comparable<Path> {
    int length();

    String at(int index);

    Stream<String> stream();

    boolean isAbsolute();

    default Path removeDotSegments() {
      if (length() == 0) {
        return this;
      }

      final Stack<String> out = new Stack<>();

      stream().forEach(segment -> {
        if ("..".equals(segment)) {
          if (!out.isEmpty()) {
            out.pop();
          }
        } else if (!".".equals(segment)) {
          out.push(segment);
        }
      });

      if (out.isEmpty()) {
        return DefaultIri.Path.EMPTY;
      }

      return new DefaultIri.Path(out.toArray(String[]::new));
    }

    default String escapedString() {
      final StringJoiner joiner = new StringJoiner("/");

      stream().forEach(s -> joiner.add(PercentEncoding.encode(
          s,
          ((IntPredicate) CharacterUtils::isIPChar)
              .negate()
      )));

      return (isAbsolute() ? "/" : "") + joiner.toString();
    }

    default String asciiString() {
      final StringJoiner joiner = new StringJoiner("/");

      stream().forEach(s -> joiner.add(PercentEncoding.encode(
            s,
            ((IntPredicate) CharacterUtils::isPChar)
                .negate()
        )));

      return (isAbsolute() ? "/" : "") + joiner.toString();
    }

    @Override
    default int compareTo(final Path other) {
      Objects.requireNonNull(other, "other cannot be null");

      if (this == other) {
        return 0;
      }

      int len = Math.min(length(), other.length());
      for (int i = 0; i < len; ++ i) {
        final int result = at(i).compareTo(other.at(i));
        if (result != 0) {
          return result;
        }
      }

      return Integer.compare(length(), other.length());
    }
  }
}
