package io.github.erikorbons.functionalrdf.core.rdf;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import org.junit.Test;

public class RdfTest {

  @Test
  public void test() {
    final Rdf r = Rdf.create();

    final Dataset dataset = r.dataset(d -> {
      d.withNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "http://www.w3.org/2000/01/rdf-schema#", "http://www.w3.org/2002/07/owl#", "http://purl.org/dc/elements/1.1/", (rdf, rdfs, owl, dc) -> {

        /*
        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> a owl:Ontology ;
          dc:title "The RDF Concepts Vocabulary (RDF)" ;
          dc:description "This is the RDF Schema for the RDF vocabulary terms in the RDF Namespace, defined in RDF 1.1 Concepts." .
        */
        d.resource(rdf.iri(), res -> res
            .triple(rdf.expand("type"), owl.expand("Ontology"))
            .triple(dc.expand("title"), Literal.of("The RDF Concepts Vocabulary (RDF)"))
            .triple(dc.expand("description"), Literal
                .of("This is the RDF Schema for the RDF vocabulary terms in the RDF Namespace, defined in RDF 1.1 Concepts.")));

        /*
        rdf:HTML a rdfs:Datatype ;
          rdfs:subClassOf rdfs:Literal ;
          rdfs:isDefinedBy <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ;
          rdfs:seeAlso <http://www.w3.org/TR/rdf11-concepts/#section-html> ;
          rdfs:label "HTML" ;
          rdfs:comment "The datatype of RDF literals storing fragments of HTML content" .
        */
        d.resource(rdf.expand("HTML"), res -> res
            .triple(rdf.expand("type"), rdfs.expand("Datatype"))
            .triple(rdfs.expand("isDefinedBy"), rdf.iri())
            .triple(rdfs.expand("seeAlso"),
                Iri.of("http://www.w3.org/TR/rdf11-concepts/#section-html"))
            .triple(rdfs.expand("label"), Literal.of("HTML"))
            .triple(rdfs.expand("comment"),
                Literal.of("The datatype of RDF literals storing fragments of HTML content")));


        d.triple(rdf.expand("subject"), rdf.expand("type"), rdf.expand("Property"))
            .triple(rdf.expand("predicate"), rdf.expand("type"), rdf.expand("Property"));
      });
    });

    System.out.println(dataset);
  }

}
