package io.github.neczpal.restdsl.generator.openapi;

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
        StringBuilder sb = new StringBuilder();
        sb.append(apiGenerator.generate(restDsl.api()));
        sb.append(serviceGenerator.generate(restDsl.services()));
        sb.append(modelGenerator.generate(restDsl.models()));
        return List.of(new GeneratedFile("openapi.yaml", sb.toString()));
    }
}
