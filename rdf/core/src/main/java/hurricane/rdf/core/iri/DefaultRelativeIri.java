package hurricane.rdf.core.iri;

import hurricane.rdf.core.iri.Iri.Authority;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class DefaultRelativeIri implements RelativeIri, IriReference {

  private Authority authority;
  private Path path;
  private String query;
  private String fragment;

  DefaultRelativeIri(final Authority authority, final Path path, final String query,
      final String fragment) {
    this.authority = authority;
    this.path = Objects.requireNonNull(path, "path cannot be null");
    this.query = query;
    this.fragment = fragment;
  }

  @Override
  public Optional<Iri> iri() {
    return Optional.empty();
  }

  @Override
  public Optional<RelativeIri> relativeIri() {
    return Optional.of(this);
  }

  @Override
  public IriReference asIriReference() {
    return this;
  }

  @Override
  public Iri iri(final Iri baseIri) {
    return Objects.requireNonNull(baseIri, "baseIri cannot be null").applyRelativeIri(this);
  }

  @Override
  public <T> T map(final Function<? super Iri, T> mapIri,
      final Function<? super RelativeIri, T> mapRelativeIri) {
    return mapRelativeIri.apply(this);
  }

  @Override
  public Optional<Authority> authority() {
    return Optional.ofNullable(authority);
  }

  @Override
  public Path path() {
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

  static class Path implements Iri.Path {
    public static final Path RELATIVE_EMPTY = new Path(false);
    public static final Path ABSOLUTE_EMPTY = new Path(true);

    private final String[] segments;
    private final boolean absolute;

    Path(final boolean absolute, final String ... segments) {
      this.absolute = absolute;
      this.segments = Arrays.copyOf(segments, segments.length);
    }

    @Override
    public int length() {
      return segments.length;
    }

    @Override
    public String at(int index) {
      if (index < 0 || index >= segments.length) {
        throw new IndexOutOfBoundsException();
      }

      return segments[index];
    }

    @Override
    public Stream<String> stream() {
      return Arrays.stream(segments);
    }

    @Override
    public boolean isAbsolute() {
      return absolute;
    }
  }
}
