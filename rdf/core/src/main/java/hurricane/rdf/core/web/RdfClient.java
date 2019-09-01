package hurricane.rdf.core.web;

import java.net.http.HttpClient;

public class RdfClient {

  private final HttpClient httpClient;

  public RdfClient(final HttpClient httpClient) {
    this.httpClient = httpClient;
  }

}
