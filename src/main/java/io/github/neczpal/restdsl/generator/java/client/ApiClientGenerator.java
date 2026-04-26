package io.github.neczpal.restdsl.generator.java.client;

import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.generator.Generator;
import io.github.neczpal.restdsl.generator.java.ModelGenerator;
import io.github.neczpal.restdsl.model.RestDsl;

import java.util.ArrayList;
import java.util.List;

public class ApiClientGenerator implements Generator {
    private final String packageName;

    public ApiClientGenerator(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public List<GeneratedFile> generate(RestDsl restDsl) {
        List<GeneratedFile> files = new ArrayList<>();
        ModelGenerator modelGenerator = new ModelGenerator(packageName, restDsl.models());
        ApiGenerator apiGenerator = new ApiGenerator(packageName);
        files.addAll(modelGenerator.generate());
        files.addAll(apiGenerator.generate(restDsl.services()));
        return files;
    }
}
