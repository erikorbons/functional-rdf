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

  private final List<Token> parse(final String ... input) {
    return ParserTestUtils.parse(JsonParser::new, input);
  }
}
