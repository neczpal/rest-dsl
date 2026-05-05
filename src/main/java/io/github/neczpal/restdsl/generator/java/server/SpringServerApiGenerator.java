package io.github.neczpal.restdsl.generator.java.server;

import io.github.neczpal.restdsl.generator.GeneratedFile;
import io.github.neczpal.restdsl.generator.Generator;
import io.github.neczpal.restdsl.generator.java.ModelGenerator;
import io.github.neczpal.restdsl.model.RestDsl;

import java.util.ArrayList;
import java.util.List;

public class SpringServerApiGenerator implements Generator {
    private final String packageName;

    public SpringServerApiGenerator(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public List<GeneratedFile> generate(RestDsl restDsl) {
        List<GeneratedFile> files = new ArrayList<>();
        ModelGenerator modelGenerator = new ModelGenerator(packageName, restDsl.traits(), restDsl.models());
        ApiGenerator apiGenerator = new ApiGenerator(packageName);
        files.addAll(modelGenerator.generate());
        files.addAll(apiGenerator.generate(restDsl.services()));
        return files;
    }
}
