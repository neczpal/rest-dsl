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

public class NewTypesParserTest {

    @Test
    public void testNewTypesModelDefinition() {
        String dsl = """
                api NewTypesApi {
                    meta {
                        title: "New Types API"
                        version: "1.0.0"
                        base: "/api"
                    }

                    models {
                        NewTypes {
                            dateTime: DateTime
                            date: Date
                            time: Time
                            float: Float
                            bigInt: BigInt
                            binary: Binary
                        }
                    }

                    paths {
                        /new-types {
                            get getNewTypes -> NewTypes
                        }
                    }
                }
                """;
        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(dsl));
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

        assertEquals(1, models.size());

        Model newTypes = modelsMap.get("NewTypes");
        assertEquals("NewTypes", newTypes.name());
        assertEquals(6, newTypes.fields().size());
        assertEquals("dateTime", newTypes.fields().get(0).name());
        assertEquals("DateTime", newTypes.fields().get(0).type().name());
        assertEquals("date", newTypes.fields().get(1).name());
        assertEquals("Date", newTypes.fields().get(1).type().name());
        assertEquals("time", newTypes.fields().get(2).name());
        assertEquals("Time", newTypes.fields().get(2).type().name());
        assertEquals("float", newTypes.fields().get(3).name());
        assertEquals("Float", newTypes.fields().get(3).type().name());
        assertEquals("bigInt", newTypes.fields().get(4).name());
        assertEquals("BigInt", newTypes.fields().get(4).type().name());
        assertEquals("binary", newTypes.fields().get(5).name());
        assertEquals("Binary", newTypes.fields().get(5).type().name());
    }
}
