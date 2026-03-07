package io.github.neczpal.restdsl.model;

import lombok.Builder;
import java.util.List;
import java.util.ArrayList;

@Builder
public record Service(String name, String base, List<Method> methods) {
    public Service {
        if (methods == null) methods = new ArrayList<>();
    }
}
