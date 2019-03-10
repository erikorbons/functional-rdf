package io.github.erikorbons.functionalrdf.core.rdf.formatters;

import io.github.erikorbons.functionalrdf.core.iri.Iri;
import io.github.erikorbons.functionalrdf.core.parser.Parser;
import io.github.erikorbons.functionalrdf.core.rdf.BlankNode;
import io.github.erikorbons.functionalrdf.core.rdf.Literal;
import io.github.erikorbons.functionalrdf.core.rdf.Predicate;
import io.github.erikorbons.functionalrdf.core.rdf.Quad;
import io.github.erikorbons.functionalrdf.core.rdf.RdfObject;
import io.github.erikorbons.functionalrdf.core.rdf.Subject;
import io.github.erikorbons.functionalrdf.core.rdf.impl.DefaultBlankNode;
import io.github.erikorbons.functionalrdf.core.rdf.impl.DefaultObject;
import io.github.erikorbons.functionalrdf.core.rdf.impl.DefaultObject.BlankNodeObject;
import io.github.erikorbons.functionalrdf.core.rdf.impl.DefaultObject.IriObject;
import io.github.erikorbons.functionalrdf.core.rdf.impl.DefaultPredicate;
import io.github.erikorbons.functionalrdf.core.rdf.impl.DefaultQuad;
import io.github.erikorbons.functionalrdf.core.rdf.impl.DefaultSubject;
import io.github.erikorbons.functionalrdf.core.rdf.literals.RawLiteral;
import io.github.erikorbons.functionalrdf.core.rdf.literals.XsdString;
import java.util.function.Function;
import java.util.function.IntFunction;

public class NQuadsParser extends Parser<Quad> {

  @Override
  public ParserState initialState() {
    return cp -> {
      if (cp < 0) {
        // End-of-file:
        emitEndOfInput();
      }

      // Parse a quad:
      pushState(statement(quad -> {
        emit(quad);
        return popState();
      }));

      return false;
    };
  }

  public ParserState statement(final Function<Quad, ParserState> quadConsumer) {
    return skipWhitespace(
        subject(subject -> skipWhitespace(
            predicate(predicate -> skipWhitespace(
                object(object -> skipWhitespace(cp -> {

                  // Accept the dot character as the end of a quad without a graph label:
                  if (cp == '.') {
                    become(quadConsumer.apply(new DefaultQuad(null, subject, predicate, object)));
                    return true;
                  }

                  // Parse a graph label:
                  become(graphLabel(graphLabel -> skipWhitespace(
                      expect('.', c -> {
                        become(quadConsumer.apply(new DefaultQuad(graphLabel, subject, predicate, object)));
                        return false;
                      })
                  )));

                  return false;
                }))
            ))
        ))
    );
    /*
    return cp -> {
      // Accept all whitespace before the statement:
      if (Character.isWhitespace(cp)) {
        return true;
      }

      pushState(subject(subject -> (c -> {
        // Accept all whitespace before the predicate:
        if (Character.isWhitespace(c)) {
          return true;
        }

        pushState(predicate(predicate -> (c2 -> {
          // Accept all whitespace before the object:
          if (Character.isWhitespace(c2)) {
            return true;
          }

          pushState(object(object -> (c3 -> {
            // Accept all whitespace before the dot or graph label:
            if (Character.isWhitespace(c3)) {
              return true;
            }

            // Accept the dot character as the end of a quad without a graph label:
            if (c3 == '.') {
              become(quadConsumer.apply(new DefaultQuad(null, subject, predicate, object)));
              return true;
            }

            pushState(graphLabel(graphLabel -> (c4 -> {
              // Accept all whitespace before the dot:
              if (Character.isWhitespace(c4)) {
                return true;
              }

              if (c4 == '.') {
                become(quadConsumer.apply(new DefaultQuad(graphLabel, subject, predicate, object)));
                return true;
              }

              // Reject other characters:
              return false;
            })));

            return false;
          })));

          return false;
        })));

        return false;
      })));

      return false;
    };
    */
  }

  public ParserState subject(final Function<Subject, ParserState> subjectConsumer) {
    return cp -> {
      if (cp == '<') {
        become(iriRef(iriRef -> subjectConsumer.apply(new DefaultSubject.IriSubject(iriRef))));
      } else if (cp == '_') {
        become(blankNode(bn -> subjectConsumer.apply(new DefaultSubject.BlankNodeSubject(bn))));
      }

      return false;
    };
  }

  public ParserState predicate(final Function<Predicate, ParserState> predicateConsumer) {
    return iriRef(iri -> predicateConsumer.apply(new DefaultPredicate(iri)));

  }

  public ParserState object(final Function<RdfObject, ParserState> objectConsumer) {
    return cp -> {
      if (cp == '<') {
        become(iriRef(iri -> objectConsumer.apply(new IriObject(iri))));
      } else if (cp == '_') {
        become(blankNode(blankNode -> objectConsumer.apply(new BlankNodeObject(blankNode))));
      } else if (cp == '"') {
        become(literal(literal -> objectConsumer.apply(new DefaultObject.LiteralObject(literal))));
      }

      return false;
    };
  }

  public ParserState graphLabel(final Function<Subject, ParserState> graphNameConsumer) {
    return subject(graphNameConsumer);
  }

  public ParserState literal(final Function<Literal, ParserState> literalConsumer) {
    final StringBuilder lexicalForm = new StringBuilder();

    return expect(
        '"',
        cp -> {
          if (cp == '"') {
            // End the literal or parse a datatype iri:
            become(c -> {
              if (c == '^') {
                become(expect('^', iriRef(iri -> {
                  // Literal with iri:
                  return literalConsumer.apply(new RawLiteral(lexicalForm.toString(), iri));
                })));
                return true;
              }

              // Literal without iri:
              become(literalConsumer.apply(new XsdString(lexicalForm.toString())));
              return false;
            });

            return true;
          } else if (cp == 0xA || cp == 0xD) {
            // Reject illegal characters in literal strings:
            return false;
          } else if (cp == '\\') {
            // Parse escaped characters:
            pushState(escapedCharacter(echar -> {
              lexicalForm.appendCodePoint(echar);
              return popState();
            }));
            return false;
          }

          // Accept the character and add to the lexical form:
          lexicalForm.appendCodePoint(cp);

          return true;
        }
    );
  }

  public ParserState iriRef(final Function<Iri, ParserState> iriConsumer) {
    return cp -> {
      // Parse the start character:
      if (cp != '<') {
        return false;
      }

      final StringBuilder iri = new StringBuilder();

      // Parse IRI character until an end character:
      become(c -> {
        // Reject characters not allowed in N-Quads iri ref:
        if ((c >= 0x00 && c <= 0x20) || c == '<' || c == '\"' || c == '{' || c == '}' || c == '|'
            || c == '^' || c == '`') {
          return false;
        }

        // Parse escape sequences:
        if (c == '\\') {
          pushState(uChar(uchar -> {
            iri.appendCodePoint(uchar);
            return popState();
          }));
          return false;
        }

        // Parse the end of the iri:
        if (c == '>') {
          become(iriConsumer.apply(Iri.of(iri.toString())));
          return true;
        }

        // Other characters are added to the IRI:
        iri.appendCodePoint(c);
        return true;
      });

      return true;
    };
  }

  public ParserState blankNode(final Function<BlankNode, ParserState> blankNode) {
    return expect(
        '_',
        expect(
            ':',
            expect(
                c -> isPnCharU(c) || (c >= '0' && c <= '9'),
                pnCharU -> collectWhile(
                    c -> isPnChar(c) || c == '.',
                    chars -> {
                      final StringBuilder builder = new StringBuilder();

                      builder.appendCodePoint(pnCharU);
                      builder.append(chars);

                      return blankNode.apply(new DefaultBlankNode(builder.toString()));
                    })
            )
        )
    );
  }

  public ParserState escapedCharacter(final IntFunction<ParserState> charConsumer) {
    return expect(
        '\\',
        (int cp) -> {
          switch (cp) {
            case 't':
              become(charConsumer.apply('\t'));
              return true;
            case 'b':
              become(charConsumer.apply('\b'));
              return true;
            case 'n':
              become(charConsumer.apply('\n'));
              return true;
            case 'r':
              become(charConsumer.apply('\n'));
              return true;
            case 'f':
              become(charConsumer.apply('\f'));
              return true;
            case '\"':
              become(charConsumer.apply('\"'));
              return true;
            case '\'':
              become(charConsumer.apply('\''));
              return true;
            case '\\':
              become(charConsumer.apply('\\'));
              return true;
            case 'u':
            case 'U':
              become(hexCharacters(cp, hexCharacter -> {
                return charConsumer.apply(hexCharacter);
              }));
              return true;
          }

          return false;
        }
    );
  }

  public ParserState uChar(final IntFunction<ParserState> ucharConsumer) {
    return cp -> {
      // Parse the backslash:
      if (cp != '\\') {
        return false;
      }

      // Parse the remainder:
      become(c -> {
        // Accept only the u and U characters:
        if (c != 'u' && c != 'U') {
          return false;
        }

        // Parse the remaining hex characters:
        become(hexCharacters(c, uchar -> {
          return ucharConsumer.apply(uchar);
        }));

        return true;
      });

      return true;
    };
  }

  public ParserState hexCharacters(final int c, final IntFunction<ParserState> ucharConsumer) {
    final StringBuilder hexString = new StringBuilder();
    return hexChar -> {
      // Append hex characters:
      if ((hexChar >= '0' && hexChar <= '9') || (hexChar >= 'a' && hexChar <= 'z') || (hexChar >= 'A' && hexChar <= 'Z')) {
        hexString.appendCodePoint(hexChar);

        // End the character if it has the right length:
        if ((c == 'u' && hexString.length() == 4) || (c == 'U' && hexString.length() == 8)) {
          become(ucharConsumer.apply(Integer.parseInt(hexString.toString(), 16)));
          return true;
        }
      }

      // Reject other characters:
      return false;
    };
  }

  /**
   * PN_CHARS_BASE 	::= 	[A-Z] | [a-z] | [#x00C0-#x00D6] | [#x00D8-#x00F6] | [#x00F8-#x02FF] | [#x0370-#x037D] | [#x037F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
   */
  private static boolean isPnCharBase(final int cp) {
    return (cp >= 'A' && cp <= 'Z')
        || (cp >= 'a' && cp <= 'z')
        || (cp >= 0x00C0 && cp <= 0x00D6)
        || (cp >= 0x00D8 && cp <= 0x00F6)
        || (cp >= 0x00F8 && cp <= 0x02FF)
        || (cp >= 0x0370 && cp <= 0x037D)
        || (cp >= 0x037F && cp <= 0x1FFF)
        || (cp >= 0x200C && cp <= 0x200D)
        || (cp >= 0x2070 && cp <= 0x218F)
        || (cp >= 0x2C00 && cp <= 0x2FEF)
        || (cp >= 0x3001 && cp <= 0xD7FF)
        || (cp >= 0xF900 && cp <= 0xFDCF)
        || (cp >= 0xFDF0 && cp <= 0xFFFD)
        || (cp >= 0x10000 && cp <= 0xEFFFF);
  }

  /**
   * PN_CHARS_U 	::= 	PN_CHARS_BASE | '_' | ':'
   */
  private static boolean isPnCharU(final int cp) {
    return isPnCharBase(cp) || cp == '_' || cp == ':';
  }

  /**
   * PN_CHARS 	::= 	PN_CHARS_U | '-' | [0-9] | #x00B7 | [#x0300-#x036F] | [#x203F-#x2040]
   */
  private static boolean isPnChar(final int cp) {
    return isPnCharU(cp)
        || cp == '-'
        || (cp >= '0' && cp <= '9')
        || (cp == 0x00B7)
        || (cp >= 0x0300 && cp <= 0x036F)
        || (cp >= 0x203F && cp <= 0x2040);
  }
}
