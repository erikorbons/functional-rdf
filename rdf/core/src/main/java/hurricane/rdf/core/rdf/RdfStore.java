package hurricane.rdf.core.rdf;

import hurricane.rdf.core.iri.Iri;
import java.util.concurrent.CompletionStage;

public interface RdfStore {

  CompletionStage<Dataset> getDataset(Iri datasetIri);
}
