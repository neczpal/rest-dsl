package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Model;
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
        RestDSLParser.ModelDefinitionContext modelDefinition = parser.file().definition(0).modelDefinition();

        Model model = new ModelParser().parse(modelDefinition);
        assertEquals("Person", model.getName());
        assertEquals(4, model.getFields().size());
        assertEquals("id", model.getFields().get(0).getName());
        assertEquals("Int", model.getFields().get(0).getType());
        assertEquals("name", model.getFields().get(1).getName());
        assertEquals("String", model.getFields().get(1).getType());
        assertEquals("height", model.getFields().get(2).getName());
        assertEquals("Double", model.getFields().get(2).getType());
        assertEquals("isDead", model.getFields().get(3).getName());
        assertEquals("Boolean", model.getFields().get(3).getType());
    }
}
