package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Service;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceParserTest {

    @Test
    public void testServiceDefinition() {
        String input = """
                service Pet {
                    base: "/pet"

                    post addPet {
                        body: Pet
                        responses: {
                            200: Pet
                            405: "Invalid input"
                        }
                    }
                }
        """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);
        RestDSLParser.ServiceDefinitionContext serviceDefinition = parser.file().definition(0).serviceDefinition();

        Service service = new ServiceParser().parse(serviceDefinition);
        assertEquals("Pet", service.name());
        assertEquals("/pet", service.base());
        assertEquals(1, service.methods().size());
        assertEquals("addPet", service.methods().getFirst().name());
        assertEquals("post", service.methods().getFirst().verb());
        assertEquals("Pet", service.methods().getFirst().bodyType());
        assertEquals(2, service.methods().getFirst().responses().size());
        assertEquals("Pet", service.methods().getFirst().responses().get(200));
        assertEquals("\"Invalid input\"", service.methods().getFirst().responses().get(405));
    }

    @Test
    public void testMultipleMethods() {
        String input = """
                service Pet {
                    base: "/pet"

                    post addPet {
                        body: Pet
                        responses: {
                            200: Pet
                            405: "Invalid input"
                        }
                    }

                    put updatePet {
                        body: Pet
                        responses: {
                            200: Pet
                            400: "Invalid ID supplied"
                            404: "Pet not found"
                        }
                    }
                }
        """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);
        RestDSLParser.ServiceDefinitionContext serviceDefinition = parser.file().definition(0).serviceDefinition();

        Service service = new ServiceParser().parse(serviceDefinition);
        assertEquals("Pet", service.name());
        assertEquals("/pet", service.base());
        assertEquals(2, service.methods().size());

        assertEquals("addPet", service.methods().getFirst().name());
        assertEquals("post", service.methods().getFirst().verb());
        assertEquals("Pet", service.methods().getFirst().bodyType());
        assertEquals(2, service.methods().getFirst().responses().size());
        assertEquals("Pet", service.methods().getFirst().responses().get(200));
        assertEquals("\"Invalid input\"", service.methods().getFirst().responses().get(405));

        assertEquals("updatePet", service.methods().get(1).name());
        assertEquals("put", service.methods().get(1).verb());
        assertEquals("Pet", service.methods().get(1).bodyType());
        assertEquals(3, service.methods().get(1).responses().size());
        assertEquals("Pet", service.methods().get(1).responses().get(200));
        assertEquals("\"Invalid ID supplied\"", service.methods().get(1).responses().get(400));
        assertEquals("\"Pet not found\"", service.methods().get(1).responses().get(404));
    }
}