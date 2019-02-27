package io.github.erikorbons.functionalrdf.core.iri;

import io.github.erikorbons.functionalrdf.core.encoding.PercentEncoding;
import io.github.erikorbons.functionalrdf.core.iri.Iri.Authority;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IriParser {
  private final static Pattern iriPattern = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
  private final static Pattern schemePattern = Pattern.compile("^[a-zA-Z][a-zA-Z0-9\\+\\-\\.]*$");
  private final static Pattern authorityPattern = Pattern.compile("^(([^@]+)@)?(([^\\:]+)|(\\[[0-9a-zA-Z\\:]+\\]))(\\:([0-9]+))?$");

  public static IriReference parseIriReference(final String value) throws IriSyntaxException {
    final Matcher matcher = iriPattern.matcher(Objects.requireNonNull(value, "value cannot be null"));

    if (!matcher.matches()) {
      throw new IriSyntaxException("Invalid syntax");
    }

    final Optional<String> scheme = parseScheme(matcher.group(2));

    if (scheme.isPresent()) {
      return doParseIri(scheme.get(), matcher);
    } else {
      return doParseRelativeIri(matcher);
    }
  }

  public static RelativeIri parseRelativeIri(final String value) throws IriSyntaxException {
    return parseIriReference(value).relativeIri()
        .orElseThrow(() -> new IriSyntaxException("Not a valid relative IRI"));
  }

  private static DefaultRelativeIri doParseRelativeIri(final Matcher matcher) throws IriSyntaxException {
    return new DefaultRelativeIri(
        parseAuthority(matcher.group(4), null).orElse(null),
        parseRelativePath(matcher.group(5)),
        parseQuery(matcher.group(7)).orElse(null),
        parseFragment(matcher.group(9)).orElse(null)
    );
  }

  public static Iri parseIri(final String value) throws IriSyntaxException {
    return parseIriReference(value).iri()
        .orElseThrow(() -> new IriSyntaxException("Not a valid IRI"));
  }

  private static DefaultIri doParseIri(final String scheme, final Matcher matcher) throws IriSyntaxException {
    return new DefaultIri(
        scheme,
        parseAuthority(matcher.group(4), scheme).orElse(null),
        parseIriPath(matcher.group(5)),
        parseQuery(matcher.group(7)).orElse(null),
        parseFragment(matcher.group(9)).orElse(null)
    );
  }

  private static Optional<String> parseScheme(final String scheme) throws IriSyntaxException {
    if (scheme == null) {
      return Optional.empty();
    }

    if (!schemePattern.matcher(scheme).matches()) {
      throw new IriSyntaxException(
          String.format("Invalid IriReference scheme. %s does not match %s.", scheme, schemePattern.toString())
      );
    }

    return Optional.of(scheme.toLowerCase());
  }

  public static Optional<Authority> parseAuthority(final String authority, final String scheme)
      throws IriSyntaxException {
    if (authority == null || authority.isEmpty()) {
      return Optional.empty();
    }

    final Matcher matcher = authorityPattern.matcher(authority);

    if (!matcher.matches()) {
      throw new IriSyntaxException(
          String.format("Authority section syntax error. %s does not match %s", authority, authorityPattern.toString())
      );
    }

    final Integer port = Optional
        .ofNullable(matcher.group(7))
        .map(Integer::valueOf)
        .filter(v -> !"http".equals(scheme) || v != 80)
        .filter(v -> !"https".equals(scheme) || v != 443)
        .orElse(null);

    return Optional.of(new DefaultIri.Authority(
        Optional.ofNullable(matcher.group(2)).map(PercentEncoding::decode).orElse(null),
        PercentEncoding.decode(matcher.group(3)),
        Optional.ofNullable(matcher.group(7)).map(Integer::valueOf).orElse(null)
    ));
  }

  private static List<String> parsePath(final String path) throws IriSyntaxException {
    // Split the path:
    final List<String> parts = new ArrayList<>();

    int foundPosition;
    int startIndex = 0;
    while ((foundPosition = path.indexOf('/', startIndex)) > -1) {
      final String part = path.substring(startIndex, foundPosition);
      if (startIndex > 0 || !part.isEmpty()) {
        parts.add(PercentEncoding.decode(part));
      }
      startIndex = foundPosition + 1;
    }
    if (startIndex < path.length()) {
      parts.add(path.substring(startIndex));
    } else if (path.charAt(path.length() - 1) == '/') {
      parts.add("");
    }

    return parts;
  }

  private static DefaultIri.Path parseIriPath(final String path) throws IriSyntaxException {
    if (path == null || path.isEmpty() || path.equals("/")) {
      return DefaultIri.Path.EMPTY;
    }

    return new DefaultIri.Path(parsePath(path).toArray(String[]::new));
  }

  private static DefaultRelativeIri.Path parseRelativePath(final String path) throws IriSyntaxException {
    if (path == null || path.isEmpty()) {
      return DefaultRelativeIri.Path.RELATIVE_EMPTY;
    } else if (path.equals("/")) {
      return DefaultRelativeIri.Path.ABSOLUTE_EMPTY;
    }

    return new DefaultRelativeIri.Path(path.startsWith("/"),
        parsePath(path).toArray(String[]::new));
  }

  private static Optional<String> parseQuery(final String query) {
    if (query == null) {
      return Optional.empty();
    }

    return Optional.of(PercentEncoding.decode(query));
  }

  private static Optional<String> parseFragment(final String fragment) {
    if (fragment == null) {
      return Optional.empty();
    }

    return Optional.of(PercentEncoding.decode(fragment));
  }
}
