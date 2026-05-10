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
import io.github.neczpal.restdsl.model.Trait;
import io.github.neczpal.restdsl.parser.ApiParser;
import io.github.neczpal.restdsl.parser.ModelParser;
import io.github.neczpal.restdsl.parser.ServiceParser;
import io.github.neczpal.restdsl.parser.TraitParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RestDslGenerator {

    public static void generate(String inputFile, String generatorName, String packageName, String outputDirectory) throws Exception {
        Generator generator;
        if ("openapi".equalsIgnoreCase(generatorName)) {
            generator = new OpenApiGenerator();
        } else if ("spring".equalsIgnoreCase(generatorName)) {
            generator = new SpringServerApiGenerator(packageName);
        } else if ("restclient".equalsIgnoreCase(generatorName)) {
            generator = new ApiClientGenerator(packageName);
        } else {
            throw new IllegalArgumentException("Unknown generator '" + generatorName + "'. Supported generators: openapi, spring, restclient");
        }

        CharStream input = CharStreams.fromFileName(inputFile);
        RestDSLLexer lexer = new RestDSLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        RestDSLParser.FileContext tree = parser.file();

        ApiParser apiParser = new ApiParser();
        TraitParser traitParser = new TraitParser();
        ModelParser modelParser = new ModelParser();
        ServiceParser serviceParser = new ServiceParser();

        Api api = null;
        List<Trait> traits = new ArrayList<>();
        List<Model> models = new ArrayList<>();
        List<Service> services = new ArrayList<>();

        for (RestDSLParser.ApiDefinitionContext def : tree.apiDefinition()) {
            api = apiParser.parse(def);
            for (RestDSLParser.ApiElementContext element : def.apiElement()) {
                if (element.traitsDefinition() != null) {
                    traits.addAll(traitParser.parse(element.traitsDefinition()));
                } else if (element.modelsDefinition() != null) {
                    models.addAll(modelParser.parse(element.modelsDefinition(), traits));
                } else if (element.pathsDefinition() != null) {
                    services.addAll(serviceParser.parse(element.pathsDefinition()));
                }
            }
        }

        if (api == null) {
            throw new IllegalStateException("No API definition found in " + inputFile);
        }

        RestDsl restDsl = RestDsl.builder()
                .api(api)
                .traits(traits)
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
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            Files.write(filePath, file.content().getBytes());
        }
    }
}
