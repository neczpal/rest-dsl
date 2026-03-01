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
        Method method = new Method("GET", "getPet", "/pet/{id}", null, Collections.emptyList(), Collections.emptyList(), Collections.emptyMap());
        Service service = new Service("PetService", "/api/v3", List.of(method));
        ServiceGenerator generator = new ServiceGenerator();
        String result = generator.generate(List.of(service));
        String expected = """
                paths:
                  /api/v3/pet/{id}:
                    get:Ï
                      summary: getPet
                """;
        assertEquals(expected, result);
    }
}
