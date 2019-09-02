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
  public void testTrueAttribute() {
    final List<JsonParser.Token> output = parse(" { \"a\": true } ");

    assertEquals(4, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_TRUE, output.get(2));
    assertEquals(Token.END_OBJECT, output.get(3));
  }

  @Test
  public void testFalseAttribute() {
    final List<JsonParser.Token> output = parse(" { \"a\": false } ");

    assertEquals(4, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_FALSE, output.get(2));
    assertEquals(Token.END_OBJECT, output.get(3));
  }

  @Test
  public void testNullAttribute() {
    final List<JsonParser.Token> output = parse(" { \"a\": null } ");

    assertEquals(4, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_NULL, output.get(2));
    assertEquals(Token.END_OBJECT, output.get(3));
  }

  @Test
  public void testFloatAttribute() {
    final List<JsonParser.Token> output = parse(" { \"a\": 0.001 } ");

    assertEquals(4, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_FLOAT, output.get(2));
    assertEquals(Token.END_OBJECT, output.get(3));
  }

  @Test
  public void testFloatAttributeNegative() {
    final List<JsonParser.Token> output = parse(" { \"a\": -0.001 } ");

    assertEquals(4, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_FLOAT, output.get(2));
    assertEquals(Token.END_OBJECT, output.get(3));
  }

  @Test
  public void testFloatAttributeExponent() {
    final List<JsonParser.Token> output = parse(" { \"a\": 0.001E10 } ");

    assertEquals(4, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_FLOAT, output.get(2));
    assertEquals(Token.END_OBJECT, output.get(3));
  }


  @Test
  public void testFloatAttributeNegativeExponent() {
    final List<JsonParser.Token> output = parse(" { \"a\": 0.001E-10 } ");

    assertEquals(4, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_FLOAT, output.get(2));
    assertEquals(Token.END_OBJECT, output.get(3));
  }

  @Test
  public void testFloatAttributePositiveExponent() {
    final List<JsonParser.Token> output = parse(" { \"a\": 0.001E+10 } ");

    assertEquals(4, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.VALUE_FLOAT, output.get(2));
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
    final List<JsonParser.Token> output = parse(" [ \"a\" , \"b\" ] ");

    assertEquals(4, output.size());
    assertEquals(Token.START_ARRAY, output.get(0));
    assertEquals(Token.VALUE_STRING, output.get(1));
    assertEquals(Token.VALUE_STRING, output.get(2));
    assertEquals(Token.END_ARRAY, output.get(3));
  }

  @Test
  public void testNestedObject() {
    final List<JsonParser.Token> output = parse(" { \"a\": { \"b\": \"c\" } } ");

    assertEquals(7, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.START_OBJECT, output.get(2));
    assertEquals(Token.FIELD_NAME, output.get(3));
    assertEquals(Token.VALUE_STRING, output.get(4));
    assertEquals(Token.END_OBJECT, output.get(5));
    assertEquals(Token.END_OBJECT, output.get(6));
  }

  @Test
  public void testNestedArray() {
    final List<JsonParser.Token> output = parse(" { \"a\": [ \"a\" ] } ");

    assertEquals(6, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.START_ARRAY, output.get(2));
    assertEquals(Token.VALUE_STRING, output.get(3));
    assertEquals(Token.END_ARRAY, output.get(4));
    assertEquals(Token.END_OBJECT, output.get(5));
  }

  @Test
  public void testObjectArrayObject() {
    final List<JsonParser.Token> output = parse(" { \"a\": [ { \"b\": \"c\" } ] } ");

    assertEquals(9, output.size());
    assertEquals(Token.START_OBJECT, output.get(0));
    assertEquals(Token.FIELD_NAME, output.get(1));
    assertEquals(Token.START_ARRAY, output.get(2));
    assertEquals(Token.START_OBJECT, output.get(3));
    assertEquals(Token.FIELD_NAME, output.get(4));
    assertEquals(Token.VALUE_STRING, output.get(5));
    assertEquals(Token.END_OBJECT, output.get(6));
    assertEquals(Token.END_ARRAY, output.get(7));
    assertEquals(Token.END_OBJECT, output.get(8));
  }

  private List<Token> parse(final String ... input) {
    return ParserTestUtils.parse(JsonParser::new, input);
  }
}
