package io.github.neczpal.restdsl.generator;

import io.github.neczpal.restdsl.model.RestDsl;

import java.util.List;

public interface Generator {
    List<GeneratedFile> generate(RestDsl restDsl);
}
