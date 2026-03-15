package io.github.neczpal.restdsl.generator.openapi;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
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
        YamlMappingBuilder openapi = Yaml.createYamlMappingBuilder();
        String result = generator.generate(openapi, List.of(service)).build().toString();
        String expected = """
                paths:
                  /api/v3/pet/{id}:
                    get:
                      summary: getPet
                      responses:
                        200:
                          description: OK
                """;
        assertEquals(expected.trim(), result.trim());
    }
}
