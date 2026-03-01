package io.github.neczpal.restdsl.model;

import java.util.List;

public class Model {
    private String name;
    private List<Field> fields;

    public Model(String name, List<Field> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public List<Field> getFields() {
        return fields;
    }
}
