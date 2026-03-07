package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Api;
import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Model;
import io.github.neczpal.restdsl.model.RestDsl;
import io.github.neczpal.restdsl.model.Service;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpenApiGeneratorTest {

    @Test
    public void testGenerate() {
        Api api = Api.builder()
                .name("Petstore")
                .title("Petstore API")
                .version("1.0.0")
                .base("/api/v3")
                .build();
        Model model = Model.builder()
                .name("User")
                .fields(Arrays.asList(
                        Field.builder().name("id").type("Int").build(),
                        Field.builder().name("name").type("String").build()
                ))
                .build();
        Method method = Method.builder()
                .verb("GET")
                .name("getPet")
                .path("/pet/{id}")
                .pathParams(Collections.emptyList())
                .queryParams(Collections.emptyList())
                .responses(Collections.emptyMap())
                .build();
        Service service = Service.builder()
                .name("PetService")
                .base("/api/v3")
                .methods(List.of(method))
                .build();

        OpenApiGenerator generator = new OpenApiGenerator();
        RestDsl restDsl = RestDsl.builder()
                .api(api)
                .models(List.of(model))
                .services(List.of(service))
                .build();
        String result = generator.generate(restDsl);

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
                      responses:
                        '200':
                          description: OK
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
