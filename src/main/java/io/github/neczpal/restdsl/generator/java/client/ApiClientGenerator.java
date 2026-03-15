package io.github.neczpal.restdsl.generator.java.client;

import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.generator.Generator;
import io.github.neczpal.restdsl.generator.java.ModelGenerator;
import io.github.neczpal.restdsl.model.RestDsl;

import java.util.ArrayList;
import java.util.List;

public class ApiClientGenerator implements Generator {
    private final ApiGenerator apiGenerator;
    private final ModelGenerator modelGenerator;

    public ApiClientGenerator(String packageName) {
        this.apiGenerator = new ApiGenerator(packageName);
        this.modelGenerator = new ModelGenerator(packageName);
    }

    @Override
    public List<GeneratedFile> generate(RestDsl restDsl) {
        List<GeneratedFile> files = new ArrayList<>();
        files.addAll(modelGenerator.generate(restDsl.models()));
        files.addAll(apiGenerator.generate(restDsl.services()));
        return files;
    }
}
