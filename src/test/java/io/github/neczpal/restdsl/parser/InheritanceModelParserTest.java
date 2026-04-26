package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Model;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InheritanceModelParserTest {

    @Test
    public void testInheritanceModelDefinition() {
        String input = """
                api Test {
                    models {
                        User {
                            phone: String
                            email: String
                        }
                        Individual : User {
                            title: String
                            firstName: String
                            lastName: String
                        }
                        Company : User {
                            companyName: String
                            companyType: String
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

        List<Model> models = new ModelParser().parse(modelsDefinition);
        Map<String, Model> modelsMap = models.stream().collect(Collectors.toMap(Model::name, Function.identity()));

        assertEquals(3, models.size());

        Model user = modelsMap.get("User");
        assertEquals("User", user.name());
        assertNull(user.parent());
        assertEquals(2, user.fields().size());
        assertEquals("phone", user.fields().get(0).name());
        assertEquals("String", user.fields().get(0).type());
        assertEquals("email", user.fields().get(1).name());
        assertEquals("String", user.fields().get(1).type());

        Model individual = modelsMap.get("Individual");
        assertEquals("Individual", individual.name());
        assertNotNull(individual.parent());
        assertEquals("User", individual.parent().name());
        assertEquals(3, individual.fields().size());
        assertEquals("title", individual.fields().get(0).name());
        assertEquals("String", individual.fields().get(0).type());
        assertEquals("firstName", individual.fields().get(1).name());
        assertEquals("String", individual.fields().get(1).type());
        assertEquals("lastName", individual.fields().get(2).name());
        assertEquals("String", individual.fields().get(2).type());

        Model company = modelsMap.get("Company");
        assertEquals("Company", company.name());
        assertNotNull(company.parent());
        assertEquals("User", company.parent().name());
        assertEquals(2, company.fields().size());
        assertEquals("companyName", company.fields().get(0).name());
        assertEquals("String", company.fields().get(0).type());
        assertEquals("companyType", company.fields().get(1).name());
        assertEquals("String", company.fields().get(1).type());
    }
}
