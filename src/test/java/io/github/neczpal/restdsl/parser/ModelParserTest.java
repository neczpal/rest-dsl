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
        assertEquals("Person", model.name());
        assertEquals(4, model.fields().size());
        assertEquals("id", model.fields().get(0).name());
        assertEquals("Int", model.fields().get(0).type());
        assertEquals("name", model.fields().get(1).name());
        assertEquals("String", model.fields().get(1).type());
        assertEquals("height", model.fields().get(2).name());
        assertEquals("Double", model.fields().get(2).type());
        assertEquals("isDead", model.fields().get(3).name());
        assertEquals("Boolean", model.fields().get(3).type());
    }
}
