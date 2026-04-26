package io.github.neczpal.restdsl.model;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record Model(String name, Model parent, List<Field> fields) {
    public Model {
        if (fields == null) {
            fields = new ArrayList<>();
        }
    }
}
