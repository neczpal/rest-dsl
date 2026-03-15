package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Api;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiGeneratorTest {

    @Test
    public void testGenerate() {
        Api api = Api.builder()
                .name("Petstore")
                .title("Petstore API")
                .version("1.0.0")
                .base("/api/v3")
                .build();
        ApiGenerator generator = new ApiGenerator();
        String result = generator.generate(api).build().toString();
        String expected = """
                openapi: 3.0.0
                info:
                  title: Petstore API
                  version: 1.0.0
                servers:
                  - 
                    url: /api/v3
                """;
        assertEquals(expected.trim(), result.trim());
    }
}
