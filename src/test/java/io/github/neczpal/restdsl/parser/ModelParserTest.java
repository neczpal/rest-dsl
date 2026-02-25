package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelParserTest {

    @Test
    public void testSimpleModelDefinition() {
        String input = """
                model Person {
                    id: Int
                    name: String
                    height: Double
                    isDead: Boolean
                }
        """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        RestDSLParser.FileContext fileContext = parser.file();

        assertEquals(1, fileContext.definition().size());
        RestDSLParser.ModelDefinitionContext modelContext = fileContext.definition(0).modelDefinition();
        assertEquals("Person", modelContext.ID().getText());
        assertEquals(4, modelContext.field().size());
        assertEquals("id", modelContext.field(0).ID().getText());
        assertEquals("Int", modelContext.field(0).type().getText());
        assertEquals("name", modelContext.field(1).ID().getText());
        assertEquals("String", modelContext.field(1).type().getText());
        assertEquals("height", modelContext.field(2).ID().getText());
        assertEquals("Double", modelContext.field(2).type().getText());
        assertEquals("isDead", modelContext.field(3).ID().getText());
        assertEquals("Boolean", modelContext.field(3).type().getText());
    }
}
