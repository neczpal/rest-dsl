package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Api;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetaInfoGeneratorTest {

    @Test
    public void testGenerate() throws Exception {
        Api api = Api.builder()
                .name("Petstore")
                .title("Petstore API")
                .version("1.0.0")
                .base("/api/v3")
                .build();
        MetaInfoGenerator generator = new MetaInfoGenerator();
        OpenAPI openAPI = new OpenAPI();
        generator.generate(openAPI, api);
        
        String result = Yaml.mapper().writeValueAsString(openAPI);
        String expected = """
                openapi: 3.0.1
                info:
                  title: Petstore API
                  version: 1.0.0
                servers:
                - url: /api/v3
                """;
        // swagger-core might format it slightly differently, comparing without exact whitespaces is safer
        assertEquals(expected.replaceAll("\\s+", ""), result.replaceAll("\\s+", ""));
    }
}
