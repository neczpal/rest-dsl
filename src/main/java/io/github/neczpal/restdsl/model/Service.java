package io.github.neczpal.restdsl.model;

import java.util.List;

public class Service {
    private String name;
    private String base;
    private List<Method> methods;

    public Service(String name, String base, List<Method> methods) {
        this.name = name;
        this.base = base;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public String getBase() {
        return base;
    }

    public List<Method> getMethods() {
        return methods;
    }
}
