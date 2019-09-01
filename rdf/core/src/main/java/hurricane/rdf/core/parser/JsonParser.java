package hurricane.rdf.core.parser;

import hurricane.rdf.core.parser.JsonParser.Token;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

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
    return cp -> {
      if (cp == '{') {
        pushState(object());
        return false;
      } else if (cp == '[') {
        pushState(array());
        return false;
      } else if (cp < 0) {
        emitEndOfInput();
        return false;
      } else if (cp <= ' ') {
        // Skip whitespace:
        return true;
      }

      return false;
    };
  }

  private ParserState object() {
    return expect('{', () -> skipWhitespace(() -> cp -> {
      emit(Token.START_OBJECT);

      if (cp == '}') {
        emit(Token.END_OBJECT);
        become(popState());
        return true;
      } else if (cp == '\"') {
        // Parse attributes:
        become(firstMember());
        return false;
      }

      return false;
    }));
  }

  private ParserState array() {
    return expect('[', () -> cp -> {
      emit(Token.START_ARRAY);
      become(firstArrayMember());
      return false;
    });
  }

  private ParserState firstArrayMember() {
    return skipWhitespace(() -> cp -> {
      if (cp == ']') {
        emit(Token.END_ARRAY);
        become(popState());
        return true;
      }

      become(value(() -> arrayEndOrNextMember()));
      return false;
    });
  }

  private ParserState arrayEndOrNextMember() {
    return skipWhitespace(() -> cp -> {
      if (cp == ']') {
        emit(Token.END_ARRAY);
        become(popState());
        return true;
      } else if (cp == ',') {
        become(nextArrayMember());
        return true;
      }

      return true;
    });
  }

  private ParserState nextArrayMember() {
    return expect(',', () -> skipWhitespace(() -> value(() -> arrayEndOrNextMember())));
  }

  private ParserState firstMember() {
    return skipWhitespace(() -> cp -> {
      if (cp == '}') {
        emit(Token.END_OBJECT);
        become(popState());
        return true;
      } else if (cp == '\"') {
        // Parse attribute member:
        become(member(() -> endOrNextMember()));
        return false;
      }

      return false;
    });
  }

  private ParserState endOrNextMember() {
    return skipWhitespace(() -> cp -> {
      if (cp == '}' ) {
        emit(Token.END_OBJECT);
        become(popState());
        return true;
      } else if (cp == ',') {
        become(nextMember());
        return false;
      }

      return false;
    });
  }

  private ParserState nextMember() {
    return expect(',', () -> skipWhitespace(() -> member(() -> endOrNextMember())));
  }

  private ParserState member(final Supplier<ParserState> continuation) {
    return string(
        memberName -> skipWhitespace(
            () -> expect(
                ':',
                () -> {
                  emit(Token.FIELD_NAME);
                  return value(continuation);
                }
            )
        )
    );
  }

  private ParserState value(final Supplier<ParserState> continuation) {
    return skipWhitespace(() -> (cp -> {
      if (cp == '"') {
        // Parse string value:
        become(string(value -> {
          emit(Token.VALUE_STRING);
          return continuation.get();
        }));
      }

      return false;
    }));
  }

  private ParserState string(final Function<String, ParserState> stringConsumer) {
    final StringBuilder value = new StringBuilder();

    return expect(
        '"',
        () -> (cp -> {
          if (cp == '"') {
            // End the string and move to the state provided by the consumer:
            become(stringConsumer.apply(value.toString()));
            return true;
          } else if (cp == '\\') {
            // Handle escape sequences:
            pushState(escapedCharacter(echar -> {
              value.appendCodePoint(echar);
              return popState();
            }));
          } else if (cp < 0x20 || cp > 0x10ffff) {
            // Reject invalid characters:
            return false;
          }

          // Accept the character and add to the value:
          value.appendCodePoint(cp);

          return true;
        })
    );
  }

  public ParserState escapedCharacter(final IntFunction<ParserState> charConsumer) {
    return expect(
        '\\',
        () -> ((int cp) -> {
          switch (cp) {
            case 't':
              become(charConsumer.apply('\t'));
              return true;
            case 'b':
              become(charConsumer.apply('\b'));
              return true;
            case 'n':
              become(charConsumer.apply('\n'));
              return true;
            case 'r':
              become(charConsumer.apply('\n'));
              return true;
            case 'f':
              become(charConsumer.apply('\f'));
              return true;
            case '\"':
              become(charConsumer.apply('\"'));
              return true;
            case '\'':
              become(charConsumer.apply('\''));
              return true;
            case '\\':
              become(charConsumer.apply('\\'));
              return true;
            case 'u':
            case 'U':
              become(hexCharacters(cp, hexCharacter -> {
                return charConsumer.apply(hexCharacter);
              }));
              return true;
          }

          return false;
        }
        ));
  }

  public ParserState hexCharacters(final int c, final IntFunction<ParserState> ucharConsumer) {
    final StringBuilder hexString = new StringBuilder();
    return hexChar -> {
      // Append hex characters:
      if ((hexChar >= '0' && hexChar <= '9') || (hexChar >= 'a' && hexChar <= 'z') || (hexChar >= 'A' && hexChar <= 'Z')) {
        hexString.appendCodePoint(hexChar);

        // End the character if it has the right length:
        if ((c == 'u' && hexString.length() == 4) || (c == 'U' && hexString.length() == 8)) {
          become(ucharConsumer.apply(Integer.parseInt(hexString.toString(), 16)));
          return true;
        }
      }

      // Reject other characters:
      return false;
    };
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
