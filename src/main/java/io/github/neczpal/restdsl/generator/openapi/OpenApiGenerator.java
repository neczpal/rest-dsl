package io.github.neczpal.restdsl.generator.openapi;

import io.github.neczpal.restdsl.generator.Generator;
import io.github.neczpal.restdsl.model.Api;
import io.github.neczpal.restdsl.model.Model;
import io.github.neczpal.restdsl.model.Service;

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
    public String generate(Api api, List<Model> models, List<Service> services) {
        StringBuilder sb = new StringBuilder();
        sb.append(apiGenerator.generate(api));
        sb.append(serviceGenerator.generate(services));
        sb.append(modelGenerator.generate(models));
        return sb.toString();
    }
}
