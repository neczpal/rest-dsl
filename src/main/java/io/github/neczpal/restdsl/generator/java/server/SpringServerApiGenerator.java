package io.github.neczpal.restdsl.generator.java.server;

import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.generator.Generator;
import io.github.neczpal.restdsl.model.RestDsl;

import java.util.ArrayList;
import java.util.List;

public class SpringServerApiGenerator implements Generator {
    private final ApiGenerator apiGenerator;
    private final ModelGenerator modelGenerator;

    public SpringServerApiGenerator() {
        this.apiGenerator = new ApiGenerator();
        this.modelGenerator = new ModelGenerator();
    }

    @Override
    public List<GeneratedFile> generate(RestDsl restDsl) {
        List<GeneratedFile> files = new ArrayList<>();
        files.addAll(modelGenerator.generate(restDsl.models()));
        files.addAll(apiGenerator.generate(restDsl.services()));
        return files;
    }
}
