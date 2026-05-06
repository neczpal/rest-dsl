package io.github.neczpal.restdsl.parser;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import io.github.neczpal.restdsl.model.Service;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServiceParserTest {

    @Test
    public void testServiceDefinition() {
        String input = """
                api Test {
                    paths {
                        /pet {
                            post addPet(body: Pet) {
                                response { 200 -> Pet }
                                error { 405 -> ApiResponse }
                            }
                        }
                    }
                }
        """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);
        RestDSLParser.ApiDefinitionContext apiDefinition = parser.file().apiDefinition(0);
        RestDSLParser.PathsDefinitionContext pathsDefinition = null;
        for (RestDSLParser.ApiElementContext element : apiDefinition.apiElement()) {
            if (element.pathsDefinition() != null) {
                pathsDefinition = element.pathsDefinition();
            }
        }

        List<Service> services = new ServiceParser().parse(pathsDefinition);
        assertEquals(1, services.size());
        Service service = services.getFirst();
        assertEquals("Pet", service.name());
        assertEquals("/pet", service.base());
        assertEquals(1, service.methods().size());
        assertEquals("addPet", service.methods().getFirst().name());
        assertEquals("post", service.methods().getFirst().verb());
        assertEquals("Pet", service.methods().getFirst().bodyType().name());
        assertEquals(2, service.methods().getFirst().responses().size());
        assertEquals("Pet", service.methods().getFirst().responses().get(200).name());
        assertEquals("ApiResponse", service.methods().getFirst().responses().get(405).name());
    }

    @Test
    public void testMultipleMethods() {
        String input = """
                api Test {
                    paths {
                        /pet {
                            post addPet(body: Pet) {
                                response { 200 -> Pet }
                                error { 405 -> ApiResponse }
                            }
        
                            put updatePet(body: Pet) {
                                response { 200 -> Pet }
                                error {
                                    400 -> ApiResponse
                                    404 -> ApiResponse
                                }
                            }
                        }
                    }
                }
                """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);
        RestDSLParser.ApiDefinitionContext apiDefinition = parser.file().apiDefinition(0);
        RestDSLParser.PathsDefinitionContext pathsDefinition = null;
        for (RestDSLParser.ApiElementContext element : apiDefinition.apiElement()) {
            if (element.pathsDefinition() != null) {
                pathsDefinition = element.pathsDefinition();
            }
        }

        List<Service> services = new ServiceParser().parse(pathsDefinition);
        assertEquals(1, services.size());
        Service service = services.getFirst();
        assertEquals("Pet", service.name());
        assertEquals("/pet", service.base());
        assertEquals(2, service.methods().size());

        assertEquals("addPet", service.methods().getFirst().name());
        assertEquals("post", service.methods().getFirst().verb());
        assertEquals("Pet", service.methods().getFirst().bodyType().name());
        assertEquals(2, service.methods().getFirst().responses().size());
        assertEquals("Pet", service.methods().getFirst().responses().get(200).name());
        assertEquals("ApiResponse", service.methods().getFirst().responses().get(405).name());

        assertEquals("updatePet", service.methods().get(1).name());
        assertEquals("put", service.methods().get(1).verb());
        assertEquals("Pet", service.methods().get(1).bodyType().name());
        assertEquals(3, service.methods().get(1).responses().size());
        assertEquals("Pet", service.methods().get(1).responses().get(200).name());
        assertEquals("ApiResponse", service.methods().get(1).responses().get(400).name());
        assertEquals("ApiResponse", service.methods().get(1).responses().get(404).name());
    }
}
