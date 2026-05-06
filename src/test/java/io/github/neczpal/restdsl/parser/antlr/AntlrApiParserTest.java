package io.github.neczpal.restdsl.parser.antlr;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AntlrApiParserTest {

    @Test
    public void testApiDefinition() {
        String input = """
                api Petstore {
                    meta {
                        version: "1.0.0"
                        basePath: "/api/v3"
                    }
                }
                """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        RestDSLParser.FileContext fileContext = parser.file();

        assertEquals(1, fileContext.apiDefinition().size());
        RestDSLParser.ApiDefinitionContext apiContext = fileContext.apiDefinition(0);
        assertEquals("Petstore", apiContext.CAPITAL_ID().getText());
    }
}