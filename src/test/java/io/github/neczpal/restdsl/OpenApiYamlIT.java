package io.github.neczpal.restdsl;

import com.networknt.oas.OpenApiParser;
import com.networknt.oas.model.OpenApi3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenApiYamlIT {

    @TempDir
    Path tempDir;

    @Test
    public void testOpenApiGenerator() throws Exception {
        // 1. Prepare test files with the correct RDSL syntax
        Path rsdlFile = tempDir.resolve("simple.rdsl");
        String rsdlContent = """
                api Simple {
                    version: "1.0.0"
                    base: "/api"
                }
                model Greeting {
                    message: String
                }
                service GreeterService {
                    base: "/greeter"
                    get sayHello {
                        path: "/hello/{name}"
                        pathParams: {
                            name: String
                        }
                        responses: {
                            200: Greeting
                        }
                    }
                }
                """;
        Files.write(rsdlFile, rsdlContent.getBytes());

        Path openapiOutputDir = tempDir.resolve("openapi");

        // 2. Generate openapi spec
        Main.main(new String[]{"-g", "openapi", rsdlFile.toString(), openapiOutputDir.toString()});

        Path openapiFile = openapiOutputDir.resolve("openapi.yaml");
        assertTrue(Files.exists(openapiFile), "OpenAPI file not generated");

        // 3. Validate the generated OpenAPI file
        OpenApi3 openApi = (OpenApi3) new OpenApiParser().parse(openapiFile.toUri().toURL(), true);
        assertNotNull(openApi, "Parsed OpenAPI specification is null");

        assertNotNull(openApi.getPath("/greeter/hello/{name}"), "Endpoint missing");
        assertNotNull(openApi.getSchema("Greeting"), "Schema missing");
    }
}