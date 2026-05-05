package io.github.neczpal.restdsl.model;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public record Trait(String name, List<Trait> traits, List<Field> fields) {
    public Trait {
        if (traits == null) {
            traits = new ArrayList<>();
        }
        if (fields == null) {
            fields = new ArrayList<>();
        }
    }
}
