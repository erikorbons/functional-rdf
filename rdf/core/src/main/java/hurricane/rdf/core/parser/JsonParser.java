package hurricane.rdf.core.parser;

import hurricane.rdf.core.parser.JsonParser.Token;
import java.util.LinkedList;

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
  protected ParserState initialState() {
    return skipWhitespace(() -> cp -> {
      if (cp == '{') {
        pushState(object());
        return false;
      } else if (cp == '[') {
        pushState(array());
        return false;
      }

      return false;
    });
  }

  private ParserState object() {
    return expect('{', () -> cp -> {
      emit(Token.START_OBJECT);
      return false;
    });
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
      return readOffset == tokens.length;
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
      if (isConsumed() || readOffset + 1 >= writeOffset) {
        throw new IllegalStateException("No tokens available");
      }

      ++ readOffset;
      return tokens[readOffset];
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
