package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;
import io.github.neczpal.restdsl.model.Trait;
import io.github.neczpal.restdsl.model.Type;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
                        Field.builder().name("id").type(Type.builder().name("Int").isPrimitive(true).build()).build(),
                        Field.builder().name("name").type(Type.builder().name("String").isPrimitive(true).build()).build()
                ))
                .build();
        ModelGenerator generator = new ModelGenerator();
        OpenAPI openAPI = new OpenAPI();
        generator.generate(openAPI, new ArrayList<>(), List.of(model));

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
                          format: int32
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
                        Field.builder().name("id").type(Type.builder().name("Int").isPrimitive(true).build()).build()
                ))
                .build();
        Model indModel = Model.builder()
                .name("Individual")
                .parent(userModel)
                .fields(Collections.singletonList(
                        Field.builder().name("name").type(Type.builder().name("String").isPrimitive(true).build()).build()
                ))
                .build();
        Model compModel = Model.builder()
                .name("Company")
                .parent(userModel)
                .fields(Collections.singletonList(
                        Field.builder().name("registrationNumber").type(Type.builder().name("String").isPrimitive(true).build()).build()
                ))
                .build();

        ModelGenerator generator = new ModelGenerator();
        OpenAPI openAPI = new OpenAPI();
        generator.generate(openAPI, new ArrayList<>(), List.of(userModel, indModel, compModel));

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
                          format: int32
                      discriminator:
                        propertyName: _type
                        mapping:
                          Individual: "#/components/schemas/Individual"
                          Company: "#/components/schemas/Company"
                    Individual:
                      allOf:
                      - $ref: "#/components/schemas/User"
                      - type: object
                        properties:
                          name:
                            type: string
                    Company:
                      allOf:
                      - $ref: "#/components/schemas/User"
                      - type: object
                        properties:
                          registrationNumber:
                            type: string
                """;
        assertEquals(expected.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));
    }

    @Test
    public void testGenerateWithTraits() throws Exception {
        Trait auditable = Trait.builder()
                .name("Auditable")
                .fields(Arrays.asList(
                        Field.builder().name("createdAt").type(Type.builder().name("String").isPrimitive(true).build()).build(),
                        Field.builder().name("updatedAt").type(Type.builder().name("String").isPrimitive(true).build()).build()
                ))
                .build();
        Model documentModel = Model.builder()
                .name("Document")
                .traits(Collections.singletonList(auditable))
                .fields(Collections.singletonList(
                        Field.builder().name("docId").type(Type.builder().name("String").isPrimitive(true).build()).build()
                ))
                .build();

        ModelGenerator generator = new ModelGenerator();
        OpenAPI openAPI = new OpenAPI();
        generator.generate(openAPI, List.of(auditable), List.of(documentModel));

        String result = Yaml.mapper().writeValueAsString(openAPI);
        String expected = """
            openapi: 3.0.1
            components:
              schemas:
                Auditable:
                  type: object
                  properties:
                    createdAt:
                      type: string
                    updatedAt:
                      type: string
                Document:
                  allOf:
                  - $ref: "#/components/schemas/Auditable"
                  - type: object
                    properties:
                      docId:
                        type: string
            """;
        assertEquals(expected.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));
    }
}
