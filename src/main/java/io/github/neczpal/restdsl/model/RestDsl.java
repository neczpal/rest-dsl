package io.github.neczpal.restdsl.model;

import java.util.List;

public class RestDsl {
    private final Api api;
    private final List<Model> models;
    private final List<Service> services;

    public RestDsl(Api api, List<Model> models, List<Service> services) {
        this.api = api;
        this.models = models;
        this.services = services;
    }

    public Api getApi() {
        return api;
    }

    public List<Model> getModels() {
        return models;
    }

    public List<Service> getServices() {
        return services;
    }
}
