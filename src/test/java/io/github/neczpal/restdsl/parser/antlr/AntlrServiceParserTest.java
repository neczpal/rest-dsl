package io.github.neczpal.restdsl.parser.antlr;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AntlrServiceParserTest {

    @Test
    public void testServiceDefinition() {
        String input = """
                service Pet {
                    base: "/pet"

                    post addPet {
                        body: Pet
                        responses: {
                            200: Pet
                            405: "Invalid input"
                        }
                    }
                }
        """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        RestDSLParser.FileContext fileContext = parser.file();

        assertEquals(1, fileContext.definition().size());
        RestDSLParser.ServiceDefinitionContext serviceContext = fileContext.definition(0).serviceDefinition();
        assertEquals("Pet", serviceContext.ID().getText());
        assertEquals(2, serviceContext.serviceElement().size());
    }
}