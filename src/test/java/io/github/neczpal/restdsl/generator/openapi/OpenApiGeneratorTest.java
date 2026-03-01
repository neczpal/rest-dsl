package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Api;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Model;
import io.github.neczpal.restdsl.model.Service;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenApiGeneratorTest {

    @Test
    public void testGenerate() {
        Api api = new Api("Petstore", "Petstore API", "1.0.0", "/api/v3");
        Model model = new Model("User", Arrays.asList(
                new Field("id", "Int"),
                new Field("name", "String")
        ));
        Method method = new Method("GET", "getPet", "/pet/{id}", null, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        Service service = new Service("PetService", "/api/v3", List.of(method));

        OpenApiGenerator generator = new OpenApiGenerator();
        String result = generator.generate(api, List.of(model), List.of(service));

        String expected = """
                openapi: 3.0.0
                info:
                  title: Petstore API
                  version: 1.0.0
                servers:
                  - url: /api/v3
                paths:
                  /api/v3/pet/{id}:
                    get:
                      summary: getPet
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
        assertEquals(expected, result);
    }
}
