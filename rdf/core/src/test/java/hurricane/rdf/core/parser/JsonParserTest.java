package hurricane.rdf.core.parser;

import hurricane.rdf.core.parser.JsonParser.Token;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonParserTest {
  @Test
  public void testEmptyJson() {
    final List<JsonParser.Token> output = parse("");

    assertTrue(output.isEmpty());
  }

  @Test
  public void testEmptyObject() {
    final List<JsonParser.Token> output = parse("{ }");

    assertEquals(2, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.END_OBJECT, output.get(1));
  }

  @Test
  public void testStringAttribute() {
    final List<JsonParser.Token> output = parse(" { \"a\": \"b\" } ");

    assertEquals(4, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_STRING, output.get(2));
    assertEquals(Token.END_OBJECT, output.get(3));
  }

  @Test
  public void testTwoAttributes() {
    final List<JsonParser.Token> output = parse(" { \"a\": \"b\", \"c\": \"d\" } ");

    assertEquals(6, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_STRING, output.get(2));
    assertEquals(Token.FIELD_NAME, output.get(3));
    assertEquals(Token.VALUE_STRING, output.get(4));
    assertEquals(Token.END_OBJECT, output.get(5));
  }

  @Test
  public void testEmptyArray() {
    final List<JsonParser.Token> output = parse("[ ]");

    assertEquals(2, output.size());
    assertEquals(Token.START_ARRAY, output.get(0));
    assertEquals(Token.END_ARRAY, output.get(1));
  }

  @Test
  public void testArrayString() {
    final List<JsonParser.Token> output = parse("[ \"a\" ]");

    assertEquals(3, output.size());
    assertEquals(Token.START_ARRAY, output.get(0));
    assertEquals(Token.VALUE_STRING, output.get(1));
    assertEquals(Token.END_ARRAY, output.get(2));
  }

  @Test
  public void testArrayStrings() {
    final List<JsonParser.Token> output = parse("[ \"a\", \"b\" ]");

    assertEquals(4, output.size());
    assertEquals(Token.START_ARRAY, output.get(0));
    assertEquals(Token.VALUE_STRING, output.get(1));
    assertEquals(Token.VALUE_STRING, output.get(2));
    assertEquals(Token.END_ARRAY, output.get(3));
  }

  private final List<Token> parse(final String ... input) {
    return ParserTestUtils.parse(JsonParser::new, input);
  }
}
