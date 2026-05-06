package io.github.neczpal.restdsl.parser.antlr;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AntlrServiceParserTest {

    @Test
    public void testServiceDefinition() {
        String input = """
                api Test {
                    paths {
                        /pet {
                            post addPet(body: Pet) -> Pet
                        }
                    }
                }
                """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        RestDSLParser.FileContext fileContext = parser.file();

        RestDSLParser.ApiDefinitionContext apiContext = fileContext.apiDefinition(0);
        RestDSLParser.PathsDefinitionContext pathsContext = null;
        for (RestDSLParser.ApiElementContext elem : apiContext.apiElement()) {
            if (elem.pathsDefinition() != null) {
                pathsContext = elem.pathsDefinition();
            }
        }

        assertNotNull(pathsContext);
        assertEquals(1, pathsContext.pathElement().size());
        RestDSLParser.PathDefinitionContext pathContext = pathsContext.pathElement(0).pathDefinition();
        assertEquals("/pet", pathContext.PATH_ID().getText());
        assertEquals(1, pathContext.pathBlock().pathElement().size());
        RestDSLParser.EndpointDefinitionContext endpointContext = pathContext.pathBlock().pathElement(0).endpointDefinition();
        assertEquals("post", endpointContext.httpMethod().getText());
        assertEquals("addPet", endpointContext.anyId().getText());
    }
}