package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.generator.Generator;
import io.github.neczpal.restdsl.model.RestDsl;

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
    public String generate(RestDsl restDsl) {
        StringBuilder sb = new StringBuilder();
        sb.append(apiGenerator.generate(restDsl.api()));
        sb.append(serviceGenerator.generate(restDsl.services()));
        sb.append(modelGenerator.generate(restDsl.models()));
        return sb.toString();
    }
}
