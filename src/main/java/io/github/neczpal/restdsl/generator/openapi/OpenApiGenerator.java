package io.github.neczpal.restdsl.generator.openapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.generator.Generator;
import io.github.neczpal.restdsl.model.RestDsl;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;

import java.util.List;

public class OpenApiGenerator implements Generator {
    private final MetaInfoGenerator metaInfoGenerator;
    private final ModelGenerator modelGenerator;
    private final ServiceGenerator serviceGenerator;

    public OpenApiGenerator() {
        this.metaInfoGenerator = new MetaInfoGenerator();
        this.modelGenerator = new ModelGenerator();
        this.serviceGenerator = new ServiceGenerator();
    }

    @Override
    public List<GeneratedFile> generate(RestDsl restDsl) {
        OpenAPI openAPI = new OpenAPI();
        metaInfoGenerator.generate(openAPI, restDsl.api());
        serviceGenerator.generate(openAPI, restDsl.services());
        modelGenerator.generate(openAPI, restDsl.models());

        try {
            String openapiYaml = Yaml.mapper().writeValueAsString(openAPI);
            return List.of(new GeneratedFile("openapi.yaml", openapiYaml));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize OpenAPI to YAML", e);
        }
    }
}
