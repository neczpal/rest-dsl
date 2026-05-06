package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Api;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiParserTest {

    @Test
    public void testApiDefinition() {
        String input = """
                api Petstore {
                    meta {
                        title: "Petstore"
                        version: "1.0.27"
                        basePath: "/api/v3"
                    }
                }
                """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);
        RestDSLParser.ApiDefinitionContext apiDefinition = parser.file().apiDefinition(0);

        Api api = new ApiParser().parse(apiDefinition);
        assertEquals("Petstore", api.name());
        assertEquals("Petstore", api.title());
        assertEquals("1.0.27", api.version());
        assertEquals("/api/v3", api.base());
    }
}