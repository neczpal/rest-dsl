package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiParserTest {

    @Test
    public void testApiDefinition() {
        String input = """
                api Petstore {
                    title: "Petstore"
                    version: "1.0.27"
                    base: "/api/v3"
                }
        """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        RestDSLParser.FileContext fileContext = parser.file();

        assertEquals(1, fileContext.definition().size());
        RestDSLParser.ApiDefinitionContext apiContext = fileContext.definition(0).apiDefinition();
        assertEquals("Petstore", apiContext.ID().getText());
    }
}
