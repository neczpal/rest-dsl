package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelGeneratorTest {

    @Test
    public void testGenerate() throws Exception {
        Model model = Model.builder()
                .name("User")
                .fields(Arrays.asList(
                        Field.builder().name("id").type("Int").build(),
                        Field.builder().name("name").type("String").build()
                ))
                .build();
        ModelGenerator generator = new ModelGenerator();
        OpenAPI openAPI = new OpenAPI();
        generator.generate(openAPI, List.of(model));
        
        String result = Yaml.mapper().writeValueAsString(openAPI);
        String expected = """
                openapi: 3.0.1
                components:
                  schemas:
                    User:
                      type: object
                      properties:
                        id:
                          type: integer
                        name:
                          type: string
                """;
        assertEquals(expected.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));
    }

    @Test
    public void testGenerateInheritance() throws Exception {
        Model userModel = Model.builder()
                .name("User")
                .fields(Collections.singletonList(
                        Field.builder().name("id").type("Int").build()
                ))
                .build();
        Model indModel = Model.builder()
                .name("Individual")
                .parent(userModel)
                .fields(Collections.singletonList(
                        Field.builder().name("name").type("String").build()
                ))
                .build();
        ModelGenerator generator = new ModelGenerator();
        OpenAPI openAPI = new OpenAPI();
        generator.generate(openAPI, List.of(userModel, indModel));
        
        String result = Yaml.mapper().writeValueAsString(openAPI);
        String expected = """
                openapi: 3.0.1
                components:
                  schemas:
                    User:
                      type: object
                      properties:
                        id:
                          type: integer
                    Individual:
                      allOf:
                      - $ref: "#/components/schemas/User"
                      - type: object
                        properties:
                          name:
                            type: string
                """;
        assertEquals(expected.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));
    }
}
