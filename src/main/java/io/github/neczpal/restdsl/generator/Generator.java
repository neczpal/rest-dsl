package io.github.neczpal.restdsl.generator;

import io.github.neczpal.restdsl.model.RestDsl;

public interface Generator {
    String generate(RestDsl restDsl);
}
