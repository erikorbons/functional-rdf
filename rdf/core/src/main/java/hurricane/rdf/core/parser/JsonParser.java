package hurricane.rdf.core.parser;

import hurricane.rdf.core.parser.JsonParser.Token;
import java.util.LinkedList;
import java.util.ListIterator;

public final class JsonParser extends Parser<Token> {

  private final short tokensPerBuffer = 100;
  private final LinkedList<Tokens> tokenBuffers;

  public JsonParser() {
    this.tokenBuffers = new LinkedList<>();
    this.tokenBuffers.addLast(new Tokens(tokensPerBuffer));
  }

  @Override
  protected void emit(final Token token) {
    final Tokens currentBuffer = tokenBuffers.peekLast();
    final Tokens buffer;

    // Create a new token buffer if required:
    if (currentBuffer.isFull()) {
      buffer = new Tokens(tokensPerBuffer);
      tokenBuffers.addLast(buffer);
    } else {
      buffer = currentBuffer;
    }

    // Append the token:
    buffer.addToken(
        token,
        "",
        0,
        0,
        0
    );
  }

  @Override
  public boolean hasToken() {
    final ListIterator<Tokens> iterator = tokenBuffers.listIterator();
    final Tokens firstBuffer = iterator.next();
    final Tokens buffer;

    // If this buffer has been consumed, check the contents of the next:
    if (firstBuffer.isConsumed()) {
      // Consumed the first buffer, see if there is a second one available:
      if (!iterator.hasNext()) {
        return false;
      }

      buffer = iterator.next();
    } else {
      buffer = firstBuffer;
    }

    // See if the next token in the active buffer signals end of input:
    return buffer.hasToken() && buffer.peekToken() != null;
  }

  @Override
  public Token nextToken() {
    final Tokens firstBuffer = tokenBuffers.peekFirst();
    final Tokens buffer;

    // If the first buffer has been consumed, move on to the next:
    if (firstBuffer.isConsumed()) {
      tokenBuffers.removeFirst();
      buffer = tokenBuffers.peekFirst();
    } else {
      buffer = firstBuffer;
    }

    // Verify that there are tokens in the buffer:
    if (!buffer.hasToken() || buffer.peekToken() == null) {
      throw new IllegalStateException("There are no more tokens available in this parser");
    }

    return buffer.nextToken();
  }

  @Override
  protected ParserState initialState() {
    return skipWhitespace(() -> cp -> {
      if (cp == '{') {
        pushState(object());
        return false;
      } else if (cp == '[') {
        pushState(array());
        return false;
      } else if (cp < 0) {
        emitEndOfInput();
        return false;
      }

      return false;
    });
  }

  private ParserState object() {
    return expect('{', () -> skipWhitespace(() -> cp -> {
      emit(Token.START_OBJECT);

      if (cp == '}') {
        emit(Token.END_OBJECT);
        become(popState());
        return true;
      }

      return false;
    }));
  }

  private ParserState array() {
    return expect('[', () -> cp -> {
      emit(Token.START_ARRAY);
      return false;
    });
  }

  public enum Token {
    START_OBJECT,
    END_OBJECT,

    START_ARRAY,
    END_ARRAY,

    FIELD_NAME,

    VALUE_NULL,
    VALUE_TRUE,
    VALUE_FALSE,
    VALUE_FLOAT,
    VALUE_INT,
    VALUE_STRING,

    END_OF_INPUT
  }

  private static class Tokens {
    private final Token[] tokens;
    private final String[] values;
    private final long[] characterOffsets;
    private final long[] lineNumbers;
    private final long[] columnNumbers;
    private short writeOffset;
    private short readOffset;

    Tokens(final short length) {
      tokens = new Token[length];
      values = new String[length];
      characterOffsets = new long[length];
      lineNumbers = new long[length];
      columnNumbers = new long[length];
      writeOffset = 0;
      readOffset = -1;
    }

    boolean isFull() {
      return writeOffset == tokens.length;
    }

    boolean isConsumed() {
      return readOffset == tokens.length - 1;
    }

    void addToken(final Token token, final String value, final long characterOffset, final long lineNumber, final long columnNumber) {
      if (isFull()) {
        throw new IllegalStateException("Token buffer is full");
      }

      this.tokens[writeOffset] = token;
      this.values[writeOffset] = value;
      this.characterOffsets[writeOffset] = characterOffset;
      this.lineNumbers[writeOffset] = lineNumber;
      this.columnNumbers[writeOffset] = columnNumber;
      ++ writeOffset;
    }

    Token nextToken() {
      final Token result = peekToken();
      ++ readOffset;
      return result;
    }

    Token peekToken() {
      if (isConsumed() || readOffset + 1 >= writeOffset) {
        throw new IllegalStateException("No tokens available");
      }

      return tokens[readOffset + 1];
    }

    boolean hasToken() {
      return readOffset < writeOffset - 1;
    }

    String currentValue() {
      return values[readOffset];
    }

    long currentCharacterOffset() {
      return characterOffsets[readOffset];
    }

    long currentLineNumber() {
      return lineNumbers[readOffset];
    }

    long currentColumnNumber() {
      return columnNumbers[readOffset];
    }
  }
}
