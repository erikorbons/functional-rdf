package hurricane.rdf.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ParserTestUtils {

  public static <T> List<T> parse(final Supplier<? extends Parser<T>> parserSupplier,
      final String ... input) {

    final Parser<T> parser = parserSupplier.get();

    // Send all text at once:
    for (final String i: input) {
      parser.accept(i);
    }
    parser.endOfInput();

    final List<T> result = new ArrayList<>();
    while (parser.hasToken()) {
      result.add(parser.nextToken());
    }

    return result;
  }
}
