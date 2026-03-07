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
        assertEquals("Pet", service.getName());
        assertEquals("/pet", service.getBase());
        assertEquals(1, service.getMethods().size());
        assertEquals("addPet", service.getMethods().getFirst().getName());
        assertEquals("post", service.getMethods().getFirst().getVerb());
        assertEquals("Pet", service.getMethods().getFirst().getBodyType());
        assertEquals(2, service.getMethods().getFirst().getResponses().size());
        assertEquals("Pet", service.getMethods().getFirst().getResponses().get(200));
        assertEquals("\"Invalid input\"", service.getMethods().getFirst().getResponses().get(405));
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
        assertEquals("Pet", service.getName());
        assertEquals("/pet", service.getBase());
        assertEquals(2, service.getMethods().size());

        assertEquals("addPet", service.getMethods().getFirst().getName());
        assertEquals("post", service.getMethods().getFirst().getVerb());
        assertEquals("Pet", service.getMethods().getFirst().getBodyType());
        assertEquals(2, service.getMethods().getFirst().getResponses().size());
        assertEquals("Pet", service.getMethods().getFirst().getResponses().get(200));
        assertEquals("\"Invalid input\"", service.getMethods().getFirst().getResponses().get(405));

        assertEquals("updatePet", service.getMethods().get(1).getName());
        assertEquals("put", service.getMethods().get(1).getVerb());
        assertEquals("Pet", service.getMethods().get(1).getBodyType());
        assertEquals(3, service.getMethods().get(1).getResponses().size());
        assertEquals("Pet", service.getMethods().get(1).getResponses().get(200));
        assertEquals("\"Invalid ID supplied\"", service.getMethods().get(1).getResponses().get(400));
        assertEquals("\"Pet not found\"", service.getMethods().get(1).getResponses().get(404));
    }
}