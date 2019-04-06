package hurricane.rdf.core.rdf.literals;

import hurricane.rdf.core.rdf.TypedLiteral;
import java.util.Objects;

public abstract class AbstractTypedLiteral<T> implements TypedLiteral<T> {

  private final T value;

  public AbstractTypedLiteral(final T value) {
    this.value = Objects.requireNonNull(value, "value cannot be null");
  }

  @Override
  public T value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractTypedLiteral)) {
      return false;
    }

    AbstractTypedLiteral<?> that = (AbstractTypedLiteral<?>) o;

    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}
