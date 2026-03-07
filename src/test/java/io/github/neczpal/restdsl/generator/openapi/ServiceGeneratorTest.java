package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Method;
import io.github.neczpal.restdsl.model.Service;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceGeneratorTest {

    @Test
    public void testGenerate() {
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
        ServiceGenerator generator = new ServiceGenerator();
        String result = generator.generate(List.of(service));
        String expected = """
                paths:
                  /api/v3/pet/{id}:
                    get:
                      summary: getPet
                      responses:
                        '200':
                          description: OK
                """;
        assertEquals(expected, result);
    }
}
