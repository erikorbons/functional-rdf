package hurricane.rdf.core.rdf.formatters;

import static org.junit.Assert.assertEquals;

import hurricane.rdf.core.parser.ParserTestUtils;
import hurricane.rdf.core.iri.Iri;
import hurricane.rdf.core.rdf.Dataset;
import hurricane.rdf.core.rdf.Literal;
import hurricane.rdf.core.rdf.Rdf;
import hurricane.rdf.core.rdf.impl.DefaultRdf;
import org.junit.Before;
import org.junit.Test;

public class NQuadsParserTest {

  private Rdf rdf;

  @Before
  public void createRdf() {
    this.rdf = new DefaultRdf();
  }

  private Dataset parse(final String ... input) {
    return rdf.dataset(ds ->
        ParserTestUtils.parse(NQuadsParser::new, input)
            .forEach(ds::quad)
    );
  }

  @Test
  public void test() {
    assertEquals(
        rdf.dataset(ds -> ds
            .triple(Iri.of("http://bla.com"), Iri.of("http://bla.com"), Iri.of("http://bla.com"))
            .triple(ds.blankNode("a"), Iri.of("http://bla.com"), Literal.of("abcde"))
        ),
        parse(
            "<http://bla.com> <http://bla.com> <http://bla.com> .",
            "_:a <http://bla.com> \"abcde\"."
        )
    );
  }
}
