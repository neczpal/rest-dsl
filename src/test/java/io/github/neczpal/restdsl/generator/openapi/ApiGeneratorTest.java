package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Api;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiGeneratorTest {

    @Test
    public void testGenerate() {
        Api api = new Api("Petstore", "Petstore API", "1.0.0", "/api/v3");
        ApiGenerator generator = new ApiGenerator();
        String result = generator.generate(api);
        String expected = """
                openapi: 3.0.0
                info:
                  title: Petstore API
                  version: 1.0.0
                servers:
                  - url: /api/v3
                """;
        assertEquals(expected, result);
    }
}
