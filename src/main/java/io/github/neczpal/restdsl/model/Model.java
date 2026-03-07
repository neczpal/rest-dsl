package io.github.neczpal.restdsl.model;

import lombok.Builder;
import java.util.List;
import java.util.ArrayList;

@Builder
public record Model(String name, List<Field> fields) {
    public Model {
        if (fields == null) fields = new ArrayList<>();
    }
}
