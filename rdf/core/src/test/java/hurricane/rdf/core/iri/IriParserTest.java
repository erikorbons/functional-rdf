package hurricane.rdf.core.iri;

import org.junit.Test;

import static org.junit.Assert.*;

public class IriParserTest {

  @Test
  public void testHostScheme() throws Exception {
    final Iri a = IriParser.parseIri("http://test.com");

    assertEquals("http", a.scheme());
    assertEquals("test.com", a.authority().get().escapedHost());
    assertEquals(0, a.path().length());
  }

  @Test
  public void testPathNoHost() throws Exception {
    final Iri a = IriParser.parseIri("file:///");

    assertEquals("file", a.scheme());
    assertFalse(a.authority().isPresent());
    assertEquals(0, a.path().length());

    final Iri b = IriParser.parseIri("file:///a");
    assertEquals(1, b.path().length());
    assertEquals("a", b.path().at(0));

    final Iri c = IriParser.parseIri("file:///a/");
    assertEquals(2, c.path().length());
    assertEquals("a", c.path().at(0));
    assertEquals("", c.path().at(1));
  }

  @Test(expected = IriSyntaxException.class)
  public void testIriWithoutScheme() throws Exception {
    assertTrue(IriParser.parseIriReference("//test.com").relativeIri().isPresent());

    IriParser.parseIri("//test.com");
  }

  @Test(expected = IriSyntaxException.class)
  public void testRelativeWithScheme() throws Exception {
    assertTrue(IriParser.parseIriReference("http://test.com").iri().isPresent());

    IriParser.parseRelativeIri("http://test.com");
  }

  @Test
  public void testRelativeWithAuthority() throws Exception {
    final RelativeIri a = IriParser.parseRelativeIri("//test.com");

    assertTrue(a.authority().isPresent());
    assertEquals("test.com", a.authority().get().escapedHost());
  }
}
