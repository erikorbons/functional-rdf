package io.github.erikorbons.functionalrdf.core.rdf.formatters;

import org.junit.Test;
import static org.junit.Assert.*;

public class NQuadsParserTest {

  @Test
  public void test() {
    final NQuadsParser parser = new NQuadsParser();

    parser.accept("<http://bla.com> <http://bla.com> <http://bla.com> .");
    parser.accept("_:a <http://bla.com> \"abcde\".");
    parser.endOfInput();

    assertNotNull(parser);

    while (!parser.isComplete()) {
      if (parser.hasToken()) {
        assertNotNull(parser.nextToken());
      }
    }
  }
}
