package hurricane.rdf.core.rdf.formatters;

import java.util.stream.IntStream;

public class NQuads {

  public static void escapeLiteralValue(final StringBuilder builder, final String rawValue) {
    rawValue.chars().forEach((c) -> {
      switch (c) {
        case '\t': builder.append("\\t"); break;
        case '\b': builder.append("\\b"); break;
        case '\n': builder.append("\\n"); break;
        case '\r': builder.append("\\r"); break;
        case '\f': builder.append("\\f"); break;
        case '\"': builder.append("\\\""); break;
        case '\'': builder.append("\\\'"); break;
        case '\\': builder.append("\\\\"); break;
        default:
          if (c >= ' ' && c < 0xFFFF) {
            builder.append((char) c);
          } else {
            toUchar(builder, c);
          }
          break;
      }
    });
  }

  private static void toUchar(final StringBuilder builder, final int ch) {
    final String hexValue = Integer.toHexString(ch);
    final int padding;

    if (ch > 0xFFFF) {
      padding = 8 - hexValue.length();
      builder.append("\\U");
    } else {
      padding = 4 - hexValue.length();
      builder.append("\\u");
    }

    IntStream.range(0, padding).forEach(i -> builder.append('0'));

    builder.append(hexValue);
  }

  private static String toEchar(final int ch) {
    switch (ch) {
      case '\t':
        return "\\t";
      case '\b':
        return "\\b";
      case '\n':
        return "\\n";
      case '\r':
        return "\\r";
      case '\f':
        return "\\f";
      case '\"':
        return "\\\"";
      case '\'':
        return "\\\'";
      case '\\':
        return "\\\\";
      default:
        return null;
    }
  }

  private static boolean isPnChar(final int ch) {
    return (ch >= 'A' && ch <= 'Z')
        || (ch >= 'a' && ch <= 'z')
        || (ch >= 0x00C0 && ch <= 0x00D6)
        || (ch >= 0x00D8 && ch <= 0x00F6)
        || (ch >= 0x00F8 && ch <= 0x02FF)
        || (ch >= 0x0370 && ch <= 0x037D)
        || (ch >= 0x037F && ch <= 0x1FFF)
        || (ch >= 0x200C && ch <= 0x200D)
        || (ch >= 0x2070 && ch <= 0x218F)
        || (ch >= 0x2C00 && ch <= 0x2FEF)
        || (ch >= 0x3001 && ch <= 0xD7FF)
        || (ch >= 0xF900 && ch <= 0xFDCF)
        || (ch >= 0xFDF0 && ch <= 0xFFFD)
        || (ch >= 0x1000 && ch <= 0xEFFFF);
  }
}
