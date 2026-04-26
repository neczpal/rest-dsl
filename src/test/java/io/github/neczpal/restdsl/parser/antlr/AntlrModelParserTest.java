package io.github.neczpal.restdsl.parser.antlr;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AntlrModelParserTest {

    @Test
    public void testSimpleModelDefinition() {
        String input = """
                api Test {
                    models {
                        Person {
                            id: Int,
                            name: String
                        }
                    }
                }
        """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        RestDSLParser.FileContext fileContext = parser.file();

        RestDSLParser.ApiDefinitionContext apiContext = fileContext.apiDefinition(0);
        RestDSLParser.ModelsDefinitionContext modelsContext = null;
        for (RestDSLParser.ApiElementContext elem : apiContext.apiElement()) {
            if (elem.modelsDefinition() != null) {
                modelsContext = elem.modelsDefinition();
            }
        }

        assertNotNull(modelsContext);
        assertEquals(1, modelsContext.modelDefinition().size());
        RestDSLParser.ModelDefinitionContext modelContext = modelsContext.modelDefinition(0);
        assertEquals("Person", modelContext.CAPITAL_ID(0).getText());
        assertEquals(1, modelContext.CAPITAL_ID().size());
        assertEquals(2, modelContext.modelBlock().field().size());
        assertEquals("id", modelContext.modelBlock().field(0).anyId().getText());
        assertEquals("Int", modelContext.modelBlock().field(0).type().getText());
        assertEquals("name", modelContext.modelBlock().field(1).anyId().getText());
        assertEquals("String", modelContext.modelBlock().field(1).type().getText());
    }

    @Test
    public void testModelInheritanceDefinition() {
        String input = """
                api Test {
                    models {
                        User {
                            id: Int
                        }
                        Individual : User {
                            name: String
                        }
                    }
                }
        """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        RestDSLParser.FileContext fileContext = parser.file();

        RestDSLParser.ApiDefinitionContext apiContext = fileContext.apiDefinition(0);
        RestDSLParser.ModelsDefinitionContext modelsContext = null;
        for (RestDSLParser.ApiElementContext elem : apiContext.apiElement()) {
            if (elem.modelsDefinition() != null) {
                modelsContext = elem.modelsDefinition();
            }
        }

        assertNotNull(modelsContext);
        assertEquals(2, modelsContext.modelDefinition().size());
        
        RestDSLParser.ModelDefinitionContext userContext = modelsContext.modelDefinition(0);
        assertEquals("User", userContext.CAPITAL_ID(0).getText());
        assertEquals(1, userContext.CAPITAL_ID().size());
        
        RestDSLParser.ModelDefinitionContext indContext = modelsContext.modelDefinition(1);
        assertEquals("Individual", indContext.CAPITAL_ID(0).getText());
        assertEquals("User", indContext.CAPITAL_ID(1).getText());
        assertEquals(2, indContext.CAPITAL_ID().size());
    }
}
