package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.model.Field;
import io.github.neczpal.restdsl.model.Model;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelGeneratorTest {

    @Test
    public void testGenerate() {
        Model model = Model.builder()
                .name("User")
                .fields(Arrays.asList(
                        Field.builder().name("id").type("Int").build(),
                        Field.builder().name("name").type("String").build()
                ))
                .build();
        ModelGenerator generator = new ModelGenerator();
        String result = generator.generate(List.of(model));
        String expected = """
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
