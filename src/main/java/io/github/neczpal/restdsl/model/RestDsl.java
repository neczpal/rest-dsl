package io.github.neczpal.restdsl.model;

import lombok.Builder;
import java.util.List;
import java.util.ArrayList;

@Builder
public record RestDsl(Api api, List<Model> models, List<Service> services) {
    public RestDsl {
        if (models == null) models = new ArrayList<>();
        if (services == null) services = new ArrayList<>();
    }
}
