package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Model;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModelParserTest {

    @Test
    public void testSimpleModelDefinition() {
        String input = """
                api Test {
                    models {
                        Person {
                            id: Int
                            name: String
                            height: Double
                            isDead: Boolean
                        }
                    }
                }
        """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);
        RestDSLParser.ApiDefinitionContext apiDefinition = parser.file().apiDefinition(0);
        RestDSLParser.ModelsDefinitionContext modelsDefinition = null;
        for (RestDSLParser.ApiElementContext element : apiDefinition.apiElement()) {
            if (element.modelsDefinition() != null) {
                modelsDefinition = element.modelsDefinition();
            }
        }

        List<Model> models = new ModelParser().parse(modelsDefinition, new ArrayList<>());
        assertEquals(1, models.size());
        Model model = models.getFirst();
        assertEquals("Person", model.name());
        assertNull(model.parent());
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
        RestDSLParser.ApiDefinitionContext apiDefinition = parser.file().apiDefinition(0);
        RestDSLParser.ModelsDefinitionContext modelsDefinition = null;
        for (RestDSLParser.ApiElementContext element : apiDefinition.apiElement()) {
            if (element.modelsDefinition() != null) {
                modelsDefinition = element.modelsDefinition();
            }
        }

        List<Model> models = new ModelParser().parse(modelsDefinition, new ArrayList<>());
        Map<String, Model> modelsMap = models.stream().collect(Collectors.toMap(Model::name, Function.identity()));

        assertEquals(2, models.size());
        Model user = modelsMap.get("User");
        assertEquals("User", user.name());
        assertNull(user.parent());
        assertEquals(1, user.fields().size());
        assertEquals("id", user.fields().getFirst().name());
        assertEquals("Int", user.fields().getFirst().type());

        Model ind = modelsMap.get("Individual");
        assertEquals("Individual", ind.name());
        assertNotNull(ind.parent());
        assertEquals("User", ind.parent().name());
        assertEquals(1, ind.fields().size());
        assertEquals("name", ind.fields().getFirst().name());
        assertEquals("String", ind.fields().getFirst().type());
    }
}
