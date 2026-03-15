package io.github.neczpal.restdsl;

import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.generator.Generator;
import io.github.neczpal.restdsl.generator.java.client.ApiClientGenerator;
import io.github.neczpal.restdsl.generator.java.server.SpringServerApiGenerator;
import io.github.neczpal.restdsl.generator.openapi.OpenApiGenerator;
import io.github.neczpal.restdsl.model.Api;
import io.github.neczpal.restdsl.model.Model;
import io.github.neczpal.restdsl.model.RestDsl;
import io.github.neczpal.restdsl.model.Service;
import io.github.neczpal.restdsl.parser.ApiParser;
import io.github.neczpal.restdsl.parser.ModelParser;
import io.github.neczpal.restdsl.parser.ServiceParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String generatorName = null;
        String inputFile = null;
        String outputDirectory = ".";
        String packageName = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-g":
                    if (i + 1 < args.length) {
                        generatorName = args[++i];
                    }
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        packageName = args[++i];
                    }
                    break;
                default:
                    if (inputFile == null) {
                        inputFile = args[i];
                    } else {
                        outputDirectory = args[i];
                    }
                    break;
            }
        }

        if (generatorName == null || inputFile == null) {
            System.out.println("Usage: java -jar rest-dsl.jar -g <generator-name> [-p <package-name>] <input-file.rsdl> [output-directory]");
            return;
        }

        Generator generator;
        if ("openapi".equalsIgnoreCase(generatorName)) {
            generator = new OpenApiGenerator();
        } else if ("spring".equalsIgnoreCase(generatorName)) {
            generator = new SpringServerApiGenerator(packageName);
        } else if ("restclient".equalsIgnoreCase(generatorName)) {
            generator = new ApiClientGenerator(packageName);
        } else {
            System.err.println("Error: Unknown generator '" + generatorName + "'. Supported generators: openapi, spring, restclient");
            return;
        }

        try {
            CharStream input = CharStreams.fromFileName(inputFile);
            RestDSLLexer lexer = new RestDSLLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            RestDSLParser parser = new RestDSLParser(tokens);

            RestDSLParser.FileContext tree = parser.file();

            ApiParser apiParser = new ApiParser();
            ModelParser modelParser = new ModelParser();
            ServiceParser serviceParser = new ServiceParser();

            Api api = null;
            List<Model> models = new ArrayList<>();
            List<Service> services = new ArrayList<>();

            for (RestDSLParser.DefinitionContext def : tree.definition()) {
                if (def.apiDefinition() != null) {
                    api = apiParser.parse(def.apiDefinition());
                } else if (def.modelDefinition() != null) {
                    models.add(modelParser.parse(def.modelDefinition()));
                } else if (def.serviceDefinition() != null) {
                    services.add(serviceParser.parse(def.serviceDefinition()));
                }
            }

            if (api == null) {
                System.err.println("Error: No API definition found.");
                return;
            }

            RestDsl restDsl = RestDsl.builder()
                    .api(api)
                    .models(models)
                    .services(services)
                    .build();
            List<GeneratedFile> generatedFiles = generator.generate(restDsl);

            Path outputRoot = Paths.get(outputDirectory);
            if (!Files.exists(outputRoot)) {
                Files.createDirectories(outputRoot);
            }

            if (packageName != null) {
                Path packagePath = outputRoot.resolve(packageName.replace('.', '/'));
                if (!Files.exists(packagePath)) {
                    Files.createDirectories(packagePath);
                }
            }

            for (GeneratedFile file : generatedFiles) {
                Path filePath = outputRoot.resolve(file.name());
                Files.write(filePath, file.content().getBytes());
                System.out.println("Generated: " + filePath);
            }

            System.out.println("Successfully generated " + generatorName + " spec to " + outputDirectory);

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}
