package hurricane.rdf.core.iri;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Stream;

public class DefaultIri implements Iri, IriReference {

  private final String scheme;
  private final Iri.Authority authority;
  private final Iri.Path path;
  private final String query;
  private final String fragment;

  DefaultIri(final String scheme, final Iri.Authority authority, final Iri.Path path,
      final String query, final String fragment) {
    this.scheme = Objects.requireNonNull(scheme, "scheme cannot be null");
    this.authority = authority;
    this.path = Objects.requireNonNull(path, "path cannot be null");
    this.query = query;
    this.fragment = fragment;
  }

  @Override
  public Optional<Iri> iri() {
    return Optional.of(this);
  }

  @Override
  public Optional<RelativeIri> relativeIri() {
    return Optional.empty();
  }

  @Override
  public IriReference asIriReference() {
    return this;
  }

  @Override
  public Iri iri(Iri baseIri) {
    return this;
  }

  @Override
  public <T> T map(final Function<? super Iri, T> mapIri,
      final Function<? super RelativeIri, T> mapRelativeIri) {
    return mapIri.apply(this);
  }

  @Override
  public String scheme() {
    return scheme;
  }

  @Override
  public Optional<Iri.Authority> authority() {
    return Optional.ofNullable(authority);
  }

  @Override
  public Iri.Path path() {
    return path;
  }

  @Override
  public Optional<String> query() {
    return Optional.ofNullable(query);
  }

  @Override
  public Optional<String> fragment() {
    return Optional.ofNullable(fragment);
  }

  @Override
  public Iri normalize() {
    return this;
  }

  @Override
  public boolean isNormalized() {
    return true;
  }

  @Override
  public Iri applyRelativeIri(final RelativeIri relativeIri) {
    Objects.requireNonNull(relativeIri, "relativeIri cannot be null");

    final Optional<Iri.Authority> newAuthority = relativeIri.authority();
    if (newAuthority.isPresent()) {
      // Only keep the scheme from this IRI:
      return new DefaultIri(
          scheme,
          newAuthority.get(),
          relativeIri.path(),
          relativeIri.query().orElse(null),
          relativeIri.fragment().orElse(null)
      );
    } else if (relativeIri.path().isAbsolute() || path.length() == 0) {
      // Replace an absolute path:
      return new DefaultIri(
          scheme,
          authority,
          relativeIri.path().removeDotSegments(),
          relativeIri.query().orElse(null),
          relativeIri.fragment().orElse(null)
      );
    } else {
      // Apply relative paths:
      final Iri.Path newPath = new Path(
          Stream.concat(path.stream().limit(path.length() - 1), relativeIri.path().stream())
              .toArray(String[]::new))
          .removeDotSegments();

      return new DefaultIri(
          scheme,
          authority,
          newPath,
          relativeIri.query().orElse(null),
          relativeIri.fragment().orElse(null)
      );
    }
  }

  @Override
  public Iri withFragment(final String fragment) {
    if ((this.fragment == null && fragment == null) || (this.fragment != null && this.fragment
        .equals(fragment))) {
      return this;
    }

    return new DefaultIri(
        this.scheme,
        this.authority,
        this.path,
        this.query,
        fragment
    );
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof Iri)) {
      return false;
    }

    return compareTo((Iri) obj) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        scheme,
        authority,
        path,
        query,
        fragment
    );
  }

  static class Authority implements Iri.Authority {
    private final String userinfo;
    private final String host;
    private final Integer port;

    Authority(final String userinfo, final String host, final Integer port) {
      this.userinfo = userinfo;
      this.host = Objects.requireNonNull(host, "host cannot be null");
      this.port = port;
    }

    @Override
    public Optional<String> userinfo() {
      return Optional.ofNullable(userinfo);
    }

    @Override
    public String host() {
      return host;
    }

    @Override
    public OptionalInt port() {
      return port == null
          ? OptionalInt.empty()
          : OptionalInt.of(port);
    }

    @Override
    public boolean equals(final Object obj) {
      if (!(obj instanceof Iri.Authority)) {
        return false;
      }

      return compareTo((Iri.Authority) obj) == 0;
    }

    @Override
    public int hashCode() {
      return Objects.hash(
          userinfo,
          host,
          port
      );
    }
  }

  static class Path implements Iri.Path {

    public static final Path EMPTY = new Path();

    private final String[] elements;

    Path(final String... elements) {
      this.elements = Arrays.copyOf(elements, elements.length);
    }

    @Override
    public boolean isAbsolute() {
      return true;
    }

    @Override
    public int length() {
      if (elements.length == 1 && elements[0].isEmpty()) {
        return 0;
      }

      return elements.length;
    }

    @Override
    public String at(int index) {
      if (index < 0 || index >= length()) {
        throw new IndexOutOfBoundsException("index " + index + " out of range");
      }

      return elements[index];
    }

    @Override
    public Stream<String> stream() {
      return length() == 0 ? Stream.empty() : Arrays.stream(elements);
    }

    @Override
    public boolean equals(final Object obj) {
      if (!(obj instanceof Iri.Path)) {
        return false;
      }

      return compareTo((Iri.Path) obj) == 0;
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(elements);
    }
  }
}
