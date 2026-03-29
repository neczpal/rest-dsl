package io.github.neczpal.restdsl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {

    @TempDir
    Path tempDir;

    @Test
    public void testMain() throws IOException {
        Path inputFile = tempDir.resolve("test.rdsl");
        Path outputDir = tempDir.resolve("output");

        String rsdlContent = """
                api Petstore {
                    meta {
                        version: "1.0.0"
                        basePath: "/api/v3"
                    }
                    models {
                        User {
                            id: Int
                            name: String
                        }
                    }
                    paths {
                        /pet {
                            /:id {
                                get getPet -> User
                            }
                        }
                    }
                }
        """;
        Files.write(inputFile, rsdlContent.getBytes());

        Main.main(new String[]{"-g", "openapi", inputFile.toString(), outputDir.toString()});

        Path outputFile = outputDir.resolve("openapi.yaml");
        assertTrue(Files.exists(outputFile));
        String content = new String(Files.readAllBytes(outputFile));
        assertTrue(content.contains("openapi: 3.0.0"));
        assertTrue(content.contains("Petstore"));
        assertTrue(content.contains("User"));
        assertTrue(content.contains("getPet"));
    }

    @Test
    public void testSimpleRsdl() throws IOException {
        Path inputFile = Paths.get("src/test/resources/simple/simple.rdsl");
        Path outputDir = tempDir.resolve("simple_output");

        assertTrue(Files.exists(inputFile), "Test resource simple.rdsl not found");

        Main.main(new String[]{"-g", "openapi", inputFile.toString(), outputDir.toString()});

        Path outputFile = outputDir.resolve("openapi.yaml");
        assertTrue(Files.exists(outputFile));
        String content = new String(Files.readAllBytes(outputFile));
        assertTrue(content.contains("openapi: 3.0.0"));
        assertTrue(content.contains("Petstore"));
        assertTrue(content.contains("Pet"));
        assertTrue(content.contains("Category"));
        assertTrue(content.contains("Order"));
        assertTrue(content.contains("User"));
        assertTrue(content.contains("ApiResponse"));
        assertTrue(content.contains("/pet"));
        assertTrue(content.contains("updatePet"));
        assertTrue(content.contains("findByStatus"));
    }
}