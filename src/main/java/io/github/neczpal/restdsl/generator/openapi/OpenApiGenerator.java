package io.github.neczpal.restdsl.generator.openapi;

import com.amihaiemil.eoyaml.YamlMappingBuilder;
import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.generator.Generator;
import io.github.neczpal.restdsl.model.RestDsl;

import java.util.List;

public class OpenApiGenerator implements Generator {
    private final ApiGenerator apiGenerator;
    private final ModelGenerator modelGenerator;
    private final ServiceGenerator serviceGenerator;

    public OpenApiGenerator() {
        this.apiGenerator = new ApiGenerator();
        this.modelGenerator = new ModelGenerator();
        this.serviceGenerator = new ServiceGenerator();
    }

    @Override
    public List<GeneratedFile> generate(RestDsl restDsl) {
        YamlMappingBuilder openapiBuilder = apiGenerator.generate(restDsl.api());
        openapiBuilder = serviceGenerator.generate(openapiBuilder, restDsl.services());
        openapiBuilder = modelGenerator.generate(openapiBuilder, restDsl.models());

        return List.of(new GeneratedFile("openapi.yaml", openapiBuilder.build().toString()));
    }
}
