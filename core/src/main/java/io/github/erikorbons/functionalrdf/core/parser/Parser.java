package io.github.erikorbons.functionalrdf.core.parser;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;

public abstract class Parser<T> {

  private LinkedList<ParserState> state = new LinkedList<>();
  private LinkedList<T> tokens = new LinkedList<>();
  private boolean endSignalled = false;
  private boolean atEnd = false;

  public void accept(final CharSequence characters) {
    if (endSignalled) {
      throw new IllegalStateException("Cannot accept input after end-of-input was signalled");
    }

    if (this.state.isEmpty()) {
      pushState(initialState());
    }

    characters.codePoints().forEach(this::acceptCodePoint);
  }

  public void endOfInput() {
    if (endSignalled) {
      throw new IllegalStateException("Cannot accept input after end-of-input was signalled");
    }

    acceptCodePoint(-1);
    endSignalled = true;
  }

  public boolean hasToken() {
    return !tokens.isEmpty();
  }

  public T nextToken() {
    if (!hasToken()) {
      throw new IllegalStateException("The parser has no tokens available.");
    }

    return tokens.pollFirst();
  }

  public boolean isComplete() {
    return atEnd && !hasToken();
  }

  private void acceptCodePoint(final int codePoint) {
    ParserState state = this.state.peek();

    while (!state.acceptChar(codePoint)) {
      if (atEnd) {
        break;
      }

      // Fail if the character wasn't handled and the state didn't change:
      final ParserState newState = this.state.peek();
      if (state == newState) {
        if (codePoint < 0) {
          throw new IllegalArgumentException("Unexpected EOF");
        } else {
          final StringBuilder message = new StringBuilder("Invalid character: ");
          message.appendCodePoint(codePoint);
          throw new IllegalArgumentException(message.toString());
        }
      }

      state = newState;
    }
  }

  protected abstract ParserState initialState();

  protected void emit(final T token) {
    Objects.requireNonNull(token, "token cannot be null");
    tokens.addLast(token);
  }

  protected void emitEndOfInput() {
    tokens.addLast(null);
    atEnd = true;
  }

  protected ParserState pushState(final ParserState state) {
    this.state.push(Objects.requireNonNull(state, "state cannot be null"));
    return state;
  }

  protected ParserState popState() {
    state.pop();
    return state.peek();
  }

  protected ParserState peekState() {
    return state.peek();
  }

  protected void become(final ParserState state) {
    popState();
    pushState(state);
  }

  protected ParserState skip(final IntPredicate predicate, final ParserState nextState) {
    return cp -> {
      if (predicate.test(cp)) {
        return true;
      }

      become(nextState);
      return false;
    };
  }

  protected ParserState skipWhitespace(final ParserState nextState) {
    return skip(i -> i <= ' ', nextState);
  }

  protected ParserState expect(final int expectedCodePoint, final ParserState nextState) {
    return cp -> {
      if (cp == expectedCodePoint) {
        become(nextState);
        return true;
      }

      return false;
    };
  }

  protected ParserState expect(final IntPredicate predicate, final IntFunction<ParserState> handler) {
    return cp -> {
      if (predicate.test(cp)) {
        become(handler.apply(cp));
        return true;
      }

      return false;
    };
  }

  protected ParserState collectWhile(final IntPredicate predicate, final Function<String, ParserState> handler) {
    final StringBuilder builder = new StringBuilder();

    return cp -> {
      if (predicate.test(cp)) {
        builder.appendCodePoint(cp);
        return true;
      }

      become(handler.apply(builder.toString()));
      return false;
    };
  }

  @FunctionalInterface
  public interface ParserState {
    boolean acceptChar(int codepoint);
  }
}
